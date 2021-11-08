    package com.jsbl.genix.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.gne.www.lib.OnPinCompletedListener
import com.google.gson.Gson
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityVerificationBinding
import com.jsbl.genix.model.RequestResponse
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.services.checkPhoneStatePermission
import com.jsbl.genix.viewModel.VerificationViewModel
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CHANGE_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CHANGE_PHONE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_FORGET_PASS
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_MOBILE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_PIN
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_POLICY_PASSWORD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_USER_NAME
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.TYPE_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.TYPE_FORGET_PASS
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.TYPE_LOGIN
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import kotlinx.android.synthetic.main.alt_fragment_profile_details.*
import kotlinx.android.synthetic.main.spinner_item_layout.view.*

class ActivityVerification : BaseActivity<VerificationViewModel, ActivityVerificationBinding>(
    VerificationViewModel::class.java
) {

//    private lateinit var smsVerifyCatcher: SmsVerifyCatcher
    private lateinit var timer: CountDownTimer
    private var customerX = CustomerX()
    val gson = Gson()

    // test
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.mobileNumber = intent.getStringExtra(INTENT_MOBILE)
        viewModel.userName = intent.getStringExtra(INTENT_USER_NAME)
        viewModel.policyPassword = intent.getStringExtra(INTENT_POLICY_PASSWORD)
        viewModel.pin = intent.getStringExtra(INTENT_PIN)!!
        viewModel.email = intent.getStringExtra(INTENT_EMAIL)!!


        logD("**userNameee", "email:    " + viewModel.userName)
        logD("**userNameee", "phone number:    " + viewModel.mobileNumber)
        logD("**userNameee", "phone number rrr:    " +viewModel.email)


/*
        viewModel.customer.observe(this, Observer {
            customerX = it
            logD("**customerXX", "customer::" + gson.toJson(customerX))
        })*/

        viewModel.customer.observe(this, Observer {
            it?.let {
                customerX = it
                setAccountProgress(getProfilePercent(it))
            }
        })


        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.Q){
            if (checkPhoneStatePermission(this)){
                buttonGetIMEI()
            }
        }else{
            buttonGetIMEI()

        }


