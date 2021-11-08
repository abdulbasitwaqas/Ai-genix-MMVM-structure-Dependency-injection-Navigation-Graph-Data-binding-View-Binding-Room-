package com.jsbl.genix.views.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.R
import com.jsbl.genix.databinding.AltFragmentProfileDetailsBinding
import com.jsbl.genix.model.UpdateProfileResponseModel
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.OtpX
import com.jsbl.genix.url.APIsURL
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.viewModel.PersonalDetailViewModel
import com.jsbl.genix.views.activities.ActCaptureCamera
import com.jsbl.genix.views.activities.ActivityRegistration
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CHANGE_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CHANGE_PHONE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_MOBILE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_PIN
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_POLICY_PASSWORD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.REQUEST_CODE_VERIFY_EMAIL_CHANGE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.REQUEST_CODE_VERIFY_PHONE_CHANGE
import com.jsbl.genix.views.activities.ActivityVerification
import com.jsbl.genix.views.adapters.DropDownArrayAdapter
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.alt_fragment_profile_details.*
import kotlinx.android.synthetic.main.edit_layout.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [PersonalDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class PersonalDetails : BaseFragment<PersonalDetailViewModel, AltFragmentProfileDetailsBinding>(
    PersonalDetailViewModel::class.java
) {

    private var profileImagePath: String = ""
    private var customerX = CustomerX()

    private var maritalStatus = arrayListOf<String>()
    private var selectedMaritalStatus = -1

    private var phoneNumber: String = ""
    private var imei: String = ""
    private var email: String = ""
    private var pin: String = "PDA"
    val symbols = "^[a-zA-Z ]+$"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*   if (ActivityCompat.checkSelfPermission(
                   requireActivity(),
                   Manifest.permission.READ_PHONE_STATE
               ) != PackageManager.PERMISSION_GRANTED
           ) {*/

        /*   ActivityCompat.requestPermissions(
               requireActivity(),
               arrayOf(Manifest.permission.READ_PHONE_STATE),
               ActivityWelcome.REQUEST_READ_PHONE_STATE
           )*/
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
//        return
//        } else {
//            buttonGetIMEI()
//        }

        buttonGetIMEI()
        observeDetails()
        setSpinners()
        //TODO
        binding.onClickListener = this
        viewModel.fetchFromDatabase()
        setEditTypes()


     /*   if (customerX.name != binding.edName.et.text.toString().trim() ||customerX.phone != binding.edMobile.et.text.toString().trim() || customerX.email != binding.edEmail.et.text.toString().trim()){
            binding.btnNextCarDetails.setBackgroundResource(R.drawable.invisible_btn)
            binding.btnNextCarDetails.isEnabled = true
        } else {
            binding.btnNextCarDetails.setBackgroundResource(R.drawable.bg_login_next)
            binding.btnNextCarDetails.isEnabled = false
        }*/
    }

    private fun setEditTypes() {
        binding.edEmail.et.inputType =
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT
        binding.edEmail.et.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(100)))
        binding.edName.et.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(100)))

        binding.edMobile.et.inputType = InputType.TYPE_CLASS_PHONE
        binding.edMobile.et.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(100)))



        binding.cnic.et.alpha = 0.5f
        binding.dob.et.alpha = 0.5f
        binding.gender.et.alpha = 0.5f


