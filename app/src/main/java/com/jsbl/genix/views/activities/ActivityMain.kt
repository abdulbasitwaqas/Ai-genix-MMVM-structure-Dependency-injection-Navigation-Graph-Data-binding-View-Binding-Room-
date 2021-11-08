package com.jsbl.genix.views.activities

import android.Manifest
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fondesa.kpermissions.allDenied
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyDenied
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.ContextUtils.getActivity
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityMainBinding
import com.jsbl.genix.databinding.AltNavHeaderHomeBinding
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.extensions.showConfirmationDialog
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.utils.services.*
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.fragments.DetailFragment
import com.scope.mhub.utils.TripManagementHelper
import kotlinx.android.synthetic.main.activity_main.*


class ActivityMain : DrawerLayout.DrawerListener,
    BaseActivity<MainHomeViewModel, ActivityMainBinding>(MainHomeViewModel::class.java) {

    private lateinit var navController: NavController
    private lateinit var header: View
    private lateinit var selectedMenuItem: MenuItem

    private lateinit var _headerBinding: AltNavHeaderHomeBinding
    private var onDash = true
    private lateinit var serviceIntent: Intent
    private lateinit var timer: CountDownTimer
    private lateinit var customerX: CustomerX

    var sharedEditor: SharedPreferences.Editor? = null
    private var dialog: Dialog? = null

    companion object {
        const val REQUEST_CODE_FINE_LOCATION = 99
        const val REQUEST_BACKGROUNDlOCATION = 110
        const val REQUEST_CODE_CAMERA = 98
        const val REQUEST_CODE_OTP = 97
        const val REQUEST_REQ_PERMISSIOM = 102
        const val REQUEST_SMS = 101
        const val REQUEST_CAM = 108
        const val REQUEST_LOC = 103
        const val REQUEST_COARSE_LOC = 104
        const val REQUEST_READ_ST = 105
        const val REQUEST_WRITE_ST = 106
        const val REQUEST_READ_PHONE_STATE = 107
        const val REQUEST_READ_PHONE_STATE_FP = 1552
        const val REQUEST_READ_PHONE_STATE_REG = 1111
        const val REQUEST_ACTIVITY_RECOGNITION = 109
        const val PERMISSIONS_ALLOWED = 10111
    }

    private var referenceCode: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedEditor = SharePreferencesHelper.prefs!!.edit()

/*
        if (checkAllPermissionReg(this)){
            initMembers()
        } else {}*/

        getPermissions()


    }

    fun initMembers() {
        viewModel.fromSplash =
            intent.getBooleanExtra(ActivityRegistration.INTENT_SPLASH_FLOW, false)!!
        viewModel.showReferanceDialog = intent.getBooleanExtra("show_referance_dialog", false)!!


        logD("***reference","reference :::   ${viewModel.showReferanceDialog}")
        if (/*isItFirestTime() && */viewModel.showReferanceDialog) {
            dialog = Dialog(this)
            dialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog!!.getWindow()?.setGravity(Gravity.CENTER)
            dialog!!.setCancelable(false)
            dialog!!.setContentView(R.layout.reference_code_layout)

            val addReferenceCodeBtn: Button =
                dialog!!.findViewById<Button>(R.id.addReferenceCodeBtn)
            val skipReferenceCodeBtn: Button =
                dialog!!.findViewById<Button>(R.id.skipReferenceCodeBtn)
            val referenceCodeET: EditText =
                dialog!!.findViewById<EditText>(R.id.referenceCodeET)
            addReferenceCodeBtn.isEnabled = false

            referenceCodeET.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (referenceCodeET.length() == 6) {
                        addReferenceCodeBtn.isEnabled = true
                        addReferenceCodeBtn.setBackgroundResource(R.drawable.bg_login_next)
                        referenceCode = referenceCodeET.text.toString()
                    } else {
                        addReferenceCodeBtn.isEnabled = false
                        addReferenceCodeBtn.setBackgroundResource(R.drawable.invisible_btn)
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })


            addReferenceCodeBtn.setOnClickListener {

//                showShort(requireContext(),"Click")
                showPDialog()
                viewModel.isVerifyCode = true
                viewModel.shareWithFriend(referenceCode)

            }
            skipReferenceCodeBtn.setOnClickListener { dialog!!.dismiss() }


            dialog!!.show()
        }


        binding.onClickListener = this
        binding.title = "Title"

        timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                TripManagementHelper.getInstance(this@ActivityMain).startHelper()
            }
        }


        //TODO
        navController =
            Navigation.findNavController(this, R.id.fragmentNavHos)
        observeDetails()
        //Pass the ID's of Different destinations
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.rewards2,
            R.id.dashBoard3,
            R.id.redeemFragmentDirections
        )
            .build()

//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }
            binding.title = try {
                destination.label as String?
            } catch (e: Resources.NotFoundException) {
                "Title"
            }

            if (destination.id == R.id.dashBoard4) {
//                binding.toolbarCustom.drawerButton.setImageResource(R.drawable.alt_menuicon)
                onDash = true
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                viewModel.fetchFromDatabase()
            } else {
//                binding.toolbarCustom.drawerButton.setImageResource(ic_baseline_arrow_back_ios)
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                onDash = false
            }

//            showShort(this@MainActivity, "Navigated to $dest")
        }

//        toggleService(this@Login, viewModel.prefsHelper)
//        apiServices = ScopeRetrofitClient.apiScopeServices(this)

        setNavHeader()
        if (viewModel.fromSplash) {
            viewModel.fetchDropDown()
        } else {
            viewModel.getDropDown()

        }


    }

    /*fun setTripListeners(start: Boolean = false) {
        if (start) {
            if (SCPSmartDriveManager.getInstance(this@ActivityMain).activeTripInProgress()) {
                TripManagementHelper.getInstance(context).registerTripEventReceiver()
            }

        }
    }*/

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    fun observeDetails() {
        viewModel.customer.observe(this, Observer {
            it?.let {
                customerX = it
                _headerBinding.customer = it
                if (it.name != null) {
                    if (it.name!!.isNotEmpty()) {
                        _headerBinding.drawerName.text = it.name

                    }
                }
                if (it.birthPlace != null) {
                    if (it.birthPlace!!.isNotEmpty()) {
                        _headerBinding.tvNavLocation.text = it.birthPlace

                    }
                }


//                actionForService(this, start = true)
                timer.start()
            }
        })
    }

    fun setNavHeader() {


        /*  header = binding.navView.getHeaderView(0)
          header!!.setOnClickListener({
  //            showShort(this@MainActivity, "Clicked")
              navController.navigate(R.id.ProfileManagement)
              binding.drawerLayout.closeDrawer(GravityCompat.START)
          })
  */
        _headerBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.alt_nav_header_home,
            binding.navView,
            false
        )
        _headerBinding.onClickListener = object : OnViewClickListener {
            override fun onClick(view: View, obj: Any) {
                when (view.id) {
                    R.id.drawerProfileImage -> {
                        navController.navigate(R.id.ProfileManagement)
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    R.id.drawerName -> {
                        navController.navigate(R.id.ProfileManagement)
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    R.id.tvNavLocation -> {
                        navController.navigate(R.id.ProfileManagement)
                        binding.drawerLayout.closeDrawer(GravityCompat.START)

                    }
                }
            }
        }
        binding.navView.addHeaderView(_headerBinding.getRoot())



        binding.navView.setNavigationItemSelectedListener { menuItem ->
            val id: Int = menuItem.itemId
            //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
            when (id) {
                R.id.trips -> {
                    navController.navigate(R.id.myTrips)
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    selectedMenuItem = menuItem
                }
                R.id.games -> {
                    navController.navigate(R.id.gamificationFragment)
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    selectedMenuItem = menuItem
                }
                R.id.shareWithFriends -> {
//                    shareLink()
                    navController.navigate(R.id.shareWithFriendsFragment)
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    selectedMenuItem = menuItem
                }
                R.id.profileSettings -> {
                    navController.navigate(R.id.ProfileManagement)
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    selectedMenuItem = menuItem
                }
                R.id.help -> {
                    navController.navigate(R.id.learnMore)
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    selectedMenuItem = menuItem
                }

                R.id.feedBack -> {
                    navController.navigate(R.id.feedback)
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    selectedMenuItem = menuItem
                }
                R.id.logout -> {
                    showConfirmationDialog(
                        this,
                        title = "Logout",
                        msg = "Are you sure, you want to logout?",
                        onPositiveClick = {
                            viewModel.logout()
                            //                actionOnService(this@ActivityMain, Actions.STOP, viewModel.prefsHelper)
                            //                SCPSmartDriveManager.getInstance(this).logout()

                            logoutExplicit(this@ActivityMain)
//                            viewModel.logout()
                        }
                    )


                }
            }
            //This is for maintaining the behavior of the Navigation view

            //This is for closing the drawer after acting on it
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    fun shareLink() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                
                
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed();
        }
    }

    fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.SEND_SMS
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Send Sms Permission")
                    .setMessage("This Application Requires Access to send SMS")
                    .setPositiveButton("Ask me") { dialog, which ->
                        requestSmsPermission()
                    }.setNegativeButton("No") { dialog, which ->
                        notifyDetailFragment(false)
                    }
                    .show()
            } else {
                requestSmsPermission()
            }


        } else {
            notifyDetailFragment(true)
        }
    }

    fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.SEND_SMS),
            REQUEST_CODE_SEND_SMS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyDetailFragment(true)
                } else {
                    notifyDetailFragment(false)

                }
            }
            REQUEST_REQ_PERMISSIOM -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyDetailFragment(true)
                } else {
                    notifyDetailFragment(false)

                }
            }


            ActivityMain.REQUEST_CODE_FINE_LOCATION -> {
                if (checkAllGranted(grantResults)) {

                    // permission was granted
//                    toggleService(this@MainActivity, viewModel.prefsHelper)

//                    actionOnService(this@ActivityMain, Actions.START, viewModel.prefsHelper)
//                    actionForService(this, start = true)
                    timer.start()


                }
            }
        }

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


    private fun checkAllGranted(grantResults: IntArray): Boolean {
        var granted = true
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                granted = false
                break
            }
        }
        return granted
    }

    fun notifyDetailFragment(permissionGranted: Boolean) {

        val activeFragment = fragmentNavHos.childFragmentManager.primaryNavigationFragment
        if (activeFragment is DetailFragment) {
            activeFragment.onPermissionResult(permissionGranted)
        }
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.drawerButton -> {
                checkDrawer()
            }
        }

    }

    override fun onLoading(obj: RequestHandler) {
    }

    override fun onSuccess(obj: RequestHandler) {
        if (obj.any is ResponseFillDropDown) {
            val rr = obj.any as ResponseFillDropDown
            if (rr.makers.isNullOrEmpty() || rr.colors.isNullOrEmpty() || rr.manufacturers.isNullOrEmpty() || rr.motorTypes.isNullOrEmpty() || rr.notInsuredReasons.isNullOrEmpty() /*|| rr.interests.isNullOrEmpty()*/) {
                viewModel.getDropDown()
            }
        } else if (viewModel.isVerifyCode) {
            dialog!!.dismiss()
            if (obj.any is String)
//                showShort(this, obj.any.toString())
                showOnlyAlertMessage(this, msg = obj.any.toString()) {
                }
        }
    }

    override fun onError(obj: RequestHandler) {
    }

    fun checkDrawer() {
        if (onDash) {
            openDrawer()
        } else {
            onBackPressed()
        }
    }

    fun openDrawer() {
        observeDetails()
        viewModel.fetchFromDatabase()
        if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                invalidateOptionsMenu()


            }, 300)


        } else {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            invalidateOptionsMenu()
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun initViewModel(viewModel: MainHomeViewModel) {
    }


    fun getPermissions() {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {

            permissionsBuilder(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACTIVITY_RECOGNITION
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION

            ).build().send { result ->
                // Handle the result, for example check if all the requested permissions are granted.
                if (result.allGranted()) {
                    initMembers()

                } else if (result.allDenied()) {

                    // show dialogue and on ok button call this method again, this will handle denied settings case ase wel
                    showConfirmationDialog(
                        this,
                        title = "Permissions Required",
                        msg = "All permissions must be granted to proceed further. If you DENY these permissions, then you will not be able to use this app further.",
                        onPositiveClick = {
                            showShort(this, resources.getString(R.string.allow_all_permissions))
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, PERMISSIONS_ALLOWED)
                        },
                        onNegativeClick = {
                            viewModel.logout()
                            val intentM = Intent(this, ActivityWelcome::class.java)
                            startActivity(intentM)
                            finishAffinity()
                        }
                    )
                } else if (result.anyDenied()) {
                    // show dialogue and on ok button call this method again, this will handle denied settings case ase wel
                    showConfirmationDialog(
                        this,
                        title = "Permissions Required",
                        msg = "All permissions must be granted to proceed further. If you DENY these permissions, then you will not be able to use this app further.",
                        onPositiveClick = {
                            showShort(this, resources.getString(R.string.allow_all_permissions))
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, PERMISSIONS_ALLOWED)
                        },
                        onNegativeClick = {
                            viewModel.logout()
                            val intentM = Intent(this, ActivityWelcome::class.java)
                            startActivity(intentM)
                            finishAffinity()
                        }
                    )
                } else {
                    getPermissions()
                }
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

            permissionsBuilder(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
            ).build().send { result ->
                // Handle the result, for example check if all the requested permissions are granted.
                if (result.allGranted()) {
                    initMembers()

                } else if (result.allDenied()) {

                    // show dialogue and on ok button call this method again, this will handle denied settings case ase wel
                    showConfirmationDialog(
                        this,
                        title = "Permissions Required",
                        msg = "All permissions must be granted to proceed further. If you DENY these permissions, then you will not be able to use this app further.",
                        onPositiveClick = {
                            showShort(this, resources.getString(R.string.allow_all_permissions))
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, PERMISSIONS_ALLOWED)
                        },
                        onNegativeClick = {
                            viewModel.logout()
                            val intentM = Intent(this, ActivityWelcome::class.java)
                            startActivity(intentM)
                            finishAffinity()
                        }
                    )
                } else if (result.anyDenied()) {
                    // show dialogue and on ok button call this method again, this will handle denied settings case ase wel
                    showConfirmationDialog(
                        this,
                        title = "Permissions Required",
                        msg = "All permissions must be granted to proceed further. If you DENY these permissions, then you will not be able to use this app further.",
                        onPositiveClick = {
                            showShort(this, resources.getString(R.string.allow_all_permissions))
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, PERMISSIONS_ALLOWED)
                        },
                        onNegativeClick = {
                            viewModel.logout()
                            val intentM = Intent(this, ActivityWelcome::class.java)
                            startActivity(intentM)
                            finishAffinity()
                        }
                    )
                } else {
                    getPermissions()
                }
            }
        } else {

            permissionsBuilder(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
            ).build().send { result ->
                // Handle the result, for example check if all the requested permissions are granted.
                if (result.allGranted()) {
                    // All the permissions are granted.
                    initMembers()

                } else if (result.allDenied()) {
                    // show dialogue and on ok button call this method again, this will handle denied settings case ase wel
                    showConfirmationDialog(
                        this,
                        title = "Permissions Required",
                        msg = "All permissions must be granted to proceed further. If you DENY these permissions, then you will not be able to use this app further.",
                        onPositiveClick = {
                            showShort(this, resources.getString(R.string.allow_all_permissions))
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, PERMISSIONS_ALLOWED)
                        },
                        onNegativeClick = {
                            viewModel.logout()
                            val intentM = Intent(this, ActivityWelcome::class.java)
                            startActivity(intentM)
                            finishAffinity()
                        }
                    )

                } else if (result.anyDenied()) {
                    // show dialogue and on ok button call this method again, this will handle denied settings case ase wel
                    showConfirmationDialog(
                        this,
                        title = "Permissions Required",
                        msg = "All permissions must be granted to proceed further. If you DENY these permissions, then you will not be able to use this app further.",
                        onPositiveClick = {
                            showShort(this, resources.getString(R.string.allow_all_permissions))
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, PERMISSIONS_ALLOWED)
                        },
                        onNegativeClick = {
                            viewModel.logout()
                            val intentM = Intent(this, ActivityWelcome::class.java)
                            startActivity(intentM)
                            finishAffinity()
                        }
                    )
                } else {
                    getPermissions()
                }
            }


        }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        logD("**drawerState", "Drawer SLIDE")
        observeDetails()
        viewModel.fetchFromDatabase()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            invalidateOptionsMenu()
        }, 300)
    }

    override fun onDrawerOpened(drawerView: View) {
        observeDetails()
        viewModel.fetchFromDatabase()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            invalidateOptionsMenu()
        }, 300)
        logD("**drawerState", "Drawer OPEN")
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerStateChanged(newState: Int) {
        logD("**drawerState", "Drawer STATE")
        observeDetails()
        viewModel.fetchFromDatabase()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            invalidateOptionsMenu()
        }, 300)
    }


    fun isItFirestTime(): Boolean {
        return if (SharePreferencesHelper.prefs!!.getBoolean("firstTime", true)) {
            sharedEditor!!.putBoolean("firstTime", false)
            sharedEditor!!.commit()
            sharedEditor!!.apply()
            true
        } else {
            false
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSIONS_ALLOWED) {
            getPermissions()
        }
    }


}
