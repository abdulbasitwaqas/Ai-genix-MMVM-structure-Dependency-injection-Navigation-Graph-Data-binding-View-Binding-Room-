package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.model.NetworkModel.CheckOutRedeemRequest
import com.jsbl.genix.model.NetworkModel.RedeemRewardRequest
import com.jsbl.genix.model.NetworkModel.UpdateRedeemCartRequest
import com.jsbl.genix.model.NetworkModel.UserRedeemsGetRequest
import com.jsbl.genix.model.redeem.AvailableRedeemsModelItem
import com.jsbl.genix.model.redeem.MyRedeemResponse
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.redeem.RedeemRewardModel
import com.jsbl.genix.utils.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import retrofit2.adapter.rxjava2.Result.response
import java.net.SocketTimeoutException


/**
 * Created by Muhammad Ali on 04-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class RedeemViewModel(application: Application) : BaseViewModel(application) {

    var isRedeemCartCount = false
    var isAvailableRedeem = false
    var isMyRedeem = false
    var isAddCartRedeem = false
    var isCheckoutRedeem = false

    fun getCartRedeem(iD: String) {
        logD(APP_TAG, "method Called")
        val userRedeemsGetRequest = UserRedeemsGetRequest(iD)
        //encryption start
        val json = Gson().toJson(userRedeemsGetRequest)
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
        localService.getRedeemCart(sendEncrptRequest)
            .enqueue(object : Callback<SendEncryptionRequest> {
                override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                   setError(call, "Internet Connectivity issue")
                }

                override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
                ) {
                    if (response.code() == 200) {
                        isRedeemCartCount =true
                        try {
                            //decryption start
                            val sendEncrptRequest: SendEncryptionRequest? = response.body()
                            val jsonString: String = sendEncrptRequest?.getText().toString()
                            val decrypted: String = Cryptography_Android.Decrypt(
                                jsonString,
                                Constaint.mKey
                            )
                            val gson = Gson()
                            val token: TypeToken<ArrayList<RedeemCartListItem?>?> =
                                object : TypeToken<ArrayList<RedeemCartListItem?>?>() {}
                            val redeemsList: List<RedeemCartListItem> = gson.fromJson(decrypted, token.type)
                            // decryption end
                            setSuccess(redeemsList)
                        }catch (e: Exception){
                            setError(response,null)
                        }
                    } else {
                        setError(response,null)

                    }

                }

            })
    }
    fun checkoutRedeem(iD: String,redeems: String) {
        logD(APP_TAG, "method Called")
        val checkOutRedeemRequest = CheckOutRedeemRequest(iD,redeems)
        val json = Gson().toJson(checkOutRedeemRequest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)
        localService.checkoutRedeem(sendEncrptRequest)
            .enqueue(object : Callback<SendEncryptionRequest> {
                override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                   setError(call, "Internet Connectivity issue")
                }

                override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
                ) {
                    if (response.code() == 200) {
                        isCheckoutRedeem =true
                        try {

                            setSuccess(response.body())
                        }catch (e: Exception){
                            setError(response,null)
                        }
                    } else {
                        setError(response,null)

                    }

                }

            })
    }

    fun getAvailableRedeems() {
        logD(APP_TAG, "method Called")

        localService.getAvailableRedeem()
            .enqueue(object : Callback<SendEncryptionRequest> {
                override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                   setError(call, "Internet Connectivity issue")
                }

                override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
                ) {
                    if (response.code() == 200) {
                        isAvailableRedeem = true
                        try {
                            val sendEncrptRequest: SendEncryptionRequest? = response.body()
                            val jsonString: String = sendEncrptRequest?.getText().toString()
                            val decrypted: String = Cryptography_Android.Decrypt(
                                jsonString,
                                Constaint.mKey
                            )
                            val gson = Gson()
                            val token: TypeToken<ArrayList<AvailableRedeemsModelItem?>?> =
                                object : TypeToken<ArrayList<AvailableRedeemsModelItem?>?>() {}
                            val availableRedeemsModelItem: List<AvailableRedeemsModelItem> = gson.fromJson(decrypted, token.type)

                            setSuccess(availableRedeemsModelItem)
                        }catch (e: Exception){
                            setError(response,null)
                        }
                    } else {
                        setError(response,null)

                    }

                }

            })
    }

    fun updateRedeemCart(userId: String, redeemID: String, flag: String,count:String) {
        logD(APP_TAG, "method Called")

        val updateRedeemCartRequest = UpdateRedeemCartRequest(userId,redeemID,flag,count)
        val json = Gson().toJson(updateRedeemCartRequest)
        var Request: String? = null
        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.updateRedeemCart(sendEncrptRequest)
            .enqueue(object : Callback<SendEncryptionRequest> {
                override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                   setError(call, "Internet Connectivity issue")
                }

                override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
                ) {
                    if (response.code() == 200) {
                        isAddCartRedeem = true
                        try {
                            val sendEncrptRequest: SendEncryptionRequest? = response.body()
                            val jsonString: String = sendEncrptRequest?.getText().toString()
                            val decrypted: String = Cryptography_Android.Decrypt(
                                jsonString,
                                Constaint.mKey
                            )
                            val gson = Gson()
                            val token: TypeToken<ArrayList<RedeemCartListItem?>?> =
                                object : TypeToken<ArrayList<RedeemCartListItem?>?>() {}
                            val redeemCartListItem: List<RedeemCartListItem> = gson.fromJson(decrypted, token.type)

                            setSuccess(redeemCartListItem)
                        }catch (e: Exception){
                            setError(response,null)
                        }
                    } else {
                        setError(response,null)

                    }

                }

            })
    }

    fun getRedeemReward(userId: String, redeemID: String) {
        logD(APP_TAG, "method Called")

        sendRequest()
        val updateRedeemCartRequest = RedeemRewardRequest(userId,redeemID)
        val json = Gson().toJson(updateRedeemCartRequest)
        var Request: String? = null
        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.getRedeemReward(sendEncrptRequest)
            .enqueue(object : Callback<SendEncryptionRequest> {
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
                            val token: TypeToken<RedeemRewardModel> =
                                object : TypeToken<RedeemRewardModel>() {}
                            val redeemRewardModel: RedeemRewardModel = gson.fromJson(decrypted, token.type)

                            setSuccess(redeemRewardModel)
                        }catch (e: Exception){
                            setError(response,null)
                        }
                    } else {
                        setError(response,null)

                    }

                }

            })
    }

    fun getMyRedeems(iD: String) {
        logD(APP_TAG, "method Called")
        val userRedeemsGetRequest = UserRedeemsGetRequest(iD)
        val json = Gson().toJson(userRedeemsGetRequest)
        var Request: String? = null
        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.getMyRedeems(sendEncrptRequest)
            .enqueue(object : Callback<SendEncryptionRequest> {
                override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                   setError(call, "Internet Connectivity issue")
                }

                override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
                ) {
                    if (response.code() == 200) {
                        isMyRedeem = true
                        try {
                            val sendEncrptRequest: SendEncryptionRequest? = response.body()
                            val jsonString: String = sendEncrptRequest?.getText().toString()
                            val decrypted: String = Cryptography_Android.Decrypt(
                                jsonString,
                                Constaint.mKey
                            )
                            val gson = Gson()
                            val token: TypeToken<MyRedeemResponse> =
                                object : TypeToken<MyRedeemResponse>() {}
                            val myRedeemResponse: MyRedeemResponse = gson.fromJson(decrypted, token.type)

                            setSuccess(myRedeemResponse)
                        }catch (e: Exception){
                            setError(response,null)
                        }
                    } else {
                        setError(response,null)

                    }

                }

            })
    }


    /*fun getDropDown() {
        logD(APP_TAG, "method Called")
        localService.getDropDown().enqueue(object : Callback<ResponseFillDropDown> {
            override fun onFailure(call: Call<ResponseFillDropDown>, t: Throwable) {
                setError(call, null)
            }

            override fun onResponse(
                call: Call<ResponseFillDropDown>,
                response: Response<ResponseFillDropDown>
            ) {
                if (response.code() == 200) {
                    try {
                        if (response.body() != null)
                            storeDropDownLocally(response.body()!!)
                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null)
                }
            }

        })
    }*/



}