//        binding.ed.et.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    }

    fun setSpinners() {
//        setDummyData()
        maritalStatus.add("Single")
        maritalStatus.add("Married")
        val manufacturerAdapter =
            DropDownArrayAdapter(context = requireContext(), objList = maritalStatus)

        /* binding.spinnerReason.dropDown.setAdapter(manufacturerAdapter)
         binding.spinnerReason.dropDown.onItemClickListener =
             AdapterView.OnItemClickListener { parent, view, position, id ->
                 selectedMaritalStatus = position
             }
 */

    }


    fun observeDetails() {
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.customer = it
                customerX = it

                /* if (customerX.maritalStatus!![0].equals('m', true)) {
                     binding.spinnerReason.dropDown.setText(maritalStatus[1], false)
                     selectedMaritalStatus = 1
                 } else if (customerX.maritalStatus!![0].equals('s', true)) {
                     binding.spinnerReason.dropDown.setText(maritalStatus[0], false)
                     selectedMaritalStatus = 0
                 }*/

                binding.edName.et.setText(customerX!!.name)
                binding.edMobile.et.setText(customerX!!.phone)
                binding.edEmail.et.setText(customerX!!.email)
                binding.userNameTV.setText(customerX!!.userName)
                binding.cnic.et.setText(customerX!!.cNIC)
                binding.dob.et.setText(customerX!!.dOB)
                binding.gender.et.setText(customerX!!.gender)
                profileImagePath = customerX.profileImagePath!!

              /*  Glide.with(requireContext())
                    .load(customerX.cNIC_frontImageUrl)
                    .fitCenter()
                    .into(binding.ivProfile)*/

                logD("**percentage","personal details   ${it.percentage}")
                setAccountProgress(getProfilePercent(it))
                if (it.percentage!! > 60) {

                    binding.actionBarCustom.pBar.progress = resources.getColor(R.color.progress_green)

                } else {
                    binding.actionBarCustom.pBar.progress = resources.getColor(R.color.progress_yellow)
                }
            }
        })
    }

    fun setAccountProgress(value: Int) {
        var percentage = value
        if (percentage > 100) {
            percentage = 100
        } else if (percentage < 0) {
            percentage = 0
        }else if (percentage == 60) {
            percentage = 50
        }
        binding.actionBarCustom.pBar.setProgress(percentage)
    }


    private fun validateName(): Boolean {

        if (binding.edName.et.text.toString().trim().isNotEmpty()) {
            binding.edName.til.error = null
            return true
        } else {
            binding.edName.til.error = "Please Enter Name"
            return false
        }
    }

    private fun validateEdMobile(): Boolean {
        if (binding.edMobile.et.text.toString().trim().isNotEmpty()) {
            if (android.util.Patterns.PHONE.matcher(
                    binding.edMobile.et.text.toString().trim()
                )
                    .matches() /*&& edittext.length == 11 && edittext[0] == '0' && edittext[1] == '3'*/
            ) {
                binding.edMobile.til.error = null
                return true
            } else {
                binding.edMobile.til.error = "Please Enter Mobile Number"
                return false
            }

        } else {
            binding.edMobile.til.error = "Please Enter Mobile Number"
            return false
        }
    }

    private fun validateEdEmail(): Boolean {
        if (binding.edEmail.et.text.toString().trim().isNotEmpty()) {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(
                    binding.edEmail.et.text.toString().trim()
                ).matches()
            ) {
//                        viewModel.registerEntry(edittext)
                binding.edEmail.til.error = null
                return true
//                        setNextQuestion()
            } else {
                binding.edEmail.til.error = "Please enter valid email"
                return false
            }

        } else {
            binding.edEmail.til.error = "Please enter valid email"
            return false
        }
    }

    private fun validateCnic(): Boolean {
        if (binding.cnic.et.text.toString().trim().isNotEmpty()) {
            if (binding.cnic.et.text.toString().trim().length == 13) {
                binding.cnic.til.error = null
                return true
            } else {
                binding.cnic.til.error = "Please Enter CNIC"
                return false
            }

        } else {
            binding.cnic.til.error = "Please Enter CNIC"
            return false
        }
    }

