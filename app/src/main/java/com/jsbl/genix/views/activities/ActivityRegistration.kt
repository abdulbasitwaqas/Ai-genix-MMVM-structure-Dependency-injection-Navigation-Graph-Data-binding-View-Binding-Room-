package com.jsbl.genix.views.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.InputType.*
import android.view.KeyEvent
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityRegistrationBinding
import com.jsbl.genix.model.NetworkModel.PasswordRequest
import com.jsbl.genix.model.RegChatItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.OtpX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.showConfirmationDialog
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.utils.services.checkAllPermission
import com.jsbl.genix.utils.services.checkAllPermissionReg
import com.jsbl.genix.utils.services.checkPhoneStatePermission
import com.jsbl.genix.viewModel.RegistrationViewModel
import com.jsbl.genix.views.adapters.RegChatAdapter
import com.jsbl.genix.views.adapters.RegChatAdapter.Companion.VIEW_RECEIVER
import com.jsbl.genix.views.adapters.RegChatAdapter.Companion.VIEW_SENDER
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern


class ActivityRegistration :
    BaseActivity<RegistrationViewModel, ActivityRegistrationBinding>(RegistrationViewModel::class.java) {
    private lateinit var passwordRequest: PasswordRequest
    private lateinit var regChatAdapter: RegChatAdapter
    var stringIMEI: String? = null

    lateinit var cnicWatcher: TextWatcher
    private var isFormatting = false
    private var deletingHyphen = false
    private var deletingBackward = false

    private var hyphenStart = 0
    val CITY_CODE = 1
    val COUNTRY_CODE = 2
    var genderData = 1
    private lateinit var smsVerifyCatcher: SmsVerifyCatcher
    private lateinit var timer: CountDownTimer


    //
    private var edittext: String = ""
    private var cnicImages = arrayListOf<String>()

    companion object {
        //to display view on XML
        const val VIEW_TYPE_TEXT_ONLY = 1
        const val VIEW_TYPE_MOBILE = 6
        const val VIEW_TYPE_CNIC = 4
        const val VIEW_TYPE_PROFILE_PIC = 5
        const val VIEW_TYPE_IMAGE_URL = 2
        const val VIEW_TYPE_PASSWORD = 3
        const val VIEW_TYPE_RESEND = 7
        const val VIEW_TYPE_SKIP = 8
        const val VIEW_TYPE_BOTH_RESEND_SKIP = 9
        const val VIEW_TYPE_CNIC_PIC = 10

        //to display question and its ans
        const val STEP_MOBILE = 0
        const val STEP_MOBILE_VERIFICATION = 1
        const val STEP_EMAIL = 2
        const val STEP_EMAIL_VERIFICATION = 3
        const val STEP_NAME = 4
        const val STEP_CNIC = 5
        const val STEP_CNIC_IMAGE = 6
        const val STEP_MARITAL = -1
        const val STEP_UPLOAD_PIC = 7
        const val STEP_PASSWORD = 8
        const val STEP_CONFIRM_PASSWORD = 9

        //to display question and its ans
        const val NEW_STEP_NAME = 1
        const val NEW_STEP_MOBILE = 2

        //        const val NEW_STEP_OTP_VERIFICATION = 3 // it will use for both SMS and email verification
        const val NEW_STEP_MOBILE_VERIFICATION = 3 // it will use for both SMS and email verification
        const val NEW_STEP_EMAIL = 4
        const val NEW_STEP_EMAIL_VERIFICATION = 5

        //        const val NEW_STEP_CNIC = 5
        const val NEW_STEP_CNIC_IMAGE = 6

        //        const val NEW_STEP_DOB = 7
//        const val NEW_STEP_GENDER = 8
        const val NEW_STEP_PASSWORD = 8
        const val NEW_STEP_PASSWORD_CONFIRM = 9
        const val AUTO_OTP_PERMISSIONS = 10
        const val NEW_STEP_ALL_PERMISSIONS = 11
        const val NEW_STEP_IS_INSURED = 12
        const val NEW_STEP_CNIC_CONFIRM = -10

        const val UPDATE_TYPE_CNIC: String = "CNIC:"
        const val UPDATE_TYPE_DOB: String = "DOB:"
        const val UPDATE_TYPE_GENDER: String = "Gender:"


        const val TYPE_MOBILE: String = "1"
        const val TYPE_EMAIL: String = "2"
        const val TYPE_LOGIN: String = "3"
        const val TYPE_FORGET_PASS: String = "4"
        const val TYPE_UPDATE_PROFILE: String = "5"

        //for intents
        const val INTENT_CNIC: String = "cnic"
        const val INTENT_DATE: String = "date"
        const val INTENT_GENDER: String = "gender"
        const val INTENT_CNIC_EXPIRY: String = "cnicExpiry"
        const val INTENT_MOBILE: String = "Mobile"
        const val INTENT_USER_NAME: String = "userName"
        const val INTENT_USER_PASSWORD: String = "userPassword"
        const val INTENT_IMEI: String = "IMEI"
        const val INTENT_IMAGE_URL: String = "imageUrl"
        const val INTENT_CNIC_IMAGE_URLS: String = "cnicImageUrl"
        const val INTENT_CUSTOMER: String = "customer"
        const val INTENT_POLICY_PASSWORD: String = "policyPassword"
        const val INTENT_PIN: String = "pin"
        const val INTENT_EMAIL: String = "email"
        const val INTENT_CHANGE_EMAIL: String = "change_email"
        const val INTENT_CHANGE_PHONE: String = "change_phone"
        const val INTENT_FORGET_PASS: String = "forgetPass"
        const val INTENT_SPLASH_FLOW: String = "splashflow"
        const val INTENT_IMAGE_FILE_ABS_PATH: String = "imagePath"
        const val REGEX_PASSWORD: String =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}\$"
        const val REGEX_CNIC: String =
            "^[0-9+]{5}-[0-9+]{7}-[0-9]{1}$"
        const val REGEX_CNIC2: String =
            "^[0-9+]{13}$"
        const val REGEX_DATE: String =
            "^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d\$"
        const val REGEX_CONFIRMATION: String =
            "^([1-3]+\\s)*[a-zA-Z0-9-/.]+\$"
        const val REGEX_GENDER: String =
            "^(Fem|M)ale\$"

         /*val REGEX_NEW_EMAIL: String = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"*/

        const val REGEX_NEW_EMAIL = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        //Request codes
        const val REQUEST_CODE_PROFILE = 10
        const val REQUEST_CODE_CNIC = 11
        const val REQUEST_CODE_CNIC_UPLOAD = 12
        const val REQUEST_CODE_VERIFY_EMAIL_CHANGE = 13
        const val REQUEST_CODE_VERIFY_PHONE_CHANGE = 14
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHeader()
       /* if (ActivityCompat.checkSelfPermission(
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
        if (checkPhoneStatePermission(this)) {
            buttonGetIMEI()
        }
        smsVerifyCatcher = SmsVerifyCatcher(this,
            OnSmsCatchListener<String?> { message ->
                if (viewModel.regStep == 1) {
                    val otp = getOtp(message)
                    logD(APP_TAG, "Otp : $otp")
                    if (otp.isNotEmpty())
                        binding.edMessage.setText(otp)
                }
                /* val code: String = parseCode(message) //Parse verification code
                 etCode.setText(code)*/ //set code in edit text
                //then you can send verification code to server
//                showShort(this, message)
            })
        regChatAdapter = RegChatAdapter(viewModel.chatList, this)
        setViews()
