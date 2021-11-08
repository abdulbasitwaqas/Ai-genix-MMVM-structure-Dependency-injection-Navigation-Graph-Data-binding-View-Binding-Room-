package com.jsbl.genix.views.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jsbl.genix.R
import com.jsbl.genix.StatsModel
import com.jsbl.genix.databinding.AltFragmentDashboard4Binding
import com.jsbl.genix.model.GetStatsFeedBackModel
import com.jsbl.genix.model.profileManagement.Color
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import com.jsbl.genix.viewModel.MainHomeViewModel
import com.jsbl.genix.views.activities.ActCaptureCamera
import com.jsbl.genix.views.activities.ActivityMain
import com.jsbl.genix.views.activities.ActivityRegistration
import android.net.NetworkInfo

import android.net.ConnectivityManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.*
import androidx.core.view.isVisible
import com.jsbl.genix.Logs.sendLogFiles
import com.jsbl.genix.utils.SharePreferencesHelper.Companion.sharedEditor
import com.jsbl.genix.utils.services.actionForService
import com.scope.smartdrivedemo.AbsActivity
import com.scope.smartdrivedemo.FileLoggingTree
import timber.log.Timber
import java.io.File
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [DashBoard4.newInstance] factory method to
 * create an instance of this fragment.
 */
