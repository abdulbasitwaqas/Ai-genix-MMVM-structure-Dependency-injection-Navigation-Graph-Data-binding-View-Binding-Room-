package com.jsbl.genix.views.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.InputType.*
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fondesa.kpermissions.allDenied
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyDenied
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityRegistrationBinding
import com.jsbl.genix.model.NetworkModel.PasswordRequest
import com.jsbl.genix.model.RegChatItem
import com.jsbl.genix.model.questions.QuestionResponse
import com.jsbl.genix.model.questions.QuestionResponseItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.OtpX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.extensions.showConfirmationDialog
import com.jsbl.genix.utils.extensions.showOnlyAlertMessage
import com.jsbl.genix.utils.services.*
import com.jsbl.genix.viewModel.RegistrationViewModelRevamp
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.AUTO_OTP_PERMISSIONS
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CNIC
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CNIC_IMAGE_URLS
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CUSTOMER
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_DATE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_GENDER
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_IMAGE_URL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_POLICY_PASSWORD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_ALL_PERMISSIONS
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_CNIC_CONFIRM
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_CNIC_IMAGE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_EMAIL_VERIFICATION
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_IS_INSURED
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_MOBILE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_MOBILE_VERIFICATION
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_NAME
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_PASSWORD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.NEW_STEP_PASSWORD_CONFIRM
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.REGEX_CONFIRMATION
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.REQUEST_CODE_CNIC
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.REQUEST_CODE_CNIC_UPLOAD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.REQUEST_CODE_PROFILE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.TYPE_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.TYPE_MOBILE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.UPDATE_TYPE_CNIC
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.UPDATE_TYPE_DOB
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.UPDATE_TYPE_GENDER
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.VIEW_TYPE_CNIC
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.VIEW_TYPE_CNIC_PIC
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.VIEW_TYPE_IMAGE_URL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.VIEW_TYPE_MOBILE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.VIEW_TYPE_PASSWORD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.VIEW_TYPE_PROFILE_PIC
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.VIEW_TYPE_RESEND
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.VIEW_TYPE_SKIP
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.VIEW_TYPE_TEXT_ONLY
import com.jsbl.genix.views.adapters.RegChatAdapter
import com.jsbl.genix.views.adapters.RegChatAdapter.Companion.VIEW_RECEIVER
import com.jsbl.genix.views.adapters.RegChatAdapter.Companion.VIEW_SENDER
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.alt_fragment_profile_details.*
import kotlinx.android.synthetic.main.recycler_car_detail_item.*
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import com.karumi.dexter.listener.PermissionDeniedResponse

import com.karumi.dexter.listener.PermissionGrantedResponse

import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.reg_chat_sender.*


class RegistrationRevampActivity :
    BaseActivity<RegistrationViewModelRevamp, ActivityRegistrationBinding>(
        RegistrationViewModelRevamp::class.java
    ) {

    private lateinit var passwordRequest: PasswordRequest
    private lateinit var regChatAdapter: RegChatAdapter
    var stringIMEI: String? = null
    private val dataList: ArrayList<QuestionResponseItem> = ArrayList()
    var identifiersList: List<QuestionResponseItem> = ArrayList<QuestionResponseItem>()

    lateinit var cnicWatcher: TextWatcher
    private var isFormatting = false
    private var deletingHyphen = false
    private var deletingBackward = false
    var customerX: CustomerX? = null

    //    val symbols = "0123456789/?!:;%$!@#$%^&*():?><,./'[]-'/+"
    val symbols = "^[a-zA-Z ]+$"

    private var hyphenStart = 0
    val CITY_CODE = 1
    val COUNTRY_CODE = 2
    var genderData = 1

    //    val smsVerifyCatcher: SmsVerifyCatcher = TODO()
    private lateinit var timer: CountDownTimer

    //
    private var edittext: String = ""
    private var cnicImages = arrayListOf<String>()
    private var cnicDetected: String = ""
    private var dobDetected: String = ""
    private var genderDetected: String = ""
    private var sumUpCnic = ""
    var isCNICConfimation = false
    var isOTPPermission = false
    var isAllPermission = false

    /*   companion object {
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


           const val TYPE_MOBILE: String = "1"
           const val TYPE_EMAIL: String = "2"
           const val TYPE_LOGIN: String = "3"
           const val TYPE_FORGET_PASS: String = "4"

           //for intents
           const val INTENT_CNIC: String = "cnic"
           const val INTENT_MOBILE: String = "Mobile"
           const val INTENT_IMEI: String = "IMEI"
           const val INTENT_IMAGE_URL: String = "imageUrl"
           const val INTENT_CNIC_IMAGE_URLS: String = "cnicImageUrl"
           const val INTENT_CUSTOMER: String = "customer"
           const val INTENT_POLICY_PASSWORD: String = "policyPassword"
           const val INTENT_PIN: String = "pin"
           const val INTENT_FORGET_PASS: String = "forgetPass"
           const val INTENT_SPLASH_FLOW: String = "splashflow"
           const val INTENT_IMAGE_FILE_ABS_PATH: String = "imagePath"
           const val REGEX_PASSWORD: String =
               "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}\$"

           //Request codes
           const val REQUEST_CODE_PROFILE = 10
           const val REQUEST_CODE_CNIC = 11
           const val REQUEST_CODE_CNIC_UPLOAD = 12
       }
   */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setHeader()


        /* requestPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
             .collect { permissions ->
                 // here you get the result of the requests, permissions holds a list of Permission requests and you can check if all of them have been granted:
                 val allGranted = permissions.find { !it.isGranted } == null
                 // or iterate over the permissions and check them one by one
                 permissions.forEach {
                     val granted = it.isGranted
                     // ...
                 }
             }*/

        if (haveNetworkConnection()) {

            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
                if (checkPhoneStatePermissionReg(this)) {
                    buttonGetIMEI()
                }
            } else {
                buttonGetIMEI()

            }


            /*  smsVerifyCatcher = SmsVerifyCatcher(this,
                  OnSmsCatchListener<String?> { message ->
                      if (viewModel.currentQuestionItem!!.identifier == NEW_STEP_MOBILE_VERIFICATION || viewModel.currentQuestionItem!!.identifier == AUTO_OTP_PERMISSIONS) {
                          val otp = getOtp(message)
                          logD(APP_TAG, "Otp : $otp")
                          if (otp.isNotEmpty())
                              binding.edMessage.setText(otp)
                      }
                      */
            /* val code: String = parseCode(message) //Parse verification code
                     etCode.setText(code)*/
            /*           // set code in edit text
                                 // then you can send verification code to server
                                 // showShort(this, message)*/
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
                //            timer.start()
                viewModel.getQuestions()
            }
        }