//        setRequestHandler()
        setDummyProgress()
//        setDummyList()



        setCnicType()
        binding.loadingProgress.visible()
        timer = object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                binding.loadingProgress.gone()
                setNextQuestion(null)

            }
        }
        if (viewModel.chatList.isEmpty()) {
            timer.start()
        }
//        basicAlert()
    }

    override fun onStart() {
        super.onStart()
        try{
            smsVerifyCatcher.onStart()
        }catch(e:Exception){

        }
    }

    override fun onStop() {
        super.onStop()
        try{
            smsVerifyCatcher.onStop()
        }catch(e:Exception){

        }
    }

    fun setHeader() {
//        binding.actionBarCustom.title.text = getString(R.string.registration_label)
    }

    fun setViews() {
        binding.onClickListener = this
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = regChatAdapter
        }
        binding.edMessage.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event.action === KeyEvent.ACTION_DOWN
                            && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    sendForm()
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                edittext = data?.getStringExtra(INTENT_IMAGE_URL)!!
                viewModel.setProfileImage(edittext)
                sendActions()
                viewModel.regStep++
                setNextQuestion(null)
                // OR
                // String returnedResult = data.getDataString();
            }
        } else if (requestCode == REQUEST_CODE_CNIC_UPLOAD) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    cnicImages = data?.getStringArrayListExtra(INTENT_CNIC_IMAGE_URLS)!!
                    viewModel.setCNICUrls(cnicImages)
                    sendActions()
                    viewModel.regStep++
                    setNextQuestion(null)
                } catch (e: Exception) {
                    showShort(this, "Couldn't get Images. Please try again.")
                }
            }
        } else if (requestCode == REQUEST_CODE_CNIC) {
            if (resultCode == Activity.RESULT_OK) {
                edittext = data?.getStringExtra(INTENT_CNIC)!!
//                sendActions()
                binding.edMessage.setText(edittext)
//                setNextQuestion()
                // OR
                // String returnedResult = data.getDataString();
            }
        }
    }


    override fun onClick(view: View, obj: Any) {
        when (view.id) {
            R.id.reg_send -> {
//                startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
//                showShort(this, "SendClicked")
                sendForm()
                /*startActivityForResult(
                    Intent(this@RegistrationActivity, CameraCapture::class.java),
                    REQUEST_CODE_PROFILE
                )*/
            }
            R.id.camera -> {
                if (obj is Int) {
                    when (obj) {
                        VIEW_TYPE_CNIC -> {
                            when (viewModel.regStep) {
                                STEP_CNIC -> startActivityForResult(
                                    Intent(this@ActivityRegistration, ActivityScanCNIC::class.java),
                                    REQUEST_CODE_CNIC
                                )
                                STEP_CNIC_IMAGE -> {
                                    //TODO Done for upload CNIC Pic
                                    sendForm()


                                }
                                else -> {
                                    showShort(this, "Invalid Operation")
                                }
                            }
                        }
                        VIEW_TYPE_PROFILE_PIC -> {
                            if (viewModel.regStep == STEP_UPLOAD_PIC) {
                                sendForm()

                            } else {
                                showShort(this, "Invalid Operation")
                            }
                        }
                    }
                }
//                showShort(this, "SendClicked")
            }
            R.id.resend -> {
                when (viewModel.regStep) {
                    STEP_MOBILE_VERIFICATION -> {
                        if (android.util.Patterns.PHONE.matcher(
                                viewModel.mobileNumber
                            )
                                .matches() /*&& edittext.length == 11 && edittext[0] == '0' && edittext[1] == '3'*/
                        ) {
                            viewModel.resendOtp(
                                viewModel.mobileNumber,
                                TYPE_MOBILE,
                                "",
                                stringIMEI!!
                            )
                        } else showShort(this, "Invalid mobile number")

                    }
                    STEP_EMAIL_VERIFICATION -> {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(
                                viewModel.email
                            )
                                .matches() /*&& edittext.length == 11 && edittext[0] == '0' && edittext[1] == '3'*/
                        ) {
                            viewModel.resendOtp(
                                viewModel.mobileNumber,
                                TYPE_EMAIL,
                                viewModel.email,
                                stringIMEI!!
                            )
                        } else showShort(this, "Invalid mobile number")

                    }
                }
//                showShort(this, "SendClicked")
            }
            R.id.skip -> {
                viewModel.regStep++
//                binding.loadingProgress.gone()
                setNextQuestion(null)
//                showShort(this, "SendClicked")
            }
            R.id.drawerImage -> {
                onBackPressed()
//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
        }
    }

    override fun onLoading(obj: RequestHandler) {
        if (!viewModel.resend)
            sendActions()
        binding.loadingProgress.visible()
    }

    override  fun onSuccess(obj: RequestHandler) {
        binding.loadingProgress.gone()
        if (viewModel.resend) {
            viewModel.resend = false
        } else {
            if (obj.any is CustomerX) {
                val rr = obj.any as CustomerX
                setNextQuestion(rr)
            } else {
                setNextQuestion(null)
            }
        }
    }

    override fun onError(obj: RequestHandler) {
        binding.loadingProgress.gone()
        if (!isLastQuestion()) {
            viewModel.chatList.removeAt(viewModel.chatList.size - 1)
            regChatAdapter.notifyItemRemoved(viewModel.chatList.size)
            binding.recycler.smoothScrollToPosition(viewModel.chatList.size)
        }/*if (t.any is RequestResponse) {
                            val rr = t.any as RequestResponse
                            if (rr.message != null)
                                showShort(this@RegistrationActivity, rr.message!!)

                        }*/
        if (viewModel.resend) {
            viewModel.resend = false
        }
        if (obj.showAlert) {
//                            showAlert(extractNetworkErrorMsg(t.any!!, this@RegistrationActivity))
            if (obj.any is Response<*>) {
                showOnlyAlertMessage(
                    context = this@ActivityRegistration,
                    title = "Registration",
                    msg = extractNetworkErrorMsg(obj.any!!, this@ActivityRegistration)
                )
            } else if (obj.any is String) {
                showOnlyAlertMessage(
                    context = this@ActivityRegistration,
                    title = "Registration",
                    msg = obj.any as String,
                    onPositiveClick = {
                        val intentM = Intent(
                            this@ActivityRegistration,
                            com.jsbl.genix.views.activities.ActLogin::class.java
                        )
                        startActivity(intentM)
                        finish()
                    }
                )
            }

        }
    }

    fun setDummyProgress() {
        /*    binding.accProgress.setRange(0f, 100f)
            binding.accProgress.setProgress(10f)
            binding.accProgress.isEnabled = false
            binding.accProgress.setIndicatorTextDecimalFormat("0")
            binding.accProgress.setIndicatorTextStringFormat("%s%%")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.accProgress.setTypeface(resources.getFont(R.font.cocomat_regular))
            }*/
    }

    fun setDummyList() {
        //make constants for message types
        var dummyChat = arrayListOf<RegChatItem>()
        dummyChat.add(RegChatItem(VIEW_SENDER, VIEW_TYPE_MOBILE, getString(R.string.reg_question1)))
        dummyChat.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_TEXT_ONLY, "03365398680"))
        dummyChat.add(
            RegChatItem(
                VIEW_SENDER, VIEW_TYPE_RESEND, getString(R.string.reg_question2) +
                        "SMS"
            )
        )
        dummyChat.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_TEXT_ONLY, "955648"))
        dummyChat.add(
            RegChatItem(
                VIEW_SENDER,
                VIEW_TYPE_TEXT_ONLY,
                getString(R.string.reg_question3)
            )
        )
        dummyChat.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_TEXT_ONLY, "musama@ais.net.pk"))
        dummyChat.add(
            RegChatItem(
                VIEW_SENDER,
                VIEW_TYPE_RESEND,
                getString(R.string.reg_question4)
            )
        )
        dummyChat.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_TEXT_ONLY, "955648"))
        dummyChat.add(RegChatItem(VIEW_SENDER, VIEW_TYPE_CNIC, getString(R.string.reg_question5)))
        dummyChat.add(
            RegChatItem(
                VIEW_RECEIVER, 1, "CNIC: 4130351494179\n" +
                        "NAME: MEER GHAZANFAR ALI\n" +
                        "PRESENT_ADDRESS:POST OFFICE \n" +
                        "TANDO QAISER W \n" +
                        "HUSSAIN KHAN THORHO \n" +
                        "TEHSIL and DISTRICT HYDERABAD,\n" +
                        "DATE_OF_BIRTH: 1975-04-01\n" +
                        "GENDER: male\n" +
                        "BIRTH_PLACE: HYDERABAD\n" +
                        "MOTHER_NAME: SHAHNAZ\n" +
                        "EXPIRY_DATE: 2027-11-01"
            )
        )
        dummyChat.add(
            RegChatItem(
                VIEW_SENDER,
                VIEW_TYPE_TEXT_ONLY,
                getString(R.string.reg_question6)
            )
        )
        dummyChat.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_TEXT_ONLY, "Single"))
        dummyChat.add(
            RegChatItem(
                VIEW_SENDER,
                VIEW_TYPE_PROFILE_PIC,
                getString(R.string.reg_question7)
            )
        )
        dummyChat.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_IMAGE_URL, "URL"))
        dummyChat.add(
            RegChatItem(
                VIEW_SENDER,
                VIEW_TYPE_TEXT_ONLY,
                getString(R.string.reg_question8)
            )
        )
        dummyChat.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_PASSWORD, "password"))
        dummyChat.add(
            RegChatItem(
                VIEW_SENDER,
                VIEW_TYPE_TEXT_ONLY,
                getString(R.string.reg_question9)
            )
        )
        dummyChat.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_PASSWORD, "passwrod"))
        regChatAdapter.updateList(dummyChat)
    }

    fun sendForm() {
        if (binding.loadingProgress.visibility == VISIBLE) {
            showShort(this, "Please wait while loading")
            return
        }
        if (stringIMEI.isNullOrEmpty()) {
            showShort(this, "IMEI number required")
            checkPhoneStatePermission(this)
            return
        }
        edittext = binding.edMessage.text.toString().trim()
        when (viewModel.regStep) {
            STEP_MOBILE -> {

                if (edittext.isNotEmpty()) {

                    if (android.util.Patterns.PHONE.matcher(
                            edittext
                        )
                            .matches() /*&& edittext.length == 11 && edittext[0] == '0' && edittext[1] == '3'*/
                    ) {
                        viewModel.askOtp(edittext, TYPE_MOBILE, "", stringIMEI!!)
                    } else showShort(this, "Invalid mobile number")

                } else {
                    showShort(this, "Please enter mobile number")
                }
            }
            STEP_MOBILE_VERIFICATION -> {
                if (edittext.isNotEmpty()) {
                    if (edittext.length == 6) {
                        viewModel.verifyOtp(edittext, TYPE_MOBILE, "", stringIMEI!!)

//                        setNextQuestion()
                    } else {
                        showShort(this, "Please enter six digit code")
                    }
                } else {
                    showShort(this, "Please enter pin code")
                }
            }
            STEP_EMAIL -> {
                if (edittext.isNotEmpty()) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(
                            edittext
                        ).matches()
                    ) {
//                        viewModel.registerEntry(edittext)
                        viewModel.askOtp(viewModel.mobileNumber, TYPE_EMAIL, edittext, stringIMEI!!)

//                        setNextQuestion()
                    } else {
                        showShort(this, "Invalid email address")

                    }
                } else {
                    showShort(this, "Please enter email address")
                }
            }
            STEP_EMAIL_VERIFICATION -> {
                if (edittext.isNotEmpty()) {
                    if (edittext.length == 6) {

                        viewModel.verifyOtp(edittext, TYPE_EMAIL, viewModel.email, stringIMEI!!)

//                        setNextQuestion()
                    } else {
                        showShort(this, "Please enter six digit code")
                    }
                } else {
                    showShort(this, "Please enter pin code")
                }
            }
            STEP_NAME -> {
                if (edittext.isNotEmpty()) {
                    viewModel.registerEntry(edittext.capitalize())
                } else {
                    showShort(this, "Please enter Name")
                }
            }
            STEP_MARITAL -> {
                if (edittext.isNotEmpty()) {

                    viewModel.registerEntry(edittext.capitalize())

//                    setNextQuestion()
                } else {
                    showShort(this, "Please enter status")
                }
            }
            STEP_PASSWORD -> {
                if (validatePassword()) {
                    /*  viewModel.regStep++
                      sendActions()

                      setNextQuestion(null)*/
                    viewModel.pass = edittext


                    passwordRequest=PasswordRequest(edittext)

                    val json = Gson().toJson(passwordRequest)
                    var Request: String? = null

                    try {
                        Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                    val Text = Request
                    val sendEncrptRequest = SendEncryptionRequest()
                    sendEncrptRequest.setText(Text)
                    viewModel.validatePassword(sendEncrptRequest)
                }
//                    viewModel.registerEntry(binding.edMessage.text.toString().trim())

            }
            STEP_CONFIRM_PASSWORD -> {
                if (validatePassword()) {
                    if (viewModel.pass.equals(edittext)) {
                        viewModel.registerEntry(edittext)
//                        setNextQuestion()
                    } else {
                        showShort(this, "Password not matched")

                    }
                }
            }
            STEP_CNIC -> {

                if (edittext.isNotEmpty()) {
                    if (edittext.length == 13) {
                        viewModel.registerEntry(edittext)

//                        setNextQuestion()
                    } else {
                        showShort(this, "Please enter valid CNIC")
                    }
                } else {
                    showShort(this, "Please enter CNIC")
                }
//                    viewModel.registerEntry(binding.edMessage.text.toString().trim())
//                setNextQuestion()
            }
            STEP_CNIC_IMAGE -> {
                startActivityForResult(
                    Intent(this@ActivityRegistration, ActCaptureCNIC::class.java),
                    REQUEST_CODE_CNIC_UPLOAD
                )
//                    viewModel.registerEntry(binding.edMessage.text.toString().trim())
//                setNextQuestion()
            }
            STEP_UPLOAD_PIC -> {
                startActivityForResult(
                    Intent(this@ActivityRegistration, ActCaptureCamera::class.java),
                    REQUEST_CODE_PROFILE
                )
//                    viewModel.registerEntry(binding.edMessage.text.toString().trim())
            }
        }

    }

    private fun validatePassword(): Boolean {
        return if (edittext
                .isNotEmpty()
        ) {
            binding.edMessage.error = null
            true
        } else {
            binding.edMessage.error = "Please enter Password"
            //                "Password must contain eight characters, alphabets, alphanumeric and at least one special character"
            false
        }
    }

    fun sendActions() {
        if (binding.edMessage.text.equals("")) {
            return
        }
        binding.edMessage.text.clear()
        if (!isLastQuestion()) {
            return
        }
        if (viewModel.regStep == STEP_UPLOAD_PIC) {
            viewModel.chatList.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_IMAGE_URL, edittext))
        } else
            if (viewModel.regStep == STEP_CNIC_IMAGE) {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_RECEIVER,
                        VIEW_TYPE_CNIC_PIC,
                        edittext,
                        cnicImages
                    )
                )
            } else if (viewModel.regStep == STEP_PASSWORD || viewModel.regStep == STEP_CONFIRM_PASSWORD) {
                viewModel.chatList.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_PASSWORD, edittext))
            } else {
                viewModel.chatList.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_TEXT_ONLY, edittext))
            }
        regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
        binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)

    }

    fun isLastQuestion(): Boolean {
        return if (viewModel.chatList.isEmpty()) {
            false
        } else {
            viewModel.chatList.last().sender == VIEW_SENDER
        }
    }


    override fun onBackPressed() {

        if (viewModel.regStep <= 8) {
//            backAlert()
            showConfirmationDialog(
                this,
                title = "Registration",
                msg = "Are you sure you want to quit the registration process?",
                onPositiveClick = {
                    finish()
                }
            )
        } else {
            super.onBackPressed()
        }
    }

    private fun moveNext(customer: CustomerX) {
        if (!customer.carDetails.isNullOrEmpty()) {
            val intentM = Intent(
                this@ActivityRegistration,
                com.jsbl.genix.views.activities.ActivityMain::class.java
            ).putExtra("show_referance_dialog",false)
            startActivity(intentM)
            finishAffinity()
        } else {
            moveToCarDetail(customer)
        }

    }

    private fun moveToCarDetail(customer: CustomerX) {

        val intentM = Intent(
            this@ActivityRegistration,
            com.jsbl.genix.views.activities.ActCarDetails::class.java
        )
        intentM.putExtra(INTENT_CUSTOMER, customer)
        intentM.putExtra(INTENT_POLICY_PASSWORD, viewModel.pass)
//            intentM.putExtra(INTENT_CUTOMER, viewModel.getCustomer())
        startActivity(intentM)
        finish()
    }


    fun setNextQuestion(customer: CustomerX?) {
        if (viewModel.regStep > STEP_CONFIRM_PASSWORD) {
            if (customer != null)
//                basicAlert(customer)
                showOnlyAlertMessage(
                    context = this@ActivityRegistration,
                    title = "Registered",
                    msg = "Congratulations, you have been Registered successfully.",
                    onPositiveClick = { moveNext(customer!!) }
                )
//            moveNext()
            return
        }

        if (isLastQuestion()) {
            return
        }
        when (viewModel.regStep) {
            STEP_MOBILE -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_MOBILE,
                        getString(R.string.reg_question1)
                    )
                )
                binding.edMessage.inputType =
                    InputType.TYPE_CLASS_PHONE
                binding.edMessage.isEnabled = true
                binding.edMessage.hint = getString(R.string.reg_hint_number)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(20)))
                binding.edMessage.text.clear()
            }
            STEP_MOBILE_VERIFICATION -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_RESEND,
                        getString(R.string.reg_question2)
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_NUMBER
                binding.edMessage.isEnabled = true
                binding.edMessage.hint = getString(R.string.reg_hint_pin)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(6)))
                binding.edMessage.text.clear()
            }
            STEP_EMAIL -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        getString(R.string.reg_question3)
                    )
                )
                binding.edMessage.inputType = TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                binding.edMessage.isEnabled = true
                binding.edMessage.hint = getString(R.string.reg_hint_email)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(100)))
                binding.edMessage.text.clear()
            }
            STEP_EMAIL_VERIFICATION -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_RESEND,
                        getString(R.string.reg_question4)
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_NUMBER
                binding.edMessage.isEnabled = true
                binding.edMessage.hint = getString(R.string.reg_hint_pin)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(6)))
                binding.edMessage.text.clear()
            }
            STEP_NAME -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        getString(R.string.reg_question10)
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_TEXT
                binding.edMessage.isEnabled = true
                binding.edMessage.hint = getString(R.string.reg_name)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(100)))
                binding.edMessage.text.clear()
            }
            STEP_CNIC -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_CNIC,
                        getString(R.string.reg_question5)
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_NUMBER
                binding.edMessage.isEnabled = false
                binding.edMessage.hint = getString(R.string.reg_cnic_hint)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(13)))
