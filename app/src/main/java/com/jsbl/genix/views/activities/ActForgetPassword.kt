package com.jsbl.genix.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.InputType
import android.view.View
import androidx.core.app.ActivityCompat
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityForgetPasswordBinding
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.services.checkPhoneStatePermission
import com.jsbl.genix.utils.services.checkPhoneStatePermissionFP
import com.jsbl.genix.viewModel.LoginViewModel
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CHANGE_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_FORGET_PASS
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_IMEI
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_MOBILE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_PIN
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_POLICY_PASSWORD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.TYPE_FORGET_PASS
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher

class ActForgetPassword : BaseActivity<LoginViewModel, ActivityForgetPasswordBinding>(
    LoginViewModel::class.java
) {

    private var edittext: String = ""
    var stringIMEI: String? = ""

//    private lateinit var smsVerifyCatcher: SmsVerifyCatcher
    private var pin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      /*  if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                ActivityWelcome.REQUEST_READ_PHONE_STATE
            )
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        } else {
            buttonGetIMEI()
        }*/
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P){
            if (checkPhoneStatePermission(this)){
                buttonGetIMEI()
            }
        }else{
            buttonGetIMEI()

        }

        binding.onClickListener = this
//        smsVerifyCatcher = SmsVerifyCatcher(this,
//            OnSmsCatchListener<String?> { message ->
//                pin = getOtp(message)
//                logD(APP_TAG, "Otp : $pin")
//
////                if (pin.isNotEmpty())
////                    binding.firstPinView.text = pin
//                /* val code: String = parseCode(message) //Parse verification code
//                 etCode.setText(code)*/ //set code in edit text
//                //then you can send verification code to server
////                showShort(this, message)
//            })
        setEditTypes()
    }

    private fun setEditTypes() {
        binding.edMobile.et.inputType = InputType.TYPE_CLASS_PHONE
    }

    override fun onStart() {
        super.onStart()
//        smsVerifyCatcher.onStart()
    }

    override fun onStop() {
        super.onStop()
//        smsVerifyCatcher.onStop()
//        actionOnService(this@Login, Actions.STOP, viewModel.prefsHelper)
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {

            R.id.submitForget -> {
                if (Build.VERSION.SDK_INT <=Build.VERSION_CODES.P){
                    if (checkPhoneStatePermissionFP(this)){
                        buttonGetIMEI()
                        viewModel.isForgetPass = true
                        edittext = binding.edMobile.et.text.toString().trim()
                        if (validateMobileNo()) {
                            showPDialog()
                            viewModel.askOtp(edittext, TYPE_FORGET_PASS, stringIMEI!!)
                        }
                    }
                }else{
                    buttonGetIMEI()
                    viewModel.isForgetPass = true
                    edittext = binding.edMobile.et.text.toString().trim()
                    if (validateMobileNo()) {
                        showPDialog()
                        viewModel.askOtp(edittext, TYPE_FORGET_PASS, stringIMEI!!)
                    }

                }

            }
            R.id.back -> {
                onBackPressed()
//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
    }

    override  fun onSuccess(obj: RequestHandler) {
        if (viewModel.isForgetPass) {
            val intentM = Intent(this@ActForgetPassword, ActivityVerification::class.java)
            intentM.putExtra(INTENT_MOBILE, binding.edMobile.et.text.toString().trim())
            intentM.putExtra(INTENT_POLICY_PASSWORD, "")
            intentM.putExtra(INTENT_IMEI, stringIMEI)
            intentM.putExtra(INTENT_PIN, pin)
            intentM.putExtra(INTENT_EMAIL,"")
            intentM.putExtra(INTENT_FORGET_PASS, true)
            intentM.putExtra(INTENT_CHANGE_EMAIL, false)
            startActivity(intentM)
        }
    }

    override fun onError(obj: RequestHandler) {
    }

    fun buttonGetIMEI() {
        val telephonyManager =
            this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        stringIMEI =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                telephonyManager.imei
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
            } else {
                telephonyManager.deviceId
            }
        //TODO comment out below line for random imei
//        stringIMEI = stringIMEI + System.currentTimeMillis()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ActivityWelcome.REQUEST_READ_PHONE_STATE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buttonGetIMEI()
            }
            ActivityMain.REQUEST_CODE_FINE_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
//                    toggleService(this@Login, viewModel.prefsHelper)

                }
            }
            ActivityMain.REQUEST_READ_PHONE_STATE_FP -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    buttonGetIMEI()
                    viewModel.isForgetPass = true
                    edittext = binding.edMobile.et.text.toString().trim()
                    if (validateMobileNo()) {
                        showPDialog()
                        viewModel.askOtp(edittext, TYPE_FORGET_PASS, stringIMEI!!)
                    }

                }
            }
        }
    }


    private fun validateMobileNo(): Boolean {
        return if (edittext
                .isNotEmpty() && android.util.Patterns.PHONE.matcher(
                edittext
            )
                .matches()
        ) {
            binding.edMobile.til.error = null
            true
        } else {
            binding.edMobile.til.error = "Please Enter Valid Mobile Number"
            false
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_forget_password
    }

    override fun initViewModel(viewModel: LoginViewModel) {

    }
}