package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.NetworkModel.UserRedeemsGetRequest
import com.jsbl.genix.model.ShareWithFriendsModel
import com.jsbl.genix.model.UpdateProfileResponseModel
import com.jsbl.genix.model.profileManagement.ResponseFillDropDown
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.OtpX
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.logD
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

/**
 * Created by Muhammad Ali on 05-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class PersonalDetailViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var otpX: OtpX
    var isUpdateProfile = false
    var isUpdatePhone = false
    var isUpdateName = false
    var changedEmail = ""
    var changedPhone = ""
    var shareWithFriendss = false




    fun registerEntry(customer: CustomerX) {
        sendRequest()
//        val userRedeemsGetRequest = UserRedeemsGetRequest(iD)
        //encryption start
        customer.password = ""
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

                    logD("**token","scope token from response:"+customerX.scopeToken)
                    storeCustomerLocally(customerX)
                } else {
                    setError(response)
                }

            }

        })
    }


    fun storeCustomerLocally(customer: CustomerX) {
        launch {
            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            val insertResult = customerDao.insertAll(customer)
        }
//        prefsHelper.updateTime(System.nanoTime())
    }

    fun askOtp(number: String, type: String, imei: String, email:String) {
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
                        val sendEncrptRequest: SendEncryptionRequest? = response.body()
                        val jsonString: String = sendEncrptRequest?.getText().toString()
                        val decrypted: String = Cryptography_Android.Decrypt(
                            jsonString,
                            Constaint.mKey
                        )
                        val gson = Gson()
                        val token: TypeToken<UpdateProfileResponseModel> =
                            object : TypeToken<UpdateProfileResponseModel>() {}
                        val updateProfileResponseModel: UpdateProfileResponseModel = gson.fromJson(decrypted, token.type)
                        setSuccess(updateProfileResponseModel)
                    } catch (e: java.lang.Exception) {
                        setError(response, null)
                    }
                }
                else {
                    setError(response, null)
                }
            }
        })
    }



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
            )
            {
                if (response.code() == 200) {
                    try {
                        val sendEncrptRequest: SendEncryptionRequest? = response.body()
                        val jsonString: String = sendEncrptRequest?.getText().toString()
                        val decrypted: String = Cryptography_Android.Decrypt(
                            jsonString,
                            Constaint.mKey
                        )
                        val gson = Gson()
                        val token: TypeToken<UpdateProfileResponseModel> =
                            object : TypeToken<UpdateProfileResponseModel>() {}
                        val updateProfileResponseModel: UpdateProfileResponseModel = gson.fromJson(decrypted, token.type)



                        setSuccess(updateProfileResponseModel)
                    } catch (e: java.lang.Exception) {
                        setError(response, null)
                    }
                }
                else {
                    setError(response, null)
                }
            }

        })
    }


    fun shareWithFriend(number: String) {
        logD(APP_TAG, "method Called")
        logD("**code", "random number: "+number)
        sendRequest()
        val shareWithFriedsss = ShareWithFriendsModel(customerX.iD,number)
        val json = Gson().toJson(shareWithFriedsss)
        var Request: String? = null
        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)
        localService.shareWithFriends(sendEncrptRequest).enqueue(object : Callback<Any?> {
            override fun onFailure(call: Call<Any?>, t: Throwable) {
                setError(call, "Internet Connectivity issue")
            }
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                if (response.code() == 200) {
                    try {

                        setSuccess(response.body())
                    } catch (e: java.lang.Exception) {
                        setError(response, null)
                    }
                }
                else if (response.code() == 201) {
                    try {
                        setError(response.message())
                    } catch (e: Exception) {
                        setError(response, null)
                    }
                }
                else {
                    setError(response, null)
                }
            }

        })
    }



}