//                binding.edMessage.addTextChangedListener(cnicWatcher)
                binding.edMessage.text.clear()
            }
            STEP_CNIC_IMAGE -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_CNIC,
                        getString(R.string.reg_question11)
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_NUMBER
                binding.edMessage.isEnabled = false
                binding.edMessage.hint = getString(R.string.reg_cnic_hint)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(13)))
//                binding.edMessage.addTextChangedListener(cnicWatcher)
                binding.edMessage.text.clear()
            }
            STEP_MARITAL -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        getString(R.string.reg_question6)
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_TEXT
                binding.edMessage.isEnabled = true
                binding.edMessage.hint = getString(R.string.reg_marital_status)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(25)))

//                binding.edMessage.removeTextChangedListener(cnicWatcher)
                binding.edMessage.text.clear()
            }
            STEP_UPLOAD_PIC -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_PROFILE_PIC,
                        getString(R.string.reg_question7)
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_TEXT
                binding.edMessage.isEnabled = false
                binding.edMessage.hint = getString(R.string.reg_upload)
                binding.edMessage.text.clear()
            }
            STEP_PASSWORD -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        getString(R.string.reg_question8)
                    )
                )
                binding.edMessage.inputType = TYPE_TEXT_VARIATION_PASSWORD or TYPE_CLASS_TEXT
                binding.edMessage.isEnabled = true
                binding.edMessage.hint = getString(R.string.reg_pass)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(100)))
                binding.edMessage.text.clear()
            }
            STEP_CONFIRM_PASSWORD -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        getString(R.string.reg_question9)
                    )
                )
                binding.edMessage.isEnabled = true
                binding.edMessage.inputType = TYPE_TEXT_VARIATION_PASSWORD or TYPE_CLASS_TEXT
                binding.edMessage.hint = getString(R.string.reg_confirm_pass)
                binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(100)))
                binding.edMessage.text.clear()
            }
        }
        regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
        binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
    }


    fun setCnicType() {
        cnicWatcher = object : TextWatcher {
            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(@NonNull text: Editable) {
                if (isFormatting) return
                isFormatting = true

                // If deleting hyphen, also delete character before or after it
                if (deletingHyphen && hyphenStart > 0) {
                    if (deletingBackward) {
                        if (hyphenStart - 1 < text.length) {
                            text.delete(hyphenStart - 1, hyphenStart)
                        }
                    } else if (hyphenStart < text.length) {
                        text.delete(hyphenStart, hyphenStart + 1)
                    }
                }
                if (text.length == 5 || text.length == 13) {
                    text.append('-')
                }
                isFormatting = false
            }

            override fun beforeTextChanged(
                @NonNull s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                if (isFormatting) return

                // Make sure user is deleting one char, without a selection
                val selStart = Selection.getSelectionStart(s)
                val selEnd = Selection.getSelectionEnd(s)
                if (s.length > 1 // Can delete another character
                    && count == 1 // Deleting only one character
                    && after == 0 // Deleting
                    && s[start] == '-' // a hyphen
                    && selStart == selEnd
                ) { // no selection
                    deletingHyphen = true
                    hyphenStart = start
                    // Check if the user is deleting forward or backward
                    deletingBackward = selStart == start + 1
                } else {
                    deletingHyphen = false
                }
            }
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
                if (checkAllGranted(grantResults)) {
                    logD(APP_TAG, "all Granted")
                    buttonGetIMEI()

                }

            }
            else -> {
            }
        }
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)

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

    override fun getLayoutRes(): Int {
        return R.layout.activity_registration
    }

    override fun initViewModel(viewModel: RegistrationViewModel) {

    }

}