//        stringIMEI = intent.getStringExtra(INTENT_IMEI)
        viewModel.isResetPass = intent.getBooleanExtra(INTENT_FORGET_PASS, false)!!
        viewModel.isEmailVerification = intent.getBooleanExtra(INTENT_CHANGE_EMAIL, false)!!
        viewModel.isPhoneVerification = intent.getBooleanExtra(INTENT_CHANGE_PHONE, false)!!

        binding.mobileNumber.setText(viewModel.mobileNumber)

       /* if (viewModel.isPhoneVerification)
//            binding.mobileNumber.text = viewModel.mobileNumber
            binding.mobileNumber.setText(viewModel.mobileNumber)

        if (viewModel.isEmailVerification)
//            binding.mobileNumber.text = viewModel.email
        binding.mobileNumber.setText(viewModel.email)*/



        viewModel.otpType = if (viewModel.isResetPass) {
            logD("**intent", "RESET PASSWORD")
            TYPE_FORGET_PASS
        } else if (viewModel.isEmailVerification) {
            logD("**intent", "PDA")
            TYPE_EMAIL
        } else if (viewModel.isPhoneVerification) {
            logD("**intent", "phone")
            TYPE_EMAIL
        } else {
            logD("**intent", "login")
            TYPE_LOGIN
        }




        logD("**otpTYPE", "RESET PASSWORD" + viewModel.otpType)
        logD("**fpstatus", "" + viewModel.isResetPass)


        binding.onClickListener = this
        binding.firstPinView.setOnPinCompletionListener(object : OnPinCompletedListener {
            override fun onPinCompleted(entirePin: String?) {

                viewModel.edittext = entirePin.toString().trim()
                if (viewModel.edittext.isNotEmpty()) {
                    if (viewModel.edittext.length == 6) {
                        showPDialog()
                        setCountDown(start = false)
                        viewModel.isOTP = false
                        if (viewModel.isResetPass) {
                            logD("**type", " Is Reset Password")
                            viewModel.verifyRegOtp(
                                viewModel.edittext,
                                viewModel.mobileNumber!!,
                                viewModel.otpType,
                                viewModel.stringIMEI!!,

                                )

                        } else if (viewModel.isEmailVerification) {
                            logD("**type", "email verification")
                            viewModel.verifyEmailOtp(
                                viewModel.edittext,
                                viewModel.email!!,
                                "5",
                                viewModel.stringIMEI!!,
                                viewModel.mobileNumber!!
                            )

                        } else if (viewModel.isPhoneVerification) {
                            logD("**type", "phone verification")
                            viewModel.verifyEmailOtp(
                                viewModel.edittext,
                                viewModel.mobileNumber!!,
                                "1",
                                viewModel.stringIMEI!!,
                                viewModel.email!!
                            )

                        } else {
                            logD("**type", "${viewModel.otpType}")
                            viewModel.verifyOtp(
                                viewModel.edittext,
                                viewModel.mobileNumber!!,
                                viewModel.otpType,
                                viewModel.stringIMEI!!,
                                "" + viewModel.userName,
                                "" + viewModel.policyPassword
                            )
                        }
                    } else {
                        showShort(this@ActivityVerification, "Please enter six digit code")
                    }
                } else {
                    showShort(this@ActivityVerification, "Please enter pin code")
                }

            }
        })

        /*   smsVerifyCatcher = SmsVerifyCatcher(this,
               OnSmsCatchListener<String?> { message ->
                   viewModel.pin = getOtp(message)
                   logD(APP_TAG, "Otp : ${viewModel.pin}")
                   if (viewModel.pin.isNotEmpty())
                       binding.firstPinView.text = viewModel.pin
                   *//* val code: String = parseCode(message) //Parse verification code
                 etCode.setText(code)*//* //set code in edit text
                //then you can send verification code to server
//                showShort(this, message)
            })*/
        timer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                binding.timer.text = "$minutes : $seconds"
            }

            override fun onFinish() {
                binding.tvResend.visible()
            }
        }
        setCountDown(start = true)
        /*addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

            }
        )*/
    }


    fun setCountDown(start: Boolean) {
        if (start) {
            binding.tvResend.gone()
            timer.start()
        } else {
            binding.tvResend.visible()
            binding.timer.text = "00:00"
            timer.cancel()
        }
    }

    override fun onStart() {
        super.onStart()
      /*  smsVerifyCatcher = SmsVerifyCatcher(this,
            OnSmsCatchListener<String?> { message ->
                viewModel.pin = getOtp(message)
                logD(APP_TAG, "Otp : ${viewModel.pin}")
                if (viewModel.pin.isNotEmpty())
                    binding.firstPinView.text = viewModel.pin
                *//* val code: String = parseCode(message) //Parse verification code
                 etCode.setText(code)*//* //set code in edit text
                //then you can send verification code to server
//                showShort(this, message)
            })
        smsVerifyCatcher.onStart()*/
    }

    override fun onStop() {
        super.onStop()
//        smsVerifyCatcher.onStop()
    }


    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.tvResend -> {

                if (viewModel.isPhoneVerification)
                {
                    if (android.util.Patterns.PHONE.matcher(viewModel.mobileNumber!!)
                            .matches() && viewModel.mobileNumber!!.length == 11 && viewModel.mobileNumber!![0] == '0' && viewModel.mobileNumber!![1] == '3'
                    ) {
                        showPDialog()

//                        var otpType = ""
                        setCountDown(start = true)
                        viewModel.isOTP = true
                        viewModel.askOtp(
                            viewModel.mobileNumber!!,
                            "1",
                            viewModel.stringIMEI!!
                        )
                    } else showShort(this, "Invalid mobile number")

                } else if (viewModel.isEmailVerification) {
                    viewModel.isOTP = true
                    showPDialog()
                    viewModel.askOtpEmail(
                        ""+viewModel.email,
                        "5",
                        "${viewModel.stringIMEI}",
                        ""+viewModel.mobileNumber!!
                    )
                } else {

//                startActivity(Intent(this@Login, Verification::class.java))
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_PHONE_STATE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        showShort(this, getString(R.string.warning_imei_required))
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_PHONE_STATE),
                            ActivityWelcome.REQUEST_READ_PHONE_STATE
                        )
                        return
                    }
                    binding.firstPinView.text = ""
                    viewModel.edittext = viewModel.mobileNumber!!
                    if (viewModel.edittext.isNotEmpty()) {

                        if (android.util.Patterns.PHONE.matcher(viewModel.edittext)
                                .matches() && viewModel.edittext.length == 11 && viewModel.edittext[0] == '0' && viewModel.edittext[1] == '3'
                        ) {
                            showPDialog()
//                        var otpType = ""
                            setCountDown(start = true)
                            viewModel.isOTP = true
                            viewModel.askOtp(
                                viewModel.edittext,
                                viewModel.otpType,
                                viewModel.stringIMEI!!
                            )
                        } else showShort(this, "Invalid mobile number")

                    } else {
                        showShort(this, "Please enter mobile number")
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

    override fun onSuccess(obj: RequestHandler) {


/*

        if (viewModel.pin.equals("PDA")) {
            showPDialog()
            logD("**pinvalue", "PDA match")
            viewModel.registerEntry(
                customerX
            )
            viewModel.isRegisteredOTP = true
        }
        else {
*/

        if (viewModel.isOTP){
            dismissDialog()
        }

        else {

            if (viewModel.isResetPass) {
                logD("**pinvalue", "reset password true")
                if (viewModel.isOTP) {
                    showShort(ActivityVerification@ this, "ViewModel OTP" + viewModel.isOTP)
                } else {
                    val intentM =
                        Intent(this@ActivityVerification, ActivityResetPassword::class.java)
                    intentM.putExtra(INTENT_MOBILE, viewModel.mobileNumber)
                    startActivity(intentM)
                    finish()
                }

            } else if (viewModel.isEmailVerification) {
                logD("**pinvalue", "email verification true")
                setResults(true)
            } else if (viewModel.isPhoneVerification) {
                logD("**pinvalue", "phone verification true")
                setResults(true)
            } else {
                logD("**pinvalue", "CUSTOMER")
                if (obj.any is CustomerX) {
                    val rr = obj.any as CustomerX
                    if (rr!!.carDetails == null) {
                        val intentM = Intent(this@ActivityVerification, ActCarDetails::class.java)
                        intentM.putExtra(ActivityRegistration.INTENT_CUSTOMER, rr)
                        intentM.putExtra(INTENT_POLICY_PASSWORD, viewModel.policyPassword)
                        startActivity(intentM)
                        finish()
                        return
                    }
                    startActivity(
                        Intent(
                            this@ActivityVerification,
                            com.jsbl.genix.views.activities.ActivityMain::class.java
                        ).putExtra("show_referance_dialog",false)
                    )
                    finishAffinity()
                }
            }
        }

//        }

        /*   if (viewModel.isRegisteredOTP) {
               logD("**pinvalue", "Registered Successfully" +viewModel.isRegisteredOTP)
               dismissDialog()
               val intentM = Intent(this@ActivityVerification, PersonalDetails::class.java)
               startActivity(intentM)
           }*/


    }


    @Synchronized
    public fun setResults(string: Boolean) {

        if (viewModel.isEmailVerification) {

            val data = Intent()
            data.putExtra(ActivityRegistration.INTENT_CHANGE_EMAIL, string)
            setResult(RESULT_OK, data)
//---close the activity---
            finish()
        } else if (viewModel.isPhoneVerification) {

            logD("**intentResult", "SET Result Changed")

            val data = Intent()
            data.putExtra(ActivityRegistration.INTENT_CHANGE_PHONE, string)
            setResult(RESULT_OK, data)
//---close the activity---
            finish()
        }


    }

    override fun onError(obj: RequestHandler) {
        if (obj.any is RequestResponse) {
            val rr = obj.any as RequestResponse
            if (rr.message != null)
                showShort(this@ActivityVerification, rr.message!!)

        }
        setCountDown(start = false)
    }


    @SuppressLint("HardwareIds")
    fun buttonGetIMEI() {
        val telephonyManager =
            this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        viewModel.stringIMEI =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                telephonyManager.imei
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
            } else {
                telephonyManager.deviceId
            }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ActivityWelcome.REQUEST_READ_PHONE_STATE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TODO uncomment Below Line
                buttonGetIMEI()
            }
            ActivityMain.REQUEST_READ_PHONE_STATE ->{
                buttonGetIMEI()
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_verification
    }

    override fun initViewModel(viewModel: VerificationViewModel) {

    }


    /*
     viewModel.customer.observe(this, Observer {
         it?.let {
//                binding.customer = it
             customerX = it

             */
    /* if (customerX.maritalStatus!![0].equals('m', true)) {
                     binding.spinnerReason.dropDown.setText(maritalStatus[1], false)
                     selectedMaritalStatus = 1
                 } else if (customerX.maritalStatus!![0].equals('s', true)) {
                     binding.spinnerReason.dropDown.setText(maritalStatus[0], false)
                     selectedMaritalStatus = 0
                 }*/
    /*

                if (it.percentage == null) {
                    setAccountProgress(getProfilePercent(it))
                } else {
                    setAccountProgress(it.percentage!!)
                }
            }
        })*/
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

    logD("**percentage", "" + percentage)
//        binding.actionBarCustom.pBar.setProgress(percentage)
}