//        basicAlert()
    }

    override fun onStart() {
        super.onStart()
        try {
//            smsVerifyCatcher.onStart()
        } catch (e: Exception) {

        }
    }

    override fun onStop() {
        super.onStop()
        try {
//            smsVerifyCatcher.onStop()
        } catch (e: Exception) {

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
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event.action === KeyEvent.ACTION_DOWN
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
                setNextQuestion(null)
                // OR
                // String returnedResult = data.getDataString();
            }
        } else if (requestCode == REQUEST_CODE_CNIC_UPLOAD) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    cnicImages = data?.getStringArrayListExtra(INTENT_CNIC_IMAGE_URLS)!!
                    cnicDetected = data?.getStringExtra(INTENT_CNIC)!!
                    dobDetected = data?.getStringExtra(INTENT_DATE)!!
                    genderDetected = data?.getStringExtra(INTENT_GENDER)!!
                    viewModel.setCNICUrls(cnicImages)
                    viewModel.setCnic(cnicDetected)
                    viewModel.setDob(dobDetected)
                    viewModel.setGender(genderDetected)
                    formatCNICDetails()
                    sendActions()
                    setNextQuestion(null)
                    regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                    binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                } catch (e: Exception) {
                    showShort(this, "Couldn't get Images. Please try again.")
                }
            }
        } else if (requestCode == REQUEST_CODE_CNIC) {
            if (resultCode == Activity.RESULT_OK) {
                edittext = data?.getStringExtra(INTENT_CNIC)!!
//                sendActions()
                binding.edMessage.setText(edittext)
                regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
            }
        }
    }

    private fun formatCNICDetails() {
        sumUpCnic =
            "$UPDATE_TYPE_CNIC ${cnicDetected}\n$UPDATE_TYPE_DOB ${dobDetected}\n$UPDATE_TYPE_GENDER $genderDetected"
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
                            when (viewModel.currentQuestionItem.identifier) {
                                /* NEW_STEP_CNIC -> startActivityForResult(
                                     Intent(
                                         this@RegistrationRevampActivity,
                                         ActivityScanCNIC::class.java
                                     ),
                                     REQUEST_CODE_CNIC
                                 )*/
                                NEW_STEP_CNIC_IMAGE -> {
                                    //TODO Done for upload CNIC Pic
                                    sendForm()


                                }
                                else -> {
                                    showShort(this, "Invalid Operation")
                                }
                            }
                        }
                        VIEW_TYPE_PROFILE_PIC -> {
                            /*if (viewModel.regStep == STEP_UPLOAD_PIC) {
                                sendForm()

                            } else {
                                showShort(this, "Invalid Operation")
                            }*/
                        }
                    }
                }
