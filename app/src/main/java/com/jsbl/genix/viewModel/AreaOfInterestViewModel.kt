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
import com.jsbl.genix.model.NetworkModel.GetCustomerInterestByIDRequest
import com.jsbl.genix.model.ShareWithFriendsModel
import com.jsbl.genix.model.help.HelpResponseModelItem
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

/**
 * Created by Muhammad Ali on 04-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class AreaOfInterestViewModel(application: Application) : BaseViewModel(application) {


    var fromSubInterestAdapter = false
    var fromSubInterestIds = false
    private fun storeCustomerLocally(customer: CustomerX) {
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

    fun getCustomer(): CustomerX {
        return customerX
    }

/*
    fun addAreaOfInterest(areaOfInterest: List<Interest>) {
        logD(APP_TAG, "method Called")
        customerX.customerInterests = areaOfInterest

        localService.addAreaOfInterest(areaOfInterest)
            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    setError(call, t.message)
                }

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
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

                    if (response.code() == 200) {
                        if (response.body() != null) {
                            try {
                                val rr = stringObj(
                                    response.body().toString(),
                                    DetailsResponse::class.java
                                )
                                if (rr != null) {
                                    val rc = rr as DetailsResponse
                                    if (rc.percentage != null && rc.percentage != 0) {
                                        customerX.percentage = rc.percentage
                                    } else {
                                        customerX.percentage = getProfilePercent(customerX)
                                    }
//                            setSuccess(response.body())
                                    try {
//                            setSuccess(response.body())
                                        storeCustomerLocally(customerX)
                                    } catch (e: Exception) {
                                        setError(response, null)
                                    }
                                } else {
                                    customerX.percentage = getProfilePercent(customerX)
                                    try {
//                            setSuccess(response.body())
                                        storeCustomerLocally(customerX)
                                    } catch (e: Exception) {
                                        setError(response, null)
                                    }
                                }

                            }
                            catch (e: Exception) {
                                customerX.percentage = getProfilePercent(customerX)
                                try {
//                            setSuccess(response.body())
                                    storeCustomerLocally(customerX)
                                } catch (e: Exception) {
                                    setError(response, null)
                                }
                            }
                        }
                        else {
                            customerX.percentage = getProfilePercent(customerX)
                            try {
//                            setSuccess(response.body())
                                storeCustomerLocally(customerX)
                            } catch (e: Exception) {
                                setError(response, null)
                            }
                        }

                    } else {
                        setError(response, null)

                    }
                }

            })
    }*/


    fun getHelp() {
        logD(APP_TAG, "method Called")

        localService.getHelp()
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
                            val token: TypeToken<ArrayList<HelpResponseModelItem?>?> =
                                object : TypeToken<ArrayList<HelpResponseModelItem?>?>() {}
                            val helpResponseModelItemList: List<HelpResponseModelItem> = gson.fromJson(decrypted, token.type)

                            setSuccess(helpResponseModelItemList)
                        } catch (e: Exception) {
                            setError(response)
                        }
                    } else {
                        setError(response)

                    }

                }

            })
    }

    fun addInterests(areaOfInterest: Interest) {
        val arraylist = ArrayList<Interest>()
        arraylist.add(areaOfInterest)
        logD(APP_TAG, "method Called")
        customerX.customerInterests = arraylist

        val json = Gson().toJson(areaOfInterest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.addAreaOfInterest(sendEncrptRequest)

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
                            setSuccess(response.body())
                            if (response.body() != null) {

                                //decryption start
                                val sendEncrptRequest: SendEncryptionRequest = response.body() as SendEncryptionRequest
                                val jsonString: String = sendEncrptRequest.getText().toString()
                                val decrypted: String = Cryptography_Android.Decrypt(
                                    jsonString,
                                    Constaint.mKey
                                )
                                val gson = Gson()
                                val token: TypeToken<JSONObject> =
                                    object : TypeToken<JSONObject>() {}
                                val json: JSONObject = gson.fromJson(decrypted, token.type)
                                // decryption end

                                try {
                                    val rr = stringObj(
                                        json.toString(),
                                        DetailsResponse::class.java
                                    )
                                    if (rr != null) {
                                        val rc = rr as DetailsResponse
                                        if (rc.percentage != null && rc.percentage != 0) {
                                            customerX.percentage = rc.percentage
                                        } else {
                                            customerX.percentage = getProfilePercent(customerX)
                                        }
                                        try {
                                            storeCustomerLocally(customerX)
                                        } catch (e: Exception) {
                                            setError(response, null)
                                        }
                                    } else {
                                        customerX.percentage = getProfilePercent(customerX)
                                        try {
                                            storeCustomerLocally(customerX)
                                        } catch (e: Exception) {
                                            setError(response, null)
                                        }
                                    }

                                }
                                catch (e: Exception) {
                                    customerX.percentage = getProfilePercent(customerX)
                                    try {
//                            setSuccess(response.body())
                                        storeCustomerLocally(customerX)
                                    } catch (e: Exception) {
                                        setError(response, null)
                                    }
                                }
                            }
                            else {
                                customerX.percentage = getProfilePercent(customerX)
                                try {
//                            setSuccess(response.body())
                                    storeCustomerLocally(customerX)
                                } catch (e: Exception) {
                                    setError(response, null)
                                }
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

    fun getInterestsByID(areaOfInterest: GetCustomerInterestByIDRequest) {
//        val arraylist = ArrayList<Interest>()
//        arraylist.add(areaOfInterest)
        logD(APP_TAG, "method Called")
//        customerX.customerInterests = arraylist

        val json = Gson().toJson(areaOfInterest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.getCustomerInterestsById(sendEncrptRequest)
            .enqueue(object : Callback<SendEncryptionRequest> {
                override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                   setError(call, "Internet Connectivity issue")

                }

                override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
                ) {
                    fromSubInterestIds = true
                    if (response.code() == 200) {
                        try {

                            val sendEncrptRequest: SendEncryptionRequest? = response.body()
                            val jsonString: String = sendEncrptRequest?.getText().toString()
                            val decrypted: String = Cryptography_Android.Decrypt(
                                jsonString,
                                Constaint.mKey
                            )
                            val gson = Gson()
                            val token: TypeToken<ArrayList<GetCustomerInterestByIdItem>> =
                                object : TypeToken<ArrayList<GetCustomerInterestByIdItem>>() {}
                            val customerInterestIdsList: ArrayList<GetCustomerInterestByIdItem> = gson.fromJson(decrypted, token.type)
                            setSuccess(customerInterestIdsList)
                        } catch (e: Exception) {
                            setError(response, null)
                        }
                    } else {
                        setError(response, null)

                    }

                }

            })
    }

    fun getInterests(areaOfInterest: GetCustomerInterestModel) {
//        val arraylist = ArrayList<Interest>()
//        arraylist.add(areaOfInterest)
        logD(APP_TAG, "method Called")
//        customerX.customerInterests = arraylist

        val json = Gson().toJson(areaOfInterest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.getCustomerInterest(sendEncrptRequest)
            .enqueue(object : Callback<SendEncryptionRequest> {
                override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                   setError(call, "Internet Connectivity issue")

                }

                override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
                ) {
                    fromSubInterestIds = true
                    if (response.code() == 200) {
                        try {

                            val sendEncrptRequest: SendEncryptionRequest? = response.body()
                            val jsonString: String = sendEncrptRequest?.getText().toString()
                            val decrypted: String = Cryptography_Android.Decrypt(
                                jsonString,
                                Constaint.mKey
                            )
                            val gson = Gson()
                            val token: TypeToken<SubInterestIdsModel> =
                                object : TypeToken<SubInterestIdsModel>() {}
                            val subInterestIdsModel: SubInterestIdsModel = gson.fromJson(decrypted, token.type)

                            setSuccess(subInterestIdsModel)
                        } catch (e: Exception) {
                            setError(response, null)
                        }
                    } else {
                        setError(response, null)

                    }

                }

            })
    }

    fun getDropDown() {
        logD(APP_TAG, "method Called")
        localService.getDropDown().enqueue(object : Callback<SendEncryptionRequest> {
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
                        val token: TypeToken<ResponseFillDropDown> =
                            object : TypeToken<ResponseFillDropDown>() {}
                        val dropDownResponse: ResponseFillDropDown = gson.fromJson(decrypted, token.type)

                        setSuccess(dropDownResponse)
                        storeDropDownLocally(dropDownResponse)
                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null)
                }
            }

        })
    }


}