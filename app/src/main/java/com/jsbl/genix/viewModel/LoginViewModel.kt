package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.redeem.MyRedeemResponse
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.LoginMdl
import com.jsbl.genix.model.registration.OtpX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.services.findDefaultPosition
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*


class LoginViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var mobileNumber: String
    private lateinit var otpX: OtpX
    private lateinit var password: String


    var isForgetPass = false

    fun askOtp(number: String, type: String) {
        logD(APP_TAG, "method Called")
        sendRequest()
        mobileNumber = number
        otpX = OtpX(mobileNumber, type, "", "0")
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
                    } catch (e: java.lang.Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null)

                }
            }

        })
    }


    fun askOtp(number: String, type: String, imei: String) {
        logD(APP_TAG, "method Called")
        sendRequest()
        mobileNumber = number
        otpX = OtpX(mobileNumber, type, "", "0", imei)
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
                response: Response<SendEncryptionRequest>)
            {
                if (response.code() == 200) {
                    try {
                        setSuccess(response.body())
                    } catch (e: java.lang.Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null)
                }
            }

        })
    }

    fun loginUser(login: LoginMdl) {
        logD(APP_TAG, "method Called")
        val json = Gson().toJson(login)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.loginUser(sendEncrptRequest).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                setError(call, "Internet Connectivity issue")
            }

            override fun onResponse(
                call: Call<SendEncryptionRequest>,
                response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {
                    try {
                        val sendEncrptRequest: SendEncryptionRequest? = response.body()
                        val jsonString: String = sendEncrptRequest?.getText().toString()
                        val decrypted: String = Cryptography_Android.Decrypt(
                            jsonString,
                            Constaint.mKey
                        )
                        val gson = Gson()
                        val token: TypeToken<CustomerX> =
                            object : TypeToken<CustomerX>() {}
                        val customerXResponse: CustomerX = gson.fromJson(decrypted, token.type)

                        if (customerXResponse!!.isOTPSent) {
                            setSuccess(customerXResponse)
                        } else {
                            if (customerXResponse!!.carDetails.isNullOrEmpty()) {
                                setSuccess(customerXResponse)
                                return
                            }
                            prefsHelper.updateAuth(customerXResponse!!.token!!)
                            prefsHelper.updateCustomerId(customerXResponse!!.iD!!)
                            val pos = findDefaultPosition(customerXResponse!!.carDetails!!)
                            if (pos != -1) {
                                prefsHelper.updateScopeName(customerXResponse!!.carDetails!![pos].registrationNo!!)
                                prefsHelper.updateScopePass(login.password!!)
                            }
                            val sDate1 = customerXResponse.carDetails!![prefsHelper.getDefaultCarPos()].renewalDate

                            val simpleDateFormat =
                                SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())
                            val date = simpleDateFormat.parse(sDate1)
                            simpleDateFormat.applyPattern("dd-MM-yyyy")
                            customerXResponse.carDetails!![prefsHelper.getDefaultCarPos()].renewalDate = simpleDateFormat.format(date)

                            storeCustomerLocally(customerXResponse!!)
                        }

                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response)
                }
            }

        })
    }

    public fun setPass(pass: String) {
        password = pass
    }


    private fun storeCustomerLocally(customer: CustomerX) {
        launch {

            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            val insertResult = customerDao.insertAll(customer)
            setSuccess(customer)

        }
//        prefsHelper.updateTime(System.nanoTime())
    }



}