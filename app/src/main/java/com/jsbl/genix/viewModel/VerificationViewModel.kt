package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.OTPVerificationModel
import com.jsbl.genix.model.registration.OtpX
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.services.findDefaultPosition
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException


class VerificationViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var otpX: OtpX
    private lateinit var oTPVerificationModel: OTPVerificationModel
    var policyPassword: String? = null
    var edittext: String = ""
    var mobileNumber: String? = ""
    var userName: String? = ""
    var email: String? = ""
    var pin = ""
    var stringIMEI: String? = ""
    lateinit var otpType: String

    var isResetPass = false
    var isOTP = false
    var isEmailVerification = false
    var isPhoneVerification = false


    fun registerEntry(customer: CustomerX) {
        sendRequest()

//        val userRedeemsGetRequest = UserRedeemsGetRequest(iD)
        //encryption start
        val json = Gson().toJson(customer)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        //encryption end
        /*val registration= CustomerX()
        registration.email = customer.email
        registration.cNIC = customer.cNIC
        registration.maritalStatus = customer.maritalStatus
        registration.profileImagePath = customer.profileImagePath*/

        customer.password = null
        localService.updateUser(sendEncrptRequest).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                setError(call, "Internet Connectivity issue")

//                logD(APP_TAG, "method error")
            }

            override fun onResponse(
                call: Call<SendEncryptionRequest>,
                response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {
                    val sendEncrptRequest: SendEncryptionRequest? = response.body()
                    val jsonString: String = sendEncrptRequest?.getText().toString()
                    val decrypted: String = Cryptography_Android.Decrypt(
                        jsonString,
                        Constaint.mKey
                    )
                    val gson = Gson()
                    val token: TypeToken<CustomerX> =
                        object : TypeToken<CustomerX>() {}
                    val customerX: CustomerX = gson.fromJson(decrypted, token.type)
                    setSuccess(customerX)
                    customerX.scopeToken = prefsHelper.getScopeToken()
                    storeCustomerLocally(customerX!!)
                } else {
                    setError(response,null)
                }

            }

        })
    }

    fun storeCustomerLocally(customer: CustomerX) {
        launch {
            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            val insertResult = customerDao.insertAll(customer)
        }
    }

    fun verifyOtp(otp: String, mobile: String, type: String, imei: String, Username:String, Password:String) {

        sendRequest()
        logD("***verifyEntry", "Verify OTP")
        oTPVerificationModel = OTPVerificationModel(mobile, type, "", ""+otp, imei, ""+Username,""+Password)
        val json = Gson().toJson(oTPVerificationModel)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.getVerifyOtpLogin(sendEncrptRequest).enqueue(object : Callback<SendEncryptionRequest?> {
            override fun onFailure(call: Call<SendEncryptionRequest?>, t: Throwable) {
                setError(call, "Internet Connectivity issue")
            }

            override fun onResponse(
                call: Call<SendEncryptionRequest?>,
                response: Response<SendEncryptionRequest?>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        try {
//                            val rr = stringObj(
//                                response.body().toString(),
//                                CustomerX::class.java
//                            )
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



                            if (customerXResponse != null) {
                                val rc = customerXResponse
                                if (rc.carDetails.isNullOrEmpty()) {
                                    setSuccess(rc)
                                    return
                                }
                                prefsHelper.updateAuth(rc.token!!)
                                prefsHelper.updateCustomerId(rc!!.iD!!)
                                val pos = findDefaultPosition(rc!!.carDetails!!)
                                if (pos != -1) {
                                    prefsHelper.updateScopeName(rc!!!!.carDetails!![pos].registrationNo!!)
                                    prefsHelper.updateScopePass(policyPassword!!)
                                }
                                storeCustomerLocaly(rc!!)
                                setSuccess(rc)
                            } else {
                                setError(response, "Unable to Get Details")
                            }

                        } catch (e: Exception) {
                            setError(response, "Unable to Get Details")
                        }
                    } else {
                        setError(response, "Unable to Get Details")
                    }

                } else {
                    setError(response, null)

                }
            }

        })
    }


    fun verifyRegOtp(otp: String, mobile: String, type: String, imei: String) {
        sendRequest()
        otpX = OtpX(mobile, type, "", otp, imei)
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


    fun verifyEmailOtp(otp: String, mobile: String, type: String, imei: String, email: String) {
        sendRequest()
        otpX = OtpX(mobile, type, "" + email, otp, imei)
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

    private fun storeCustomerLocaly(customer: CustomerX) {
        launch {
            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            try {

                val insertResult = customerDao.insertAll(customer)
                setSuccess(customer)
//                setCustomerDetails(customer)
            } catch (e: Exception) {
                setSuccess(customer)
            }
        }
//        prefsHelper.updateTime(System.nanoTime())
    }

    /* private fun storeCustomerLocaly(customer: CustomerX) {
         launch {
             try {

                 val insertResult = customerDao.insertAll(customer)
 //                setSuccess(customer)
             } catch (e: Exception) {

             }
         }
 //        prefsHelper.updateTime(System.nanoTime())
     }*/


    fun askOtp(number: String, type: String, imei: String) {
        logD(APP_TAG, "method Called")
        sendRequest()
        otpX = OtpX(number, type, "", "0", imei)
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
                    setError(response, "Something went wrong, please try again later")
                }
            }

        })
    }


    fun askOtpEmail(number: String, type: String, imei: String, email:String) {
        logD(APP_TAG, "method Called")
        sendRequest()
        otpX = OtpX(""+number, ""+type, ""+email, "0", imei)
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
                        setError(response)
                    }
                }
                else {
                    setError(response)
                }
            }

        })
    }


}