/*
  private fun validateMotorType(): Boolean {
        return if (selectedMaritalStatus != -1) {
            binding.spinnerReason.til.error = null
            true
        } else {
            binding.spinnerReason.til.error = "Please Select Marital Status"
            false
        }
    }
*/

    fun hideKeyboard(view: View?) {
        // Check if no view has focus:
        if (view != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.btnNextCarDetails -> {
                if (validateName() && validateEdMobile() && validateEdEmail() && validateCnic()
                ) {

                    phoneNumber = binding.edMobile.et.text.toString().trim()
                    email = binding.edEmail.et.text.toString().trim()

                    logD(
                        "**emails",
                        customerX.email + " == " + binding.edEmail.et.text.toString().trim()
                    )
                    logD(
                        "**emailsName",
                        customerX.name + " == " + binding.edName.et.text.toString().trim()
                    )


                    if (customerX.name != binding.edName.et.text.toString().trim()) {
                        if (binding.edName.et.text!!.matches(Regex(symbols))) {
                            logD("**email", "CustomerMatches")
                            viewModel.isUpdateName = true
                            nextforupdate()
                        } else {
                            showShort(requireContext(), "Please enter valid User name")
                        }
                    }
                    else if (customerX.phone != binding.edMobile.et.text.toString().trim()){
                        if (validateMobileNo()){
                            logD("**email","only phone change")
                            showPDialog()
                            viewModel.isUpdatePhone =true
                            viewModel.askOtp(phoneNumber, "1", ""+imei)

                        }

                    }

                    else if (customerX.email != binding.edEmail.et.text.toString().trim()){
                        logD("**email", "EmailChangeCustomer")
                        viewModel.isUpdateProfile = true

                        showPDialog()
                        viewModel.askOtp(phoneNumber, "5", ""+imei, "" + email)
                    }
                    else if (!customerX.profileImagePath.equals(profileImagePath)){
                        customerX.profileImagePath = profileImagePath
                        nextforupdate()
                    }

                }
            }
            R.id.drawerImage -> {
                requireActivity().onBackPressed()
            }
            R.id.ivProfile -> {
                startActivityForResult(
                    Intent(context, ActCaptureCamera::class.java),
                    ActivityRegistration.REQUEST_CODE_PROFILE
                )
//                    viewModel.registerEntry(binding.edMessage.text.toString().trim())
            }
        }
    }


    fun nextforupdate() {
        phoneNumber = binding.edMobile.et.text.toString().trim()
        logD("**email", "CustomerName $phoneNumber")
//        customerX.cNIC = binding.cnic.et.text.toString().trim()
        customerX.email = binding.edEmail.et.text.toString().trim()
//                    customerX.maritalStatus = maritalStatus[selectedMaritalStatus]
        customerX.name = binding.edName.et.text.toString().trim()
        customerX.phone = phoneNumber
        showPDialog()
        logD("**token","scope token:"+customerX.scopeToken)
        viewModel.registerEntry(
            customerX!!
        )
    }

    override fun onLoading(obj: RequestHandler) {

    }

    override fun onSuccess(obj: RequestHandler) {
//        val rr = obj.any as CustomerX

        if (obj.any is UpdateProfileResponseModel){
            if (viewModel.isUpdateProfile) {
                viewModel.isUpdateProfile=false

                viewModel.changedEmail == binding.edEmail.et.text.toString().trim()
                val intentM = Intent(context, ActivityVerification::class.java)
                intentM.putExtra(INTENT_EMAIL, binding.edMobile.et.text.toString().trim())
                intentM.putExtra(INTENT_CHANGE_EMAIL, true)
                intentM.putExtra(INTENT_CHANGE_PHONE, false)
                intentM.putExtra(INTENT_PIN, "")
                intentM.putExtra(INTENT_MOBILE, binding.edEmail.et.text.toString().trim())
                intentM.putExtra(INTENT_POLICY_PASSWORD, "")
                startActivityForResult(intentM, REQUEST_CODE_VERIFY_EMAIL_CHANGE)


            }
            else if (viewModel.isUpdatePhone){
                viewModel.isUpdatePhone=false

                val intentM =
                    Intent(requireContext(), ActivityVerification::class.java)
                intentM.putExtra(INTENT_MOBILE, phoneNumber)
                intentM.putExtra(INTENT_EMAIL, "" + viewModel.changedEmail)
                intentM.putExtra(INTENT_CHANGE_EMAIL, false)
                intentM.putExtra(INTENT_CHANGE_PHONE, true)
                intentM.putExtra(INTENT_PIN, "")
                intentM.putExtra(INTENT_POLICY_PASSWORD, "")
                startActivityForResult(intentM, REQUEST_CODE_VERIFY_PHONE_CHANGE)


            }

        }

       else if (obj.any is CustomerX) {
            val rr = obj.any as CustomerX
                viewModel.prefsHelper.updateAuth(""+rr.token)
//            logD("*******authhh","Auth token: "+rr.token)
//            customerX.scopeToken = rr.token
                customerX.token = rr.token
                showOnlyAlertMessage(
                    context = requireContext(),
                    title = "Personal Details",
                    msg = "Details Added Successfully",
                    onPositiveClick = {
                        requireActivity().onBackPressed()
                    }
                )
       }



    }

    override fun onError(obj: RequestHandler) {
        dismissDialog()
    }

    override fun getLayoutRes(): Int {
        return R.layout.alt_fragment_profile_details
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ActivityRegistration.REQUEST_CODE_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                profileImagePath = data?.getStringExtra(ActivityRegistration.INTENT_IMAGE_URL)!!
                Glide.with(this)
                    .load(BuildConfig.LOAD_PROFILE_BASE_URL_DEV+profileImagePath)
                    .fitCenter()
                    .into(binding.ivProfile)

                viewModel.storeCustomerLocally(customerX)
                binding.customer = customerX
                if (!customerX.profileImagePath.equals(profileImagePath)){
                    customerX.profileImagePath = profileImagePath
                    nextforupdate()
                }

//                viewModel.setSuccess(customerX)
                // OR
                // String returnedResult = data.getDataString();
            }
        } else if (requestCode == ActivityRegistration.REQUEST_CODE_VERIFY_EMAIL_CHANGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data?.getBooleanExtra(ActivityRegistration.INTENT_CHANGE_EMAIL, false)!!) {
                    viewModel.isUpdateProfile = false
                    nextforupdate()
                }
            }
        } else if (requestCode == ActivityRegistration.REQUEST_CODE_VERIFY_PHONE_CHANGE) {
            if (resultCode == Activity.RESULT_OK) {
                logD("**phoneUpdate","Phone On Activity result ")
                if (data?.getBooleanExtra(ActivityRegistration.INTENT_CHANGE_PHONE, false)!!) {
                    viewModel.isUpdateProfile = false
                    nextforupdate()
                }
            }
        }
    }

    fun buttonGetIMEI() {
        val telephonyManager =
            requireActivity().getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
        imei =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                telephonyManager.imei
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(
                    requireActivity().getContentResolver(),
                    Settings.Secure.ANDROID_ID
                )


            } else {
                telephonyManager.deviceId

            }
        //TODO comment out below line for random imei
//        stringIMEI = stringIMEI + System.currentTimeMillis()
    }



    private fun validateMobileNo(): Boolean {
        return if (edMobile.et.text.toString()
                .isNotEmpty() && android.util.Patterns.PHONE.matcher(
                edMobile.et.text.toString()
            )
                .matches()
        ) {
            binding.edMobile.til.error = null
            true
        } else {
            binding.edMobile.til.error = getString(R.string.error_valid_mobile)
            false
        }
    }
}


