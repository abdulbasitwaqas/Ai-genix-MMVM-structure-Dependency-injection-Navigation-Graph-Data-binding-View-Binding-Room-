package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.OtpX
import com.jsbl.genix.model.registration.RequestResetPassword
import com.jsbl.genix.utils.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

/**
 * Created by Muhammad Ali on 04-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class ResetViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var mobileNumber: String
    private lateinit var otpX: OtpX


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

    fun resetPassword(requestResetPassword: RequestResetPassword) {
        logD(APP_TAG, "method Called")

        val json = Gson().toJson(requestResetPassword)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.resetPassword(sendEncrptRequest).enqueue(object : Callback<Any?> {
            override fun onFailure(call: Call<Any?>, t: Throwable) {
                setError(call, "Internet Connectivity issue")
            }

            override fun onResponse(
                call: Call<Any?>,
                response: Response<Any?>
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


    private fun storeCustomerLocally(customer: CustomerX) {
        launch {

            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            val insertResult = customerDao.insertAll(customer)
            setSuccess(customer)

        }
//        prefsHelper.updateTime(System.nanoTime())
    }


}