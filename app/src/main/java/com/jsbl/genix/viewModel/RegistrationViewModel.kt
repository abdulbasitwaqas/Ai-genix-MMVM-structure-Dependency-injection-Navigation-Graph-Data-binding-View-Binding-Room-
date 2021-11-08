package com.jsbl.genix.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.RegChatItem
import com.jsbl.genix.model.questions.QuestionResponse
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.OtpX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.services.findDefaultPosition
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.STEP_CNIC
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.STEP_CONFIRM_PASSWORD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.STEP_EMAIL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.STEP_MARITAL
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.STEP_NAME
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.STEP_PASSWORD
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.STEP_UPLOAD_PIC
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

/**
 * Created by Muhammad Ali on 04-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class RegistrationViewModel(application: Application) : BaseViewModel(application) {

    public lateinit var mobileNumber: String
    public lateinit var email: String
    private lateinit var otpX: OtpX
    public var resend: Boolean = false
    private var registration = CustomerX()
    var regStep = 0
    var pass = ""
    var chatList = arrayListOf<RegChatItem>()
    var isQuestion = false

    private val disposable = CompositeDisposable()

    val error = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    var questionResponse: QuestionResponse? = null

    fun setProfileImage(url: String) {
        registration.profileImagePath = url
    }

    fun setCNICUrls(cnicImagesLink: ArrayList<String>) {
        if (!cnicImagesLink.isNullOrEmpty()) {
            if (cnicImagesLink.size > 1) {
                registration.cNIC_frontImageUrl = cnicImagesLink[0]
                registration.cNIC_backImageUrl = cnicImagesLink[1]
            }
        }
    }

    fun askOtp(number: String, type: String, email: String, imei: String) {
        logD(APP_TAG, "method Called")
        sendRequest()
        mobileNumber = number
        registration.mobile = mobileNumber
        if (email.isNotEmpty()) {
            this.email = email
            registration.email = email
        }
        otpX = OtpX(mobileNumber, type, email, "0", imei)

        val json = Gson().toJson(otpX)
        var Request: String? = null

            try {
                Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)
        localService.getOtp(sendEncrptRequest).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                setError(call, "Internet Connectivity issue")

            }

            override fun onResponse(
                call: Call<SendEncryptionRequest>,
                response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {
                    try {
                        setSuccess(response.body())
                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null)

                }
            }

        })
    }

    fun resendOtp(number: String, type: String, email: String, imei: String) {
        logD(APP_TAG, "method Called")
        resend = true
        sendRequest()
        mobileNumber = number
        if (email.isNotEmpty()) {
            this.email = email
        }
        otpX = OtpX(mobileNumber, type, email, "0", imei)
        val json = Gson().toJson(otpX)
        Log.d("Request",json)
        var Request: String? = ""

            try {
                Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)
        localService.getOtp(sendEncrptRequest).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                setError(call, "Internet Connectivity issue")

            }

            override fun onResponse(
                call: Call<SendEncryptionRequest>,
                response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {
                    try {
                        logD(APP_TAG, "method setSuccess")
                        requestHandlerMLD.value = RequestHandler(
                            loading = false,
                            isSuccess = true,
                            any = response.body()
                        )
                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null)

                }
            }

        })
    }


    fun verifyOtp(otp: String, type: String, email: String, imei: String) {
        sendRequest()
        otpX = OtpX(mobileNumber, type, email, otp, imei)
        registration.mobile = mobileNumber
        registration.phone = mobileNumber
        registration.imei = imei
        val json = Gson().toJson(otpX)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.getVerifyOtpReg(sendEncrptRequest).enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                setError(call, "Internet Connectivity issue")
            }

            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) {
                if (response.code() == 200) {
                    try {
                        setSuccess(response.body())

                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null)

                }
            }

        })
    }
    fun validatePassword(password: SendEncryptionRequest) {
        sendRequest()
        val json = Gson().toJson(password)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.validatePassword(sendEncrptRequest).enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                setError(call,t.message)
            }

            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) {
                if (response.code() == 200) {
                    try {
                        setSuccess(response.body())
                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null, true)

                }
            }

        })
    }


    fun registerEntry(entry: String) {
        sendRequest()
        when (regStep) {
            STEP_EMAIL -> {
                registration.email = entry
            }
            STEP_NAME -> {
                registration.name = entry
            }
            STEP_CNIC -> {
                registration.cNIC = entry
            }
            STEP_MARITAL -> {
                registration.maritalStatus = entry
            }
            STEP_UPLOAD_PIC -> {
                registration.profileImagePath = entry
            }
            STEP_CONFIRM_PASSWORD -> {
                registration.password = entry
            }
        }

        val json = Gson().toJson(registration)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.enterData(sendEncrptRequest).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                setError(call, "Internet Connectivity issue")
            }

            override fun onResponse(
                call: Call<SendEncryptionRequest>,
                response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {

                    try {
                        if (regStep > STEP_PASSWORD) {
                            if (response.body() != null) {
                                val sendEncrptRequest: SendEncryptionRequest? = response.body()
                                val jsonString: String? = sendEncrptRequest?.getText().toString()
                                val decrypted: String = Cryptography_Android.Decrypt(
                                    jsonString,
                                    Constaint.mKey
                                )
                                val obj = JSONObject(decrypted)
                                val gson = Gson()
                                val customerXResponse = gson.fromJson(
                                    obj.toString(),
                                    CustomerX::class.java
                                )
                                if (customerXResponse!!.carDetails.isNullOrEmpty()) {

                                    setSuccess(customerXResponse)
                                    return
                                }
                                prefsHelper.updateAuth(customerXResponse!!.token!!)
                                prefsHelper.updateCustomerId(customerXResponse!!.iD!!)
                                val pos = findDefaultPosition(customerX.carDetails!!)
                                if (pos != -1) {
                                    prefsHelper.updateScopeName(customerXResponse!!.carDetails!![pos].registrationNo!!)
                                    prefsHelper.updateScopePass(registration.password!!)

                                }
                                storeCustomerLocaly(customerXResponse!!)
                            } else {
                                setError(response, null)
                            }
                        } else {
                            setSuccess(response.body())
                        }
                    } catch (e: Exception) {
                        setError(response, null)
                    }

                } else {
                    setError(response, null)

                }

            }

        })
    }

    fun getQuestions() {
        sendRequest()
        localService.getQuestions().enqueue(object : Callback<SendEncryptionRequest> {
            override fun onResponse(
                call: Call<SendEncryptionRequest>,
                response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {
                    val obj:JSONObject
                    val decrypted: String=""
                    try {
                        val sendEncrptRequest: SendEncryptionRequest? = response.body()
                        try {
                            val jsonString: String? = sendEncrptRequest?.getText().toString()
                            val decrypted: String = Cryptography_Android.Decrypt(
                                jsonString,
                                Constaint.mKey
                            )
                             obj = JSONObject(decrypted)
                            val gson = Gson()
                            val loginResponcesModel: QuestionResponse = gson.fromJson(
                                obj.toString(),
                                QuestionResponse::class.java
                            )

                            super@RegistrationViewModel.setSuccess(loginResponcesModel)
                            Log.d("**registrationRes",""+loginResponcesModel)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null)

                }
            }

            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                setError(call, "Internet Connectivity issue")

            }

        })
    }


    private fun storeCustomerLocaly(customer: CustomerX) {
        launch {
            try {
                val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
                val insertResult = customerDao.insertAll(customer)
                setSuccess(customer)
            } catch (e: Exception) {

            }
        }
//        prefsHelper.updateTime(System.nanoTime())
    }

    override fun sendRequest() {
//        regStep++
        super.sendRequest()
    }

    override fun setError(obj: Any?, msg: String?, showAlert: Boolean, t: Throwable?) {
//        regStep++
        super.setError(obj, msg, showAlert, t)
    }

    override fun setSuccess(any: Any?) {
        regStep++
        super.setSuccess(any)
    }

    fun getCustomer(): CustomerX {
        return customerX

    }
}