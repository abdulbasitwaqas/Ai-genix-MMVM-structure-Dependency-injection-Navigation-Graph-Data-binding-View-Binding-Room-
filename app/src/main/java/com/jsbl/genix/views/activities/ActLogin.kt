package com.jsbl.genix.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityLoginBinding
import com.jsbl.genix.model.OTPVerificationModel
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.LoginMdl
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.utils.services.checkAllPermission
import com.jsbl.genix.utils.services.checkAllPermissionReg
import com.jsbl.genix.utils.services.checkPhoneStatePermission
import com.jsbl.genix.utils.services.checkSmsPermission
import com.jsbl.genix.viewModel.LoginViewModel
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CUSTOMER
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_IMEI
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_MOBILE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_PIN
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_POLICY_PASSWORD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_USER_NAME
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.scope.portalapiclient.PortalApi
import com.scope.portalapiclient.ServiceError
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import kotlinx.coroutines.launch
import retrofit2.Response


class ActLogin : BaseActivity<LoginViewModel, ActivityLoginBinding>(LoginViewModel::class.java) {
    private var edittext: String = ""
    private var password: String = ""
    var stringIMEI: String? = ""
    private var pin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P){
            if (checkPhoneStatePermission(this)){
                buttonGetIMEI()
            }
        } else{
            buttonGetIMEI()
        }
        binding.onClickListener = this
        setEditTypes()
    }


    override fun getLayoutRes() =
        R.layout.activity_login

    override fun initViewModel(viewModel: LoginViewModel) {

    }

    override fun onLoading(obj: RequestHandler) {
        showPDialog()
    }

    override fun onSuccess(obj: RequestHandler) {
        dismissDialog()
        if (!viewModel.isForgetPass) {
            if (obj.any is CustomerX) {
                val rr = obj.any as CustomerX
                if (rr!!.isOTPSent) {
                    viewModel.prefsHelper.setScopeToken(rr!!.scopeToken!!)
                    if (edittext.isNotEmpty() && password.isNotEmpty()) {
                        showPDialog()
                        lifecycleScope.launch {
                            val response =
                                    PortalApi.standardAuth(edittext.toString(), password.toString())
                            if (response.isSuccessful) {
                                // call method here
                                val intentM =
                                        Intent(this@ActLogin, ActivityVerification::class.java)
                                intentM.putExtra(INTENT_MOBILE, rr.mobileNo)
                                logD("**mobileNo","mobile: ${rr.mobileNo} "
                                )
                                intentM.putExtra(INTENT_USER_NAME,binding.edMobile.et.text.toString().trim())
                                intentM.putExtra(INTENT_POLICY_PASSWORD, password)
                                intentM.putExtra(INTENT_IMEI, stringIMEI)
                                intentM.putExtra(INTENT_EMAIL, "")
                                intentM.putExtra(INTENT_PIN, pin)
                                startActivity(intentM)
                            } else {
                                val msg = response.errorMessage ?: ServiceError.getErrorText(
                                        this@ActLogin,
                                        response.errorCode
                                )
                                dismissDialog()
                                showShort(this@ActLogin, "Scope Error: $msg")
                            }
                        }
                    }
                }
                else {
                    if (rr!!.carDetails.isNullOrEmpty()) {
                        val intentM =
                            Intent(this@ActLogin, ActCarDetails::class.java)
                        intentM.putExtra(INTENT_CUSTOMER, rr)
                        intentM.putExtra(INTENT_POLICY_PASSWORD, password)
                        startActivity(intentM)
                        return
                    } else {
                        viewModel.prefsHelper.setScopeToken(rr!!.scopeToken!!)
                        if (edittext.isNotEmpty() && password.isNotEmpty()) {
                            showPDialog()
                            lifecycleScope.launch {
                                val response =
                                    PortalApi.standardAuth(edittext.toString(), password.toString())
//                                    PortalApi.standardAuth("abdul467l", "Login@786")
                                if (response.isSuccessful) {
                                    // call method here
                                    val intentM = Intent(this@ActLogin, ActivityMain::class.java).putExtra("show_referance_dialog",false

                                    )
                                    startActivity(intentM)
                                    finishAffinity()
                                } else {
                                    val msg = response.errorMessage ?: ServiceError.getErrorText(
                                        this@ActLogin,
                                        response.errorCode
                                    )
                                    dismissDialog()
                                    showShort(this@ActLogin, "Scope Error: $msg")
                                }
                            }
                        }
                    }
                }

            }
            else if (obj.any is Response<*>) {
            }
        }
    }

    override fun onError(obj: RequestHandler) {
        dismissDialog()
        if (obj.showAlert) {
            showOnlyAlertMessage(
                context = this@ActLogin,
                title = "Login",
                msg = extractNetworkErrorMsg(obj.any!!, this@ActLogin)
            )
        }
    }

    private fun setEditTypes() {
        binding.edMobile.et.inputType = InputType.TYPE_CLASS_TEXT
        binding.edPassword.et.inputType =
            InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }


    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.btnNext -> {
                if (stringIMEI.isNullOrEmpty()) {
                    showShort(this, getString(R.string.warning_imei_required))
                    return
                }
                viewModel.isForgetPass = false
                edittext = binding.edMobile.et.text.toString().trim()
                password = binding.edPassword.et.text.toString().trim()
                //request
                if (validateMobileNo() && validatePassword()) {
                    val loginVal = LoginMdl(edittext, password, stringIMEI)
                    viewModel.loginUser(loginVal)
                }
            }
            R.id.forgetPassword -> {
                val intentM = Intent(this@ActLogin, ActForgetPassword::class.java)
                startActivity(intentM)
            }
            R.id.back -> {
                onBackPressed()
            }
        }
    }


    @SuppressLint("HardwareIds")
    fun buttonGetIMEI() {
        val telephonyManager =
            this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        stringIMEI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            telephonyManager.imei
            } else {
                telephonyManager.deviceId

            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ActivityMain.REQUEST_CODE_FINE_LOCATION -> {
                if (checkAllGranted(grantResults)) {
                    logD(APP_TAG, "all Granted")
                    buttonGetIMEI()
                }

            }

            ActivityMain.REQUEST_READ_PHONE_STATE ->{
                buttonGetIMEI()
            }
            ActivityMain.REQUEST_SMS -> {
                buttonGetIMEI()
            }
        }
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

     private fun validateMobileNo(): Boolean {
         return if (edittext.isNotEmpty()) {
             binding.edMobile.til.error = null
             true
         } else {
             binding.edMobile.til.error = getString(R.string.error_valid_user_name)
             false
         }
     }


    private fun validatePassword(): Boolean {
        return if (password
                .isNotEmpty()
        ) {
            binding.edPassword.til.error = null
            true
        } else {
            binding.edPassword.til.error = getString(R.string.enter_password)
            false
        }
    }


}