//                showShort(this, "SendClicked")
            }
            R.id.resend -> {
                when (viewModel.currentQuestionItem.identifier) {
                    NEW_STEP_MOBILE_VERIFICATION -> {
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
                    NEW_STEP_EMAIL_VERIFICATION -> {
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
//                binding.loadingProgress.gone()
//                if (isCNICConfimation) {
                isCNICConfimation = false
                setNextQuestion(null)
                regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                skip.isEnabled = false
//                } else if (isOTPPermission) {
//                    isOTPPermission = false
//                    if (otpPermission(this)) {
//                        sendForm()
//                    }
//                }
//                else if (isAllPermission){
//                    isAllPermission = false
//                    if (checkAllPermission(this)){
//                        setNextQuestion(null)
//                    }
//                }
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

    override fun onSuccess(obj: RequestHandler) {
        binding.loadingProgress.gone()


        if (viewModel.resend) {
            viewModel.resend = false
        } else {
            when (obj.any) {
                is CustomerX -> {
                    val rr = obj.any as CustomerX

                    logD("**token", "token: " + rr.token)
//                    viewModel.prefsHelper.setScopeToken(""+rr.token)
                    viewModel.prefsHelper.updateAuth("" + rr.token)

                    setNextQuestion(rr)
                }
                is QuestionResponse -> {
                    binding.loadingProgress.gone()
                    viewModel.questionResponse = obj.any as QuestionResponse
                    inActiveQuestions(viewModel.questionResponse!!)

                    /*  for (idenList in viewModel.questionResponse!!) {
                              dataList.add(idenList)

                              val intArray = intArrayOf(idenList.identifier)
                              intArray.sorted()
                          dataList.sortBy { it.iD }

                          }*/

                    /*     val productByModel: MutableList<QuestionResponseItem> = java.util.ArrayList<QuestionResponseItem>()
                         for (identifers in viewModel.questionResponse!!){
                             productByModel.add(identifers)
                         }
                         Collections.sort(viewModel.questionResponse!!,
                             Comparator { o1, o2 -> o1.identifier.compareTo(o2.identifier) })
                         logD("**identifierssss", ""+ productByModel.get(0).identifier)
     */

                    askQuestion()
                }

                else -> {
                    setNextQuestion(null)

                }

            }
        }


    }

    override fun onError(obj: RequestHandler) {
        binding.loadingProgress.gone()
        if (!isLastQuestion()) {
            if (viewModel.chatList.size == 0) {
                return
            }
            viewModel.chatList.removeAt(viewModel.chatList.size - 1)
            regChatAdapter.notifyItemRemoved(viewModel.chatList.size)
            binding.recycler.smoothScrollToPosition(viewModel.chatList.size)
        }
        /*if (t.any is RequestResponse) {
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
                if ((obj.any as Response<*>).errorBody() != null) {
                    val msg2 = (obj.any as Response<*>).errorBody()!!.string()

                    //decryption start
                    try {
                        val gsonError = Gson()
                        val typeTokenError: TypeToken<SendEncryptionRequest> =
                            object : TypeToken<SendEncryptionRequest>() {}
                        val errorMsg: SendEncryptionRequest =
                            gsonError.fromJson(msg2, typeTokenError.type)
                        val jsonString: String = errorMsg?.getText().toString()
                        val decrypted: String = Cryptography_Android.Decrypt(
                            jsonString,
                            Constaint.mKey
                        )
                        val gson = Gson()
                        val token: TypeToken<String> =
                            object : TypeToken<String>() {}
                        val error: String = gson.fromJson(decrypted, token.type)
                        // decryption end

                        showOnlyAlertMessage(
                            context = this@RegistrationRevampActivity,
                            title = "Registration",
                            msg = error
                        )
                    } catch (e: Exception) {

                        showOnlyAlertMessage(
                            context = this@RegistrationRevampActivity,
                            title = "Registration",
                            msg = msg2
                        )
                    }

                }
            } else if (obj.any is String) {
                showOnlyAlertMessage(
                    context = this@RegistrationRevampActivity,
                    title = "Registration",
                    msg = obj.any as String,
                    onPositiveClick = {
                        val intentM = Intent(
                            this@RegistrationRevampActivity,
                            com.jsbl.genix.views.activities.ActLogin::class.java
                        )
                        startActivity(intentM)
                        finish()
                    }
                )
            }

        }
    }

    private fun inActiveQuestions(questionResponse: QuestionResponse) {
        for (quest in questionResponse) {
            /*when (quest.step) {
                6 -> {
                    quest.isActive = false
                }
            }*/
        }
    }

    private fun askQuestion() {
        if (viewModel.checkCurrentQuestionItem()) {
            // if you wanna ask question without ans then add its identifier here.
            if (viewModel.currentQuestionItem.identifier == NEW_STEP_CNIC_CONFIRM ||
                viewModel.currentQuestionItem.identifier == AUTO_OTP_PERMISSIONS ||
                viewModel.currentQuestionItem.identifier == NEW_STEP_ALL_PERMISSIONS
            ) {
                logD(APP_TAG, "***identifier : ${viewModel.currentQuestionItem.identifier}")
            } else {
                if (isLastQuestion()) {
                    return
                }
            }
        } else {
            if (isLastQuestion()) {
                return
            }
        }


        if (viewModel.checkCurrentQuestionItem()) {
            if (viewModel.currentQuestionItem.identifier == NEW_STEP_CNIC_CONFIRM) {
                if (isCNICConfimation) {
                    setupQuestion(viewModel.currentQuestionItem)
                    setEditTextConfig(viewModel.currentQuestionItem)
                    regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                    binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                    return
                }
            }
        }

        /*if (viewModel.checkCurrentQuestionItem()) {
            if (viewModel.currentQuestionItem.step == NEW_STEP_EMAIL) {
                viewModel.currentQuestionItem = viewModel.otpQuestion
                if (!viewModel.currentQuestionItem.question.isNullOrEmpty()) {
                    if (viewModel.currentQuestionItem.isActive!!) {
                        setupQuestion(viewModel.currentQuestionItem)
                        setEditTextConfig(viewModel.currentQuestionItem)
                        return
                    }
                }
            }
        }*/

        if (viewModel.checkCurrentQuestionItem()) {
            if (viewModel.currentQuestionItem.identifier == NEW_STEP_CNIC_IMAGE) {
                viewModel.currentQuestionItem = viewModel.confirmationQuestion
                if (!viewModel.currentQuestionItem.question.isNullOrEmpty()) {
                    if (viewModel.currentQuestionItem.isActive!!) {
                        setupQuestion(viewModel.currentQuestionItem)
                        setEditTextConfig(viewModel.currentQuestionItem)
                        isCNICConfimation = true
                        regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                        binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                        return
                    }
                }
            }
        }

        if (!viewModel.questionResponse.isNullOrEmpty()) {
            if (viewModel.nextQuestion < viewModel.questionResponse!!.size) {
                viewModel.currentQuestionItem = viewModel.questionResponse!![viewModel.nextQuestion]
                viewModel.currentQuestion = viewModel.nextQuestion
                if (!viewModel.currentQuestionItem.question.isNullOrEmpty()) {
                    if (viewModel.currentQuestionItem.isActive!!) {


                        if (viewModel.currentQuestionItem.identifier == NEW_STEP_ALL_PERMISSIONS) {
//                            if (checkAllPermission(this)) {
                            setupQuestion(viewModel.currentQuestionItem)
                            setEditTextConfig(viewModel.currentQuestionItem)
                            regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                            binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                            sendForm()
//                            }
                        } else if (viewModel.currentQuestionItem.identifier == AUTO_OTP_PERMISSIONS) {
                            setupQuestion(viewModel.currentQuestionItem)
                            setEditTextConfig(viewModel.currentQuestionItem)
                            regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                            binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                            sendForm()
                        } else if (viewModel.currentQuestionItem.identifier != NEW_STEP_ALL_PERMISSIONS || viewModel.currentQuestionItem.identifier != AUTO_OTP_PERMISSIONS) {
                            setupQuestion(viewModel.currentQuestionItem)
                            setEditTextConfig(viewModel.currentQuestionItem)
                            regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                            binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                        }
                        regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                        binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                    }
                }
                checkNextAvailable(viewModel.nextQuestion + 1)
                regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
            }
            regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
            binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
        }

        regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
        binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
    }


    private fun askQuestion(questionResponseItem: MutableList<QuestionResponseItem>) {
        if (viewModel.checkCurrentQuestionItem()) {
            if (viewModel.currentQuestionItem.identifier != NEW_STEP_CNIC_CONFIRM) {
                if (isLastQuestion()) {
                    return
                }
            }
        } else {
            if (isLastQuestion()) {
                return
            }
        }
        if (viewModel.checkCurrentQuestionItem()) {
            if (viewModel.currentQuestionItem.identifier == NEW_STEP_CNIC_CONFIRM) {
                if (isCNICConfimation) {
                    setupQuestion(viewModel.currentQuestionItem)
                    setEditTextConfig(viewModel.currentQuestionItem)
                    return
                }
            }
        }
        /*if (viewModel.checkCurrentQuestionItem()) {
            if (viewModel.currentQuestionItem.step == NEW_STEP_EMAIL) {
                viewModel.currentQuestionItem = viewModel.otpQuestion
                if (!viewModel.currentQuestionItem.question.isNullOrEmpty()) {
                    if (viewModel.currentQuestionItem.isActive!!) {
                        setupQuestion(viewModel.currentQuestionItem)
                        setEditTextConfig(viewModel.currentQuestionItem)
                        return
                    }
                }
            }
        }*/
        if (viewModel.checkCurrentQuestionItem()) {
            if (viewModel.currentQuestionItem.identifier == NEW_STEP_CNIC_IMAGE) {
                viewModel.currentQuestionItem = viewModel.confirmationQuestion
                if (!viewModel.currentQuestionItem.question.isNullOrEmpty()) {
                    if (viewModel.currentQuestionItem.isActive!!) {
                        setupQuestion(viewModel.currentQuestionItem)
                        setEditTextConfig(viewModel.currentQuestionItem)
                        isCNICConfimation = true
                        return
                    }
                }
            }
        }
        if (!viewModel.questionResponse.isNullOrEmpty()) {
            if (viewModel.nextQuestion < viewModel.questionResponse!!.size) {
                viewModel.currentQuestionItem = viewModel.questionResponse!![viewModel.nextQuestion]
                viewModel.currentQuestion = viewModel.nextQuestion
                if (!viewModel.currentQuestionItem.question.isNullOrEmpty()) {
                    if (viewModel.currentQuestionItem.isActive!!) {
                        setupQuestion(viewModel.currentQuestionItem)
                        setEditTextConfig(viewModel.currentQuestionItem)
                    }
                }
                checkNextAvailable(viewModel.nextQuestion + 1)

            }
            regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
            binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
        }
        regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
        binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
    }

    private fun checkNextAvailable(nextItem: Int) {
        if (!viewModel.questionResponse.isNullOrEmpty()) {
            if (nextItem < viewModel.questionResponse!!.size) {
                val quesRe = viewModel.questionResponse!![nextItem]
                if (!quesRe.question.isNullOrEmpty()) {
                    if (quesRe.isActive!!) {
                        viewModel.nextQuestion = nextItem
                    } else {
                        checkNextAvailable(nextItem + 1)
                    }
                } else {
                    checkNextAvailable(nextItem + 1)
                }
            }
        }
    }

    private fun setEditTextConfig(questionResponseItem: QuestionResponseItem) {
        binding.edMessage.isEnabled = questionResponseItem.lenght != 0
        binding.edMessage.hint = questionResponseItem.placeholder!!
        if (questionResponseItem.lenght != 0) {
            binding.edMessage.setFilters(arrayOf<InputFilter>(LengthFilter(questionResponseItem.lenght!!)))
        }
        binding.edMessage.text.clear()
    }

    private fun setupQuestion(questionResponseItem: QuestionResponseItem) {

        if (questionResponseItem.identifier == NEW_STEP_MOBILE) {
            questionResponseItem.question = "Hello ${viewModel.userName +","} ${questionResponseItem.question}"
        } else
            questionResponseItem.question = "${questionResponseItem.question}"

        //        if (questionResponseItem.identifier != NEW_STEP_NAME)

        when (questionResponseItem.identifier) {

            NEW_STEP_NAME -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_TEXT
            }

            NEW_STEP_MOBILE -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_MOBILE,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType =
                    InputType.TYPE_CLASS_PHONE
            }

            AUTO_OTP_PERMISSIONS -> {
//                isOTPPermission = true
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        questionResponseItem.question!!
                    )
                )
//                    binding.edMessage.inputType = TYPE_CLASS_TEXT
            }

            NEW_STEP_MOBILE_VERIFICATION -> {

                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_RESEND,
                        questionResponseItem.question!!
                    )
                )

                binding.edMessage.inputType = TYPE_CLASS_NUMBER

            }


            NEW_STEP_EMAIL -> {

                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_TEXT
            }


            NEW_STEP_IS_INSURED -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_TEXT
            }


            NEW_STEP_EMAIL_VERIFICATION -> {

                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_RESEND,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_NUMBER

            }


            NEW_STEP_ALL_PERMISSIONS -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_TEXT
            }

            NEW_STEP_CNIC_IMAGE -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_CNIC,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_NUMBER
            }
            NEW_STEP_CNIC_CONFIRM -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_SKIP,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType = TYPE_CLASS_TEXT
            }

            NEW_STEP_PASSWORD -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType = TYPE_TEXT_VARIATION_PASSWORD or TYPE_CLASS_TEXT
            }
            NEW_STEP_PASSWORD_CONFIRM -> {
                viewModel.chatList.add(
                    RegChatItem(
                        VIEW_SENDER,
                        VIEW_TYPE_TEXT_ONLY,
                        questionResponseItem.question!!
                    )
                )
                binding.edMessage.inputType = TYPE_TEXT_VARIATION_PASSWORD or TYPE_CLASS_TEXT
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
        if (!viewModel.checkCurrentQuestionItem()) {
            return
        }
        if (binding.loadingProgress.visibility == VISIBLE) {
            showShort(this, "Please wait while loading")
            return
        }
        if (stringIMEI == null) {
            showShort(this, "IMEI number required")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                ActivityWelcome.REQUEST_READ_PHONE_STATE
            )
            return
        }
        edittext = binding.edMessage.text.toString().trim()
        when (viewModel.currentQuestionItem.identifier) {

            NEW_STEP_MOBILE -> {
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


            NEW_STEP_MOBILE_VERIFICATION -> {
                if (edittext.isNotEmpty()) {
                    if (edittext.length == 6) {

                        viewModel.verifyOtp(edittext, TYPE_MOBILE, "", stringIMEI!!)
//                        setNextQuestion()
                        Handler(Looper.getMainLooper()).postDelayed({
//                            regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                            binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                        }, 700)

                    } else {
                        showShort(this, "Please enter six digit code")
                    }
                } else {
                    showShort(this, "Please enter pin code")
                }


            }


            NEW_STEP_EMAIL -> {
                if (edittext.isNotEmpty()) {

                    if (edittext.matches(Regex(ActivityRegistration.REGEX_NEW_EMAIL))) {

                        if (viewModel.currentQuestionItem.identifier == NEW_STEP_EMAIL) {
                            viewModel.askOtp(
                                viewModel.mobileNumber,
                                TYPE_EMAIL,
                                edittext,
                                stringIMEI!!
                            )
                        }
                    } else {
                        showShort(this, "Invalid email address")
                    }
                } else {
                    showShort(this, "Please enter email address")
                }
            }


            NEW_STEP_EMAIL_VERIFICATION -> {
                if (edittext.isNotEmpty()) {
                    if (edittext.length == 6) {
                        viewModel.verifyOtp(edittext, TYPE_EMAIL, viewModel.email, stringIMEI!!)
                    } else {
                        showShort(this, "Please enter six digit code")
                    }
                } else {
                    showShort(this, "Please enter pin code")
                }

            }

            NEW_STEP_NAME -> {

                if (edittext.isNotEmpty()) {
                    if (edittext.matches(Regex(symbols))) {
                        viewModel.registerEntry(edittext.capitalize())
                    } else {
                        showShort(this, "Please enter valid User name")
                    }
                } else {
                    showShort(this, "Please enter name")
                }
            }
            NEW_STEP_IS_INSURED -> {
                if (edittext.isNotEmpty() && edittext.length == 1) {
                    if (edittext == "Y" || edittext == "y") {
                        viewModel.registerEntry("true")
                        regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                        binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                    } else if (edittext == "N" || edittext == "n") {
                        viewModel.registerEntry("true")
                        regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
                        binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)
                    } else {
                        showShort(this, "Kinldy enter your answer in y/n")
                    }

                } else {
                    showShort(this, "Kinldy enter your answer in y/n")
                }

            }


            AUTO_OTP_PERMISSIONS -> {
                if (otpPermission(this)) {
                    viewModel.registerEntry("OTP permission granted")

                }
            }

            NEW_STEP_ALL_PERMISSIONS -> {
//                if (checkAllPermission(this)) {
                viewModel.registerEntry("All permissions granted")
//                }
            }

            NEW_STEP_CNIC_CONFIRM -> {
                if (edittext.isNotEmpty()) {
//                    if (edittext.contains(REGEX_CONFIRMATION.toRegex())) {
                    val splitter = edittext.split(" ")
                    if (splitter[0].equals(UPDATE_TYPE_CNIC, true)) {
                        if (splitter[1].contains(ActivityRegistration.REGEX_CNIC.toRegex()) || splitter[1].contains(
                                ActivityRegistration.REGEX_CNIC2.toRegex()
                            )
                        ) {
                            cnicDetected = splitter[1]
                            viewModel.setCnic(cnicDetected)
                            formatCNICDetails()
                            sendActions()
                            setNextQuestion(null)
                        } else {
                            showShort(this, "Invalid CNIC")
                        }
                    } else if (splitter[0].equals(UPDATE_TYPE_DOB, true)) {
                        if (splitter[1].contains(ActivityRegistration.REGEX_DATE.toRegex())) {
                            dobDetected = splitter[1]
                            viewModel.setDob(dobDetected)
                            formatCNICDetails()
                            sendActions()
                            setNextQuestion(null)
                        } else {
                            showShort(this, "Invalid DOB")
                        }
                    } else if (splitter[0].equals(UPDATE_TYPE_GENDER, true)) {
                        if (splitter[1].contains(ActivityRegistration.REGEX_GENDER.toRegex())) {
                            genderDetected = splitter[1]
                            viewModel.setGender(genderDetected)
                            formatCNICDetails()
                            sendActions()
                            setNextQuestion(null)
                        } else {
                            showShort(this, "Invalid Gender")
                        }
                    } else {
                        showShort(this, "Invalid Input")
                    }
                    /*   } else {
                           showShort(this, "Invalid Input type")

                       }*/

                } else {
                    showShort(this, "Invalid Input")
                }
            }


            NEW_STEP_PASSWORD -> {
                if (validatePassword()) {

                    /*  viewModel.regStep++
                      sendActions()
                      setNextQuestion(null)*/

                    viewModel.pass = edittext
                    passwordRequest = PasswordRequest(edittext)
                    /*  val json = Gson().toJson(passwordRequest)
                      var Request: String? = null

                      try {
                          Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
                      } catch (e: java.lang.Exception) {
                          e.printStackTrace()
                      }

                      val Text = Request
                      val sendEncrptRequest = SendEncryptionRequest()
                      sendEncrptRequest.setText(Text)*/
                    viewModel.validatePassword(passwordRequest)
                }
//                    viewModel.registerEntry(binding.edMessage.text.toString().trim())

            }
            NEW_STEP_PASSWORD_CONFIRM -> {
                if (validatePassword()) {
                    if (viewModel.pass.equals(edittext)) {
                        viewModel.registerEntry(edittext)
                        viewModel.prefsHelper.setRegPassword(viewModel.pass)
//                        setNextQuestion()
                    } else {
                        showShort(this, "Password not matched")

                    }
                }
            }

            /* NEW_STEP_CNIC -> {

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
             }*/

            NEW_STEP_CNIC_IMAGE -> {
                startActivityForResult(
                    Intent(this@RegistrationRevampActivity, ActCaptureCNICRevamp::class.java),
                    REQUEST_CODE_CNIC_UPLOAD
                )
                //                viewModel.registerEntry(binding.edMessage.text.toString().trim())
                setNextQuestion(null)
            }
        }

    }

    private fun validatePassword(): Boolean {
        return if (edittext.isNotEmpty()) {
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
        /* if (viewModel.regStep == STEP_UPLOAD_PIC) {
             viewModel.chatList.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_IMAGE_URL, edittext))
         } else*/
        if (viewModel.currentQuestionItem.identifier == NEW_STEP_CNIC_IMAGE) {
            viewModel.chatList.add(
                RegChatItem(
                    VIEW_RECEIVER,
                    VIEW_TYPE_CNIC_PIC,
                    sumUpCnic,
                    cnicImages
                )
            )
        } else if (viewModel.currentQuestionItem.identifier == NEW_STEP_CNIC_CONFIRM) {
            viewModel.chatList.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_TEXT_ONLY, sumUpCnic))

        } else if (viewModel.currentQuestionItem.identifier == NEW_STEP_PASSWORD || viewModel.currentQuestionItem.identifier == NEW_STEP_PASSWORD_CONFIRM) {
            viewModel.chatList.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_PASSWORD, edittext))
        } else {
            viewModel.chatList.add(RegChatItem(VIEW_RECEIVER, VIEW_TYPE_TEXT_ONLY, edittext))
        }
        regChatAdapter.notifyItemInserted(viewModel.chatList.size - 1)
        binding.recycler.smoothScrollToPosition(viewModel.chatList.size - 1)

    }

    private fun isLastQuestion(): Boolean {
        return if (viewModel.chatList.isEmpty()) {
            false
        } else {
            viewModel.chatList.last().sender == VIEW_SENDER
        }
    }


    override fun onBackPressed() {
        showConfirmationDialog(
            this,
            title = "Registration",
            msg = "Are you sure you want to quit the registration process?",
            onPositiveClick = {
                finish()
            }
        )
    }

    private fun moveNext(customer: CustomerX) {
        if (!customer.carDetails.isNullOrEmpty()) {
            val intentM = Intent(
                this@RegistrationRevampActivity,
                com.jsbl.genix.views.activities.ActivityMain::class.java
            ).putExtra("show_referance_dialog", false)
            startActivity(intentM)
            finishAffinity()
        } else {
            moveToCarDetail(customer)
        }

    }

    private fun moveToCarDetail(customer: CustomerX) {

        val intentM = Intent(
            this@RegistrationRevampActivity,
            com.jsbl.genix.views.activities.ActCarDetails::class.java
        )
        intentM.putExtra(INTENT_CUSTOMER, customer)
        intentM.putExtra(INTENT_POLICY_PASSWORD, viewModel.pass)
//            intentM.putExtra(INTENT_CUTOMER, viewModel.getCustomer())
        startActivity(intentM)
        finish()

    }


    fun setNextQuestion(customer: CustomerX?) {
        if (viewModel.questionResponse!!.indexOf(viewModel.currentQuestionItem)!! > viewModel.questionResponse!!.size - 2) {
            if (customer != null)
                customerX = customer
//                basicAlert(customer)

            printDialogShow(
                this,
                "Congratulations!",
                "" + resources.getString(R.string.permissions_detail),
                customer!!
            )

            /*     showOnlyAlertMessage(
                     context = this@RegistrationRevampActivity,
                     title = "Registered",
                     msg = "Congratulations, you have been Registered successfully.",
                     onPositiveClick = {
                         if (checkAllPermission(this)) {
                             moveNext(customer!!)
                         }
                     }
                 )*/
//            moveNext()
            return
        }
/*

        val productByModel: MutableList<QuestionResponseItem> = java.util.ArrayList<QuestionResponseItem>()
        for (identifers in viewModel.questionResponse!!){
            productByModel.add(identifers)
        }
        Collections.sort(viewModel.questionResponse!!,
            Comparator { o1, o2 -> o1.identifier.compareTo(o2.identifier) })
        logD("**identifierssss", ""+ productByModel.get(0).identifier)
*/
        askQuestion()

    }


    fun printDialogShow(
        activity: Activity,
        st_header: String,
        st_permission: String,
        customer: CustomerX
    ) {
        val dialog = Dialog(activity)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.all_permissions_layout)
        val permissionHeaderTV = dialog.findViewById<TextView>(R.id.permissionHeaderTV)
        val permissionsTV = dialog.findViewById<TextView>(R.id.permissionsTV)
        val userNameTVHeader = dialog.findViewById<TextView>(R.id.userNameTVHeader)
        val okBtn = dialog.findViewById<Button>(R.id.okBtn)
        val learnMoreBtn = dialog.findViewById<Button>(R.id.learnMoreBtn)
        permissionHeaderTV.text = st_header
        val str1 = "You are registered successfully,"
        val str2 =
            "!. For better use of this application, we need permissions to access Location so that we can detect your driving speed"
        permissionsTV.text = "$str1 ${customer.name}$str2"
        val your_user_name = "Your user name is: "
        userNameTVHeader.text = "$your_user_name ${customer.userName}"

        okBtn.setOnClickListener {

            getPermissions()
            dialog.dismiss()

            /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                   permissionsBuilder(
                       Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.ACCESS_COARSE_LOCATION,
                       Manifest.permission.CAMERA,
                       Manifest.permission.WRITE_EXTERNAL_STORAGE,
                       Manifest.permission.READ_EXTERNAL_STORAGE,
                       Manifest.permission.READ_PHONE_STATE,
  //                     Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                       Manifest.permission.ACTIVITY_RECOGNITION
                   ).build().send() {
                       if (it.allGranted()) {
                           customerX?.let { moveNext(it) }
                       }
                   }

               }
               else {
                   permissionsBuilder(
                       Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.ACCESS_COARSE_LOCATION,
                       Manifest.permission.CAMERA,
                       Manifest.permission.WRITE_EXTERNAL_STORAGE,
                       Manifest.permission.READ_EXTERNAL_STORAGE,
                       Manifest.permission.READ_PHONE_STATE,
                   ).build().send() {
                       if (it.allGranted()) {
                           customerX?.let { moveNext(it) }
                       }
                   }

               }*/

            /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {


                  Dexter.withActivity(this)
                      .withPermission(Manifest.permission.CAMERA)
                      .withListener(object : PermissionListener {
                          override fun onPermissionGranted(response: PermissionGrantedResponse) {
                              // permission is granted, open the camera
                          }

                          override fun onPermissionDenied(response: PermissionDeniedResponse) {
                              // check for permanent denial of permission
                              if (response.isPermanentlyDenied) {
                                  // navigate user to app settings
                              }
                          }

                          override fun onPermissionRationaleShouldBeShown(
                              permission: PermissionRequest,
                              token: PermissionToken
                          ) {
                              token.continuePermissionRequest()
                          }
                      }).check()


                  Dexter.withActivity(this)
                      .withPermissions(
                          Manifest.permission.ACCESS_FINE_LOCATION,
                          Manifest.permission.ACCESS_COARSE_LOCATION,
                          Manifest.permission.CAMERA,
                          Manifest.permission.WRITE_EXTERNAL_STORAGE,
                          Manifest.permission.READ_EXTERNAL_STORAGE,
                          Manifest.permission.READ_PHONE_STATE,
                          Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                          Manifest.permission.ACTIVITY_RECOGNITION
                      )
                      .withListener(object : MultiplePermissionsListener {
                          override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                              // check if all permissions are granted
                              if (report.areAllPermissionsGranted()) {
                                  customerX?.let { moveNext(it) }
                              } else{

                                  Dexter.withActivity(this@RegistrationRevampActivity)
                                      .withPermissions(
                                          Manifest.permission.ACCESS_FINE_LOCATION,
                                          Manifest.permission.ACCESS_COARSE_LOCATION,
                                          Manifest.permission.CAMERA,
                                          Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                          Manifest.permission.READ_EXTERNAL_STORAGE,
                                          Manifest.permission.READ_PHONE_STATE,
                                          Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                          Manifest.permission.ACTIVITY_RECOGNITION
                                      )
                                      .withListener(object : MultiplePermissionsListener {
                                          override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                              // check if all permissions are granted
                                              if (report.areAllPermissionsGranted()) {
                                                  customerX?.let { moveNext(it) }
                                              }

                                              // check for permanent denial of any permission
                                              if (report.isAnyPermissionPermanentlyDenied) {
                                                  // permission is denied permenantly, navigate user to app settings
                                              }
                                          }

                                          override fun onPermissionRationaleShouldBeShown(
                                              permissions: List<PermissionRequest?>?,
                                              token: PermissionToken
                                          ) {
                                              token.continuePermissionRequest()
                                          }
                                      })
                                      .onSameThread()
                                      .check()
                              }

                              // check for permanent denial of any permission
                              if (report.isAnyPermissionPermanentlyDenied) {
                                  // permission is denied permenantly, navigate user to app settings
                              }
                          }

                          override fun onPermissionRationaleShouldBeShown(
                              permissions: List<PermissionRequest?>?,
                              token: PermissionToken
                          ) {
                              token.continuePermissionRequest()
                          }
                      })
                      .onSameThread()
                      .check()



              }
              else {
                  Dexter.withActivity(this)
                      .withPermissions(
                          Manifest.permission.ACCESS_FINE_LOCATION,
                          Manifest.permission.ACCESS_COARSE_LOCATION,
                          Manifest.permission.CAMERA,
                          Manifest.permission.WRITE_EXTERNAL_STORAGE,
                          Manifest.permission.READ_EXTERNAL_STORAGE,
                          Manifest.permission.READ_PHONE_STATE
                      )
                      .withListener(object : MultiplePermissionsListener {
                          override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                              // check if all permissions are granted
                              if (report.areAllPermissionsGranted()) {
                                  customerX?.let { moveNext(it) }
                              } else {
                                  Dexter.withActivity(this@RegistrationRevampActivity)
                                      .withPermissions(
                                          Manifest.permission.ACCESS_FINE_LOCATION,
                                          Manifest.permission.ACCESS_COARSE_LOCATION,
                                          Manifest.permission.CAMERA,
                                          Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                          Manifest.permission.READ_EXTERNAL_STORAGE,
                                          Manifest.permission.READ_PHONE_STATE
                                      )
                                      .withListener(object : MultiplePermissionsListener {
                                          override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                              // check if all permissions are granted
                                              if (report.areAllPermissionsGranted()) {
                                                  customerX?.let { moveNext(it) }
                                              }

                                              // check for permanent denial of any permission
                                              if (report.isAnyPermissionPermanentlyDenied) {
                                                  // permission is denied permenantly, navigate user to app settings
                                              }
                                          }

                                          override fun onPermissionRationaleShouldBeShown(
                                              permissions: List<PermissionRequest?>?,
                                              token: PermissionToken
                                          ) {
                                              token.continuePermissionRequest()
                                          }
                                      })
                                      .onSameThread()
                                      .check()
                              }

                              // check for permanent denial of any permission
                              if (report.isAnyPermissionPermanentlyDenied) {
                                  // permission is denied permenantly, navigate user to app settings
                              }
                          }

                          override fun onPermissionRationaleShouldBeShown(
                              permissions: List<PermissionRequest?>?,
                              token: PermissionToken
                          ) {
                              token.continuePermissionRequest()
                          }
                      })
                      .onSameThread()
                      .check()
              }*/

            /*if (checkCameraPermission(this)) {
                if (checkLocPermission(this)) {
                    if (checkCoarseLocPermission(this)) {
                        if (checkReadExterStPermission(this)) {
                            if (checkWriteExterStPermission(this)) {
                                if (checkPhoneStatePermission(this)) {
                                    if (checkRecognitionPermission(this)) {
                                        if (checkAccessBgLocation(this)) {
                                            customerX?.let { moveNext(it) }
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
            }*/
        }

        learnMoreBtn.setOnClickListener {
            learnMoreDialog(this, customer)
            dialog.dismiss()
        }
        dialog.show()
    }


    fun learnMoreDialog(activity: Activity, customer: CustomerX) {
        val dialog = Dialog(activity)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.learn_more_dialog)
        val permissionHeaderTV = dialog.findViewById<TextView>(R.id.permissionHeaderTV)
        val permissionsTV = dialog.findViewById<TextView>(R.id.permissionsTV)
        val okBtn = dialog.findViewById<Button>(R.id.okBtn)


        okBtn.setOnClickListener {
            getPermissions()
            dialog.dismiss()

            /*if (checkAndRequestPermissions(this)){
                customerX?.let { moveNext(it) }
                dialog.dismiss()
            }*/
/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionsBuilder(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ).build().send() {
                    if (it.allGranted()) {
                        customerX?.let { moveNext(it) }
                    }

                }

            }
            else{
                permissionsBuilder(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                ).build().send() {
                    if (it.allGranted()) {
                        customerX?.let { moveNext(it) }
                    }
                }

            }*/


            /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                   Dexter.withActivity(this)
                       .withPermissions(
                           Manifest.permission.ACCESS_FINE_LOCATION,
                           Manifest.permission.ACCESS_COARSE_LOCATION,
                           Manifest.permission.CAMERA,
                           Manifest.permission.WRITE_EXTERNAL_STORAGE,
                           Manifest.permission.READ_EXTERNAL_STORAGE,
                           Manifest.permission.READ_PHONE_STATE,
                           Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                           Manifest.permission.ACTIVITY_RECOGNITION
                       )
                       .withListener(object : MultiplePermissionsListener {
                           override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                               // check if all permissions are granted
                               if (report.areAllPermissionsGranted()) {
                                   customerX?.let { moveNext(it) }
                               } else{

                   Dexter.withActivity(this@RegistrationRevampActivity)
                       .withPermissions(
                           Manifest.permission.ACCESS_FINE_LOCATION,
                           Manifest.permission.ACCESS_COARSE_LOCATION,
                           Manifest.permission.CAMERA,
                           Manifest.permission.WRITE_EXTERNAL_STORAGE,
                           Manifest.permission.READ_EXTERNAL_STORAGE,
                           Manifest.permission.READ_PHONE_STATE,
                           Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                           Manifest.permission.ACTIVITY_RECOGNITION
                       )
                       .withListener(object : MultiplePermissionsListener {
                           override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                               // check if all permissions are granted
                               if (report.areAllPermissionsGranted()) {
                                   customerX?.let { moveNext(it) }
                               }

                               // check for permanent denial of any permission
                               if (report.isAnyPermissionPermanentlyDenied) {
                                   // permission is denied permenantly, navigate user to app settings
                               }
                           }

                           override fun onPermissionRationaleShouldBeShown(
                               permissions: List<PermissionRequest?>?,
                               token: PermissionToken
                           ) {
                               token.continuePermissionRequest()
                           }
                       })
                       .onSameThread()
                       .check()
                               }

                               // check for permanent denial of any permission
                               if (report.isAnyPermissionPermanentlyDenied) {
                                   // permission is denied permenantly, navigate user to app settings
                               }
                           }

                           override fun onPermissionRationaleShouldBeShown(
                               permissions: List<PermissionRequest?>?,
                               token: PermissionToken
                           ) {
                               token.continuePermissionRequest()
                           }
                       })
                       .onSameThread()
                       .check()



               }
               else {
                   Dexter.withActivity(this)
                       .withPermissions(
                           Manifest.permission.ACCESS_FINE_LOCATION,
                           Manifest.permission.ACCESS_COARSE_LOCATION,
                           Manifest.permission.CAMERA,
                           Manifest.permission.WRITE_EXTERNAL_STORAGE,
                           Manifest.permission.READ_EXTERNAL_STORAGE,
                           Manifest.permission.READ_PHONE_STATE
                       )
                       .withListener(object : MultiplePermissionsListener {
                           override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                               // check if all permissions are granted
                               if (report.areAllPermissionsGranted()) {
                                   customerX?.let { moveNext(it) }
                               } else {
                                   Dexter.withActivity(this@RegistrationRevampActivity)
                       .withPermissions(
                           Manifest.permission.ACCESS_FINE_LOCATION,
                           Manifest.permission.ACCESS_COARSE_LOCATION,
                           Manifest.permission.CAMERA,
                           Manifest.permission.WRITE_EXTERNAL_STORAGE,
                           Manifest.permission.READ_EXTERNAL_STORAGE,
                           Manifest.permission.READ_PHONE_STATE
                       )
                       .withListener(object : MultiplePermissionsListener {
                           override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                               // check if all permissions are granted
                               if (report.areAllPermissionsGranted()) {
                                   customerX?.let { moveNext(it) }
                               }

                               // check for permanent denial of any permission
                               if (report.isAnyPermissionPermanentlyDenied) {
                                   // permission is denied permenantly, navigate user to app settings
                               }
                           }

                           override fun onPermissionRationaleShouldBeShown(
                               permissions: List<PermissionRequest?>?,
                               token: PermissionToken
                           ) {
                               token.continuePermissionRequest()
                           }
                       })
                       .onSameThread()
                       .check()
                               }

                               // check for permanent denial of any permission
                               if (report.isAnyPermissionPermanentlyDenied) {
                                   // permission is denied permenantly, navigate user to app settings
                               }
                           }

                           override fun onPermissionRationaleShouldBeShown(
                               permissions: List<PermissionRequest?>?,
                               token: PermissionToken
                           ) {
                               token.continuePermissionRequest()
                           }
                       })
                       .onSameThread()
                       .check()
               }*/

            /*  if (checkCameraPermission(this)) {
                  if (checkLocPermission(this)) {
                      if (checkCoarseLocPermission(this)) {
                          if (checkReadExterStPermission(this)) {
                              if (checkWriteExterStPermission(this)) {
                                  if (checkPhoneStatePermission(this)) {
                                      if (checkRecognitionPermission(this)) {
                                          if (checkAccessBgLocation(this)) {
                                              customerX?.let { moveNext(it) }
                                          }
                                      }

                                  }

                              }
                          }
                      }
                  }
              }*/


        }
        dialog.show()
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
                telephonyManager.deviceId
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


            ActivityMain.REQUEST_ACTIVITY_RECOGNITION -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }
            }
            ActivityMain.REQUEST_READ_PHONE_STATE -> {
                buttonGetIMEI()
            }
            ActivityMain.REQUEST_BACKGROUNDlOCATION -> {
                customerX?.let { moveNext(it) }
            }
            ActivityMain.REQUEST_SMS -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }

            }
            ActivityMain.REQUEST_CAM -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }

            }
            ActivityMain.REQUEST_LOC -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }

            }
            ActivityMain.REQUEST_COARSE_LOC -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }

            }
            ActivityMain.REQUEST_COARSE_LOC -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }

            }
            ActivityMain.REQUEST_READ_ST -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }

            }
            ActivityMain.REQUEST_WRITE_ST -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }

            }
            ActivityMain.REQUEST_READ_PHONE_STATE -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }

            }
            ActivityMain.REQUEST_ACTIVITY_RECOGNITION -> {
                if (checkCameraPermission(this)) {
                    if (checkLocPermission(this)) {
                        if (checkCoarseLocPermission(this)) {
                            if (checkReadExterStPermission(this)) {
                                if (checkWriteExterStPermission(this)) {
                                    if (checkPhoneStatePermission(this)) {
                                        if (checkRecognitionPermission(this)) {
                                            if (checkAccessBgLocation(this)) {
                                                customerX?.let { moveNext(it) }
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }
            }


            ActivityMain.REQUEST_CODE_OTP -> {
                if (checkAllGranted(grantResults)) {
                    logD(APP_TAG, "otp permission Granted")
                    setupQuestion(viewModel.currentQuestionItem)
                    setEditTextConfig(viewModel.currentQuestionItem)
                    sendForm()
                }
            }

            ActivityMain.REQUEST_READ_PHONE_STATE_REG -> {
                if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
                    if (checkPhoneStatePermissionReg(this)) {
                        buttonGetIMEI()
                    }
                } else {
                    buttonGetIMEI()

                }
            }

            else -> {
            }
        }
//        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)

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

    override fun initViewModel(viewModel: RegistrationViewModelRevamp) {

    }

    private fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals(
                    "WIFI",
                    ignoreCase = true
                )
            ) if (ni.isConnected) haveConnectedWifi = true
            if (ni.typeName.equals(
                    "MOBILE",
                    ignoreCase = true
                )
            ) if (ni.isConnected) haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }


    fun getDeviceID(): String? {
        val deviceId: String
        val mTelephony = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        deviceId = if (mTelephony.deviceId != null) {
            mTelephony.deviceId
        } else {
            Secure.getString(applicationContext.contentResolver, Secure.ANDROID_ID)
        }
        return deviceId
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
                    customerX?.let { moveNext(it) }

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
                            startActivity(intent)
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
                            startActivity(intent)
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
                    customerX?.let { moveNext(it) }

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
                            startActivity(intent)
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
                            startActivity(intent)
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
                    customerX?.let { moveNext(it) }

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
                            startActivity(intent)
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
                            startActivity(intent)
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


}