//Current Home Fragment Nav Host
class DashBoard4 :/*PopupMenu.OnMenuItemClickListener ,*/
    BaseFragment<MainHomeViewModel, AltFragmentDashboard4Binding>(
        MainHomeViewModel::class.java
    ) {
    private lateinit var displaymetrics: DisplayMetrics
    var height: Int = 0
    var width: Int = 0
    private var customerX = CustomerX()
    var selectedItem = -1
    private val statsTimeList = arrayOf(
        "Last Week",
        "One Month",
        "One Year"
        /*,
        "Print Logs"
        */
    )
    var stat_st = ""
    private var statsAdapter: ArrayAdapter<String>? = null

    //        yearAdapter = new ArrayAdapter<>(context, R.layout.main_spinner_layout, year);
    private var dialog: Dialog? = null
    private var referenceCode: String = ""

    private lateinit var settingSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    companion object {
        const val INTENT_FILTER_TRIP = "filterTrip"
        const val FILTER_ACCELERATION = 1
        const val FILTER_SPEEDING = 2
        const val FILTER_CORNERING = 3
        const val FILTER_BRAKING = 4
        const val FILTER_TIME_OF_THE_DAY = 5
        const val FILTER_TOTAL_DISTANCE = 6


    }

    var sharedEditor: SharedPreferences.Editor? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        sharedEditor = SharePreferencesHelper.prefs!!.edit()
//        if (isNetworkAvailable(requireContext())){


        initMembers()
        binding.printLogsBtn.setOnClickListener {
            sendLogFiles(requireActivity())
        }
        FileLoggingTree.checkForObsoleteLogFiles(requireContext().applicationContext)


//        }else{
//            showShort(requireContext(), "" + resources.getString(R.string.internt_problem))
//        }


    }

    override fun onStart() {
        super.onStart()
        observeDetails()

        Timber.i("Opening ${this.javaClass.simpleName}")
        actionForService(requireActivity(), start = true)
    }

    private fun initMembers() {

//        observeDetails()
        displaymetrics = resources.displayMetrics
        height = displaymetrics.heightPixels
        width = displaymetrics.widthPixels
        binding.constraintBody.layoutParams.height = height
//        setRequestHandler()
        binding.onClickListener = this
        //TODO
        viewModel.fetchFromDatabase()
        setSettingFragment()

        val popupMenu: PopupMenu = PopupMenu(context, binding.statsIV)

        popupMenu.inflate(R.menu.main_menu)



        binding.statsIV.setOnClickListener(View.OnClickListener { popupMenu.show() })

        statsAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item_layout,
            statsTimeList
        )

        binding.statsSpinner.setAdapter(statsAdapter)
        binding.statsSpinner.setSelection(selectedItem)
        binding.statsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                stat_st = binding.statsSpinner.getSelectedItem().toString()
                selectedItem = position

                if (stat_st == "Last Week") {
//                    binding.progress.visibility = View.VISIBLE
                    showPDialog()
                    binding.dashBoard4CL.isEnabled = false
                    observeDetails()

                } else if (stat_st == "One Month") {
//                    binding.progress.visibility = View.VISIBLE
                    showPDialog()
                    binding.dashBoard4CL.isEnabled = false
                    viewModel.getStats(
                        "" + customerX!!.scopeToken,
                        "" + customerX.carDetails!![SharePreferencesHelper.invoke(requireContext())
                            .getDefaultCarPos()].registrationNo,
                        "Month", "" + customerX!!.iD
                    )
                    viewModel.getStatsFeedBack(
                        "" + customerX!!.scopeToken,
                        "" + customerX.carDetails!![SharePreferencesHelper.invoke(requireContext())
                            .getDefaultCarPos()].registrationNo,
                        "Month", "" + customerX!!.iD
                    )

                } else if (stat_st == "One Year") {
//                    binding.progress.visibility = View.VISIBLE
                    showPDialog()
                    binding.dashBoard4CL.isEnabled = false
                    viewModel.getStats(
                        "" + customerX!!.scopeToken,
                        "" + customerX.carDetails!![SharePreferencesHelper.invoke(requireContext())
                            .getDefaultCarPos()].registrationNo,
                        "Year", "" + customerX!!.iD
                    )

                    viewModel.getStatsFeedBack(
                        "" + customerX!!.scopeToken,
                        "" + customerX.carDetails!![SharePreferencesHelper.invoke(requireContext())
                            .getDefaultCarPos()].registrationNo,
                        "Year", "" + customerX!!.iD
                    )

                }/*else if (stat_st.contains("Print Logs")) {

                }*/

            }

        }


    }


    fun setSettingFragment() {
        /* settingSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
         */
        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        /*
        settingSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })*/
        val bottomSheetList = BottomSheetList()
        val ft: FragmentTransaction = childFragmentManager.beginTransaction()
        ft.add(R.id.bottomSheet, bottomSheetList)
        ft.commit()
    }

    fun setAccountProgress(value: Int) {
        var percentage = value
        if (percentage >= 100) {
            percentage = 100
//            binding.completeProfileLabel.gone()
        } else if (percentage <= 0) {
            percentage = 0
        } else if (percentage == 60) {
            percentage = 50
        } else if (percentage == 80) {
            percentage = 75
        }
        binding.circularProgressBar.progress = percentage.toFloat()
        /* val params =
             binding.accountProgressLayout.ivDropdown.getLayoutParams() as ConstraintLayout.LayoutParams
         params.horizontalBias =
             percentage.toFloat() / 100f // here is one modification for example. modify anything else you want :)
         binding.accountProgressLayout.ivDropdown.setLayoutParams(params)
         binding.accountProgressLayout.tvProgressDigits.text = "$percentage%"*/
    }


    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.customer = it
                customerX = it

                logD("**id", "token:   ${customerX!!.scopeToken}")


                viewModel.getStatsFeedBack(
                    "" + customerX!!.scopeToken,
                    "" + customerX.carDetails!![SharePreferencesHelper.invoke(requireContext())
                        .getDefaultCarPos()].registrationNo,
                    "Week", "" + customerX!!.iD
                )


                viewModel.getStats(
                    "" + customerX!!.scopeToken,
                    "" + customerX.carDetails!![SharePreferencesHelper.invoke(requireContext())
                        .getDefaultCarPos()].registrationNo,
                    "Week", "" + customerX!!.iD
                )

                if (it.name != null) {
                    if (it.name!!.isNotEmpty()) {
                        binding.name.text = it.name

                    }
                }
                if (it.birthPlace != null) {
                    if (it.birthPlace!!.isNotEmpty()) {
                        binding.city.text = it.birthPlace

                    }
                }
                logD("**percentage", " dash board ::    ${it.percentage}")
                setAccountProgress(getProfilePercent(it))
                   if (it.percentage!! > 60) {
                       binding.circularProgressBar.progressBarColor = resources.getColor(R.color.progress_green)

                   } else {
                       binding.circularProgressBar.progressBarColor = resources.getColor(R.color.progress_yellow)
                   }


            }
        })
    }


    fun showPopupMenu(view: View) {
        val popup = PopupMenu(context, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.main_menu, popup.menu)
        popup.show()
    }


    override fun onClick(view: View, obj: Any) {
        when (view.id) {

            R.id.speedConstraint -> {
                val bundle = Bundle()
                bundle.putInt(INTENT_FILTER_TRIP, FILTER_SPEEDING)
//        navController.setGraph(R.navigation.<you_nav_graph_xml>, bundle)
                Navigation.findNavController(requireActivity(), R.id.fragmentNavHos)
                    .navigate(R.id.myTrips, bundle)
            }
            R.id.corneringConstraint -> {
                val bundle = Bundle()
                bundle.putInt(INTENT_FILTER_TRIP, FILTER_CORNERING)
//        navController.setGraph(R.navigation.<you_nav_graph_xml>, bundle)
                Navigation.findNavController(requireActivity(), R.id.fragmentNavHos)
                    .navigate(R.id.myTrips, bundle)
            }
            R.id.brakingConstraint -> {
                val bundle = Bundle()
                bundle.putInt(INTENT_FILTER_TRIP, FILTER_BRAKING)
//        navController.setGraph(R.navigation.<you_nav_graph_xml>, bundle)
                Navigation.findNavController(requireActivity(), R.id.fragmentNavHos)
                    .navigate(R.id.myTrips, bundle)
            }
            R.id.accelerationConstraint -> {
                val bundle = Bundle()
                bundle.putInt(INTENT_FILTER_TRIP, FILTER_ACCELERATION)
//        navController.setGraph(R.navigation.<you_nav_graph_xml>, bundle)
                Navigation.findNavController(requireActivity(), R.id.fragmentNavHos)
                    .navigate(R.id.myTrips, bundle)
            }
            R.id.timeOfDayConstraint -> {
                val bundle = Bundle()
                bundle.putInt(INTENT_FILTER_TRIP, FILTER_TIME_OF_THE_DAY)
//        navController.setGraph(R.navigation.<you_nav_graph_xml>, bundle)
                Navigation.findNavController(requireActivity(), R.id.fragmentNavHos)
                    .navigate(R.id.myTrips, bundle)
            }
            R.id.totalDistanceConstraint -> {
                val bundle = Bundle()
                bundle.putInt(INTENT_FILTER_TRIP, FILTER_TOTAL_DISTANCE)
//        navController.setGraph(R.navigation.<you_nav_graph_xml>, bundle)
                Navigation.findNavController(requireActivity(), R.id.fragmentNavHos)
                    .navigate(R.id.myTrips, bundle)
            }
            R.id.drawerIcon -> {
                (requireActivity() as ActivityMain).checkDrawer()
            }
            /*  R.id.statsIV -> {
                  showPopupMenu()
              }*/
            R.id.ivProfile -> {
                startActivityForResult(
                    Intent(context, ActCaptureCamera::class.java),
                    ActivityRegistration.REQUEST_CODE_PROFILE
                )
//                    viewModel.registerEntry(binding.edMessage.text.toString().trim())
            }
        }
    }

    override fun onLoading(obj: RequestHandler) {

    }

    override fun onSuccess(obj: RequestHandler) {
        dismissDialog()
//        binding.progress.visibility = View.GONE

        binding.dashBoard4CL.isEnabled = true
        if (obj.any is StatsModel) {
            val rr = obj.any as StatsModel
            if (rr != null) {
                SharePreferencesHelper.invoke(requireContext()).saveStatsModel(rr)

                logD("**statsRes", "" + rr!!)

                binding.tvTotalScore.text = "" + rr!!.score?.toInt()
                var points: Int = rr!!.totalPoints.toInt() - rr!!.reedeemPoints.toInt()
                binding.tvPoints.text = points.toString()
            } else {
                SharePreferencesHelper.invoke(requireContext()).saveStatsModel(StatsModel())
            }


        } else if (obj.any is GetStatsFeedBackModel) {
            val rr = obj.any as GetStatsFeedBackModel

            rr?.let {


//                binding.corneringConstraint.detailColor = android.graphics.Color.WHITE

                if (!it.items.isNullOrEmpty() && it.items.size > 1) {

                    //cornering
                    val corneringMax = it.items!![2].rangeMax
                    binding.corneringConstraint.circularProgressBar.progressMax =
                        corneringMax!!.toFloat()
                    binding.corneringConstraint.detailValue = it.items!![2].value.toString()
                    binding.corneringConstraint.circularProgressBar.progress =
                        it.items!![2].value!!.toFloat()
                    if (it.items!![2].value!! >= 0.71) {
                        binding.corneringConstraint.detailColor = android.graphics.Color.RED
                    } else if (it.items!![2].value!! <= 0.70 && it.items!![2].value!! >= 0.41) {
                        binding.corneringConstraint.detailColor = android.graphics.Color.YELLOW
                    } else if (it.items!![2].value!! <= 0.40 && it.items!![2].value!! >= 0.0) {
                        binding.corneringConstraint.detailColor = android.graphics.Color.GREEN
                    }

                    //for distnace
                    val distanceMax = it.items!![3].rangeMax
                    binding.totalDistanceConstraint.circularProgressBar.progressMax =
                        distanceMax!!.toFloat()
                    binding.totalDistanceConstraint.detailValue = it.items!![3].value.toString()
                    binding.totalDistanceConstraint.circularProgressBar.progress =
                        it.items!![3].value!!.toFloat()
                    if (it.items!![3].value!! >= 600) {
                        binding.totalDistanceConstraint.detailColor = android.graphics.Color.GREEN
                    } else if (it.items!![3].value!! <= 599 && it.items!![3].value!! >= 300) {
                        binding.totalDistanceConstraint.detailColor = android.graphics.Color.GREEN
                    } else if (it.items!![3].value!! <= 299 && it.items!![3].value!! >= 0) {
                        binding.totalDistanceConstraint.detailColor = android.graphics.Color.GREEN
                    }
                    //for braking
                    val brakingMax = it.items!![1].rangeMax
                    binding.brakingConstraint.circularProgressBar.progressMax =
                        brakingMax!!.toFloat()
                    binding.brakingConstraint.detailValue = it.items!![1].value.toString()
                    binding.brakingConstraint.circularProgressBar.progress =
                        it.items!![1].value!!.toFloat()
                    if (it.items!![1].value!! >= 0.71) {
                        binding.brakingConstraint.detailColor = android.graphics.Color.RED
                    } else if (it.items!![1].value!! <= 0.70 && it.items!![1].value!! >= 0.41) {
                        binding.brakingConstraint.detailColor = android.graphics.Color.YELLOW
                    } else if (it.items!![1].value!! <= 0.40 && it.items!![1].value!! >= 0.0) {
                        binding.brakingConstraint.detailColor = android.graphics.Color.GREEN
                    }
                    //for acceleration
                    val accelerationMax = it.items!![0].rangeMax
                    binding.accelerationConstraint.circularProgressBar.progressMax =
                        accelerationMax!!.toFloat()
                    binding.accelerationConstraint.detailValue = it.items!![0].value.toString()
                    binding.accelerationConstraint.circularProgressBar.progress =
                        it.items!![0].value!!.toFloat()
                    if (it.items!![0].value!! >= 0.60) {
                        binding.accelerationConstraint.detailColor = android.graphics.Color.RED
                    } else if (it.items!![0].value!! <= 0.59 && it.items!![0].value!! >= 0.30) {
                        binding.accelerationConstraint.detailColor = android.graphics.Color.YELLOW
                    } else if (it.items!![0].value!! <= 0.29 && it.items!![0].value!! >= 0.0) {
                        binding.accelerationConstraint.detailColor = android.graphics.Color.GREEN
                    }
                    //for time of the day
                    val timeOfTheDayMax = it.items!![4].rangeMax
                    binding.timeOfDayConstraint.circularProgressBar.progressMax =
                        timeOfTheDayMax!!.toFloat()
                    binding.timeOfDayConstraint.detailValue = it.items!![4].value.toString()
                    binding.timeOfDayConstraint.circularProgressBar.progress =
                        it.items!![4].value!!.toFloat()
                    if (it.items!![4].value!! >= 0.71) {
                        binding.timeOfDayConstraint.detailColor = android.graphics.Color.RED
                    } else if (it.items!![4].value!! <= 0.70 && it.items!![4].value!! >= 0.41) {
                        binding.timeOfDayConstraint.detailColor = android.graphics.Color.YELLOW
                    } else if (it.items!![4].value!! <= 0.40 && it.items!![4].value!! >= 0.0) {
                        binding.timeOfDayConstraint.detailColor = android.graphics.Color.GREEN
                    }

                    binding.speedConstraint.detailColor = android.graphics.Color.GREEN
                    binding.speedConstraint.value.text = "--"

                    /*binding.corneringConstraint.circularProgressBar.apply {
                        // Set Progress
                        progress = it.items!![2].value!!.toFloat()
                        progressBarWidth = 4f
                        // Set Progress Max
                        progressMax = corneringMax!!.toFloat()

                        // Set ProgressBar Color
                       if (it.items!![2].value!! <= 0.40){
                           backgroundProgressBarColor = android.graphics.Color.TRANSPARENT
                       } else if (it.items!![2].value!! >= 0.41 && it.items!![2].value!! == 0.70) {
                           backgroundProgressBarColor = android.graphics.Color.TRANSPARENT
                       } else
                           backgroundProgressBarColor = android.graphics.Color.TRANSPARENT
                    }*/
//                    binding.accelerationConstraint.value.text = it.items!![0].value.toString()
//                    binding.brakingConstraint.value.text = it.items!![1].value.toString()
//                    binding.corneringConstraint.value.text = it.items!![2].value.toString()
//                    binding.totalDistanceConstraint.value.text = it.items!![3].value.toString()
//                    binding.timeOfDayConstraint.value.text = it.items!![4].value.toString()
//                    binding.speedConstraint.value.text = "--"
                } else {
                    binding.accelerationConstraint.value.text = "--"
                    binding.brakingConstraint.value.text = "--"
                    binding.corneringConstraint.value.text = "--"
                    binding.totalDistanceConstraint.value.text = "--"
                    binding.timeOfDayConstraint.value.text = "--"
                    binding.speedConstraint.value.text = "--"
                }

            }

            /* if (rr!!.Speeding < 0) {
                 binding.speedConstraint.value.text = "--"
             } else
                 binding.speedConstraint.value.text = rr!!.Speeding.toString()
             if (rr!!.Cornering < 0)
                 binding.corneringConstraint.value.text = "--"
             else
                 binding.corneringConstraint.value.text = rr!!.Cornering.toString()
             if (rr!!.Braking < 0)
                 binding.brakingConstraint.value.text = "--"
             else
                 binding.brakingConstraint.value.text = rr!!.Braking.toString()
             if (rr!!.Acceleration < 0)
                 binding.accelerationConstraint.value.text = "--"
             else
                 binding.accelerationConstraint.value.text = rr!!.Acceleration.toString()
             if (rr!!.TimeOfDay < 0)
                 binding.timeOfDayConstraint.value.text = "--"
             else
                 binding.timeOfDayConstraint.value.text = rr!!.TimeOfDay.toString()

             if (rr!!.DrivingDistance < 0)
                 binding.totalDistanceConstraint.value.text = "--"
             else
                 binding.totalDistanceConstraint.value.text = rr!!.DrivingDistance.toString()*/
        }


    }

    override fun onError(obj: RequestHandler) {
        dismissDialog()
//        binding.progress.visibility = View.GONE
        binding.dashBoard4CL.isEnabled = true
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_dashboard4
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ActivityRegistration.REQUEST_CODE_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                customerX.profileImagePath =
                    data?.getStringExtra(ActivityRegistration.INTENT_IMAGE_URL)!!
                viewModel.storeCustomerLocally(customerX)
                binding.customer = customerX
//                binding.progress.visibility = View.VISIBLE
                showPDialog()
                binding.dashBoard4CL.isEnabled = false
                viewModel.registerEntry(customerX)
//                viewModel.setSuccess(customerX)
                // OR
                // String returnedResult = data.getDataString();
            }
        }
    }


    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
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

}