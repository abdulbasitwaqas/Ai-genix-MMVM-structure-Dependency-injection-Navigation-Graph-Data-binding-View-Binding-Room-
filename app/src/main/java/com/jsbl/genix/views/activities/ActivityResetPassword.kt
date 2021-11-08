package com.jsbl.genix.views.activities

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.InputType
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityForgetPasswordBinding
import com.jsbl.genix.databinding.ActivityResetPasswordBinding
import com.jsbl.genix.model.registration.RequestResetPassword
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.utils.services.GenixService
import com.jsbl.genix.utils.services.checkPhoneStatePermission
import com.jsbl.genix.viewModel.LoginViewModel
import com.jsbl.genix.viewModel.ResetViewModel
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_MOBILE
import com.jsbl.genix.views.dialogs.ProgressDialog
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher

class ActivityResetPassword : BaseActivity<ResetViewModel, ActivityResetPasswordBinding>(
    ResetViewModel::class.java
) {
    private var newPass: String = ""
    private var confirmPass: String = ""
    var stringIMEI: String? = null
    private var mobileNumber: String? = ""

//    private lateinit var smsVerifyCatcher: SmsVerifyCatcher
    private var pin = ""
    private lateinit var serviceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mobileNumber = intent.getStringExtra(INTENT_MOBILE)
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
////                if (pin.isNotEmpty())
////                    binding.firstPinView.text = pin
//                /* val code: String = parseCode(message) //Parse verification code
//                 etCode.setText(code)*/ //set code in edit text
//                //then you can send verification code to server
////                showShort(this, message)
//            })
        serviceIntent = Intent(this, GenixService::class.java)
        setEditTypes()
    }


    private fun setEditTypes() {
        binding.edNewPass.et.inputType =
            InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        binding.edConfirmPass.et.inputType =
            InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
    }

    override fun onStart() {
        super.onStart()
//        smsVerifyCatcher.onStart()
//        startService(serviceIntent)
    }

    override fun onStop() {
        super.onStop()
//        smsVerifyCatcher.onStop()
//        stopService(serviceIntent)

    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.resetBtn -> {
                /*if (stringIMEI == null) {
                    showShort(this, "IMEI number required")
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        Welcome.REQUEST_READ_PHONE_STATE
                    )
                    return
                }*/


                newPass = binding.edNewPass.et.text.toString().trim()
                confirmPass = binding.edConfirmPass.et.text.toString().trim()
                if (validateNewPass() && validatePassword()) {
                    showPDialog()
                    viewModel.resetPassword(
                        RequestResetPassword(
                            ""+newPass,
                            ""+confirmPass,
                            mobileNumber
                        )
                    )
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

    override fun onSuccess(obj: RequestHandler) {
        val intentM = Intent(this@ActivityResetPassword, ActLogin::class.java)
        startActivity(intentM)
        finishAffinity()
    }

    override fun onError(obj: RequestHandler) {
        if (obj.showAlert) {
//                        showAlert(extractNetworkErrorMsg(t.any!!, this@ResetPasswordActivity))
            showOnlyAlertMessage(
                context = this@ActivityResetPassword,
                title = "Reset Password",
                msg = extractNetworkErrorMsg(obj.any!!, this@ActivityResetPassword)
            )
        }
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
            else -> {
            }
        }
    }


    private fun validateNewPass(): Boolean {
        return if (newPass
                .isNotEmpty()
        ) {
            binding.edNewPass.til.error = null
            true
        } else {
            binding.edNewPass.til.error = getString(R.string.enter_password)
            false
        }
    }


    private fun validatePassword(): Boolean {
        return if (confirmPass
                .isNotEmpty()
        ) {
            /* if (validateNewPass()) {
                 if (newPass.equals(confirmPass, false)) {
                     binding.edConfirmPass.error = null
                     true
                 } else {
                     binding.edConfirmPass.error = getString(R.string.passwordNotMatched)
                     false
                 }
             } else {
                 false
             }*/
            if (newPass.equals(confirmPass, false)) {
                binding.edConfirmPass.til.error = null
                true
            } else {
                binding.edConfirmPass.til.error = getString(R.string.passwordNotMatched)
                false
            }
        } else {
            binding.edConfirmPass.til.error = getString(R.string.enter_password)
            false
        }
    }


    private fun showAlert(msg: String) {

        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }

        with(builder)
        {
            setCancelable(false)
            setTitle("Alert")
            setMessage(msg)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            show()
        }

    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_reset_password
    }

    override fun initViewModel(viewModel: ResetViewModel) {

    }

}