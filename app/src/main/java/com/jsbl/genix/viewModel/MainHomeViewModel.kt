package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.StatsModel
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.Gamification.UserGamesRequest
import com.jsbl.genix.model.GetAllGamesEmptyRequest
import com.jsbl.genix.model.GetAllGamesResponseItem
import com.jsbl.genix.model.GetStatsFeedBackModel
import com.jsbl.genix.model.ShareWithFriendsModel
import com.jsbl.genix.model.profileManagement.PostCarDetail
import com.jsbl.genix.model.profileManagement.ResponseFillDropDown
import com.jsbl.genix.model.questions.QuestionResponse
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.trips.GetAllTripsRequest
import com.jsbl.genix.model.trips.StatsRequest
import com.jsbl.genix.trips.*
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.logD
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException


/**
 * Created by Muhammad Ali on 05-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class MainHomeViewModel(application: Application) : BaseViewModel(application) {
    var fromSplash = false
    var showReferanceDialog = false
    var selectedPosition = -1
    var filter = 0
    var getTrips = true
    lateinit var scopeName: String
    lateinit var scopeToken: String
    var isVerifyCode = false
    var tripsList = arrayListOf<TripItemResponse>()
//    var tripsList: ArrayList<TripsDetail>? = arrayListOf<TripsDetail>()


    fun getDropDown() {
        logD(APP_TAG, "method Called")
        localService.getDropDown().enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    setError(call, "Internet Connectivity issue")
                } else {
                    setError(call, t.message)
                }
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
                        val dropDownResponse: ResponseFillDropDown =
                                gson.fromJson(decrypted, token.type)

                        setSuccess(dropDownResponse)
                        storeDropDownLocally(dropDownResponse)
                    } catch (e: Exception) {
                        setError(response)
                    }
                } else {
                    setError(response)
                }
            }

        })
    }


    /*   fun getScopeTrips(token:String, policyNo: String, userName:String) {
           localService.getScopeTrips(
               customerX.scopeToken!!,
               customerX.carDetails!![selectedPosition].policyNumber!!
           ).enqueue(object : Callback<AllTripsDetail> {
               override fun onFailure(call: Call<ArrayList<AllTripsDetail>>, t: Throwable) {
                   setError(call, null)
               }

               override fun onResponse(
                   call: Call<ArrayList<AllTripsDetail>>,
                   response: Response<ArrayList<AllTripsDetail>>
               ) {
                   if (response.code() == 200) {
                       try {
                           setSuccess(response.body())
                       } catch (e: Exception) {
                           setError(response, "")
                       }
                   } else {
                       setError(response, "")
                   }
               }

           })
       }

   */

    fun getStats(scopeToken: String, regNo: String, string: String, id: String) {
        sendRequest()
        logD("**req", "scope token:  $scopeToken  reg no: $regNo  ")
        val statsRequest = StatsRequest("" + scopeToken, "" + regNo, "" + string, "" + id)
        val json = Gson().toJson(statsRequest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)

        localService.getStats(sendEncrptRequest)
                .enqueue(object : Callback<SendEncryptionRequest> {
                    override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
//                    setError(call, "")
                        if (t is SocketTimeoutException) {
                            setError(call, "Internet Connectivity issue")
                        } else {
                            setError(call, t.message)
                        }
                    }

                    override fun onResponse(
                            call: Call<SendEncryptionRequest>,
                            response: Response<SendEncryptionRequest>
                    ) {
                        if (response.code() == 200) {
                            //decryption start
                            val sendEncrptRequest: SendEncryptionRequest? = response.body()
                            val jsonString: String = sendEncrptRequest?.getText().toString()
                            val decrypted: String =
                                    Cryptography_Android.Decrypt(jsonString, Constaint.mKey)
                            val gson = Gson()
                            val token: TypeToken<StatsModel> =
                                    object : TypeToken<StatsModel>() {}
                            val statsModelResponse: StatsModel = gson.fromJson(decrypted, token.type)
                            // decryption end

                            setSuccess(statsModelResponse)


                            setSuccess(response.body()!!)
                        } else {
                            setError(response)
                        }
                    }
                })
    }


    fun getStatsFeedBack(scopeToken: String, regNo: String, string: String, id: String) {
        sendRequest()
        logD("***feedBack", "FeedBackAPICalling")

        val statsRequest = StatsRequest("" + scopeToken, "" + regNo, "" + string, "" + id)
        val json = Gson().toJson(statsRequest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)


        localService.getStatsFeedBack(sendEncrptRequest)
                .enqueue(object : Callback<SendEncryptionRequest> {
                    override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                        if (t is SocketTimeoutException) {
                            setError(call, "Internet Connectivity issue")
                        } else {
                            setError(call, t.message)
                        }

                    }

                    override fun onResponse(
                            call: Call<SendEncryptionRequest>,
                            response: Response<SendEncryptionRequest>
                    ) {
                        if (response.code() == 200) {

                            //decryption start
                            val sendEncrptRequest: SendEncryptionRequest? = response.body()
                            val jsonString: String = sendEncrptRequest?.getText().toString()
                            val decrypted: String = Cryptography_Android.Decrypt(
                                    jsonString,
                                    Constaint.mKey
                            )
                            val gson = Gson()
                            val token: TypeToken<GetStatsFeedBackModel> =
                                    object : TypeToken<GetStatsFeedBackModel>() {}
                            val statsModelResponse: GetStatsFeedBackModel =
                                    gson.fromJson(decrypted, token.type)
                            // decryption end

                            setSuccess(statsModelResponse)

//                            setSuccess(response.body()!!)
                            logD("***feedBack", "feedback called")
                        } else {
                            setError(response)
                        }
                    }
                })
    }


    fun getUserGames(userID: Long) {
        sendRequest()
        logD("***feedBack", "FeedBackAPICalling")

        val statsRequest = UserGamesRequest(userID)
        val json = Gson().toJson(statsRequest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)


        localService.getUserGames(sendEncrptRequest)
                .enqueue(object : Callback<Unit> {
                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        if (t is SocketTimeoutException) {
                            setError(call, "Internet Connectivity issue")
                        } else {
                            setError(call, t.message)
                        }

                    }

                    override fun onResponse(
                            call: Call<Unit>,
                            response: Response<Unit>
                    ) {
                        if (response.code() == 200) {

//                            setSuccess(response.body())
                        } else {
                            setError(response)
                        }
                    }
                })
    }


    fun getAllTrips(
            token: String,
            number: String,
            id: String,
            pageIndex: Int
    ) {
        sendRequest()
        logD("*tripsRequest", "Get All Trips calling")
        logD("*tripsRequest", "token: " + token)
        logD("*tripsRequest", "number: " + number)
        logD("*tripsRequest", "id: " + id)

        logD(APP_TAG, "method Called")

        val getAllTripsRequest = GetAllTripsRequest(token, number, id,pageIndex)
        val json = Gson().toJson(getAllTripsRequest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)


        localService.getALLTrips(
                sendEncrptRequest
        ).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                logD("*tripsRequest", " 3333    Get All Trips calling ${t.localizedMessage}")
//                    setError(call, "")
                if (t is SocketTimeoutException) {
                    setError(call, "Internet Connectivity issue")
                } else {
                    setError(call, t.message)
                }

            }

            override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {
                    logD("*tripsRequest", " 44444    Get All Trips calling")
                    //decryption start
                    val sendEncrptRequest: SendEncryptionRequest? = response.body()
                    val jsonString: String = sendEncrptRequest?.getText().toString()
                    val decrypted: String = Cryptography_Android.Decrypt(
                            jsonString,
                            Constaint.mKey
                    )
                    val gson = Gson()
                    val token: TypeToken<TripsResponse> =
                            object : TypeToken<TripsResponse>() {}
                    val tripsResponse: TripsResponse = gson.fromJson(decrypted, token.type)
                    // decryption end


                    setSuccess(tripsResponse)
//
                } else {
                    setError(response)
                }
            }
        })
    }


    fun getAllTrip(
            token: String,
            number: String,
            id: String,
            pageIndex: Int
    ) {
        sendRequest()
        logD("*tripsRequest", "Get All Trips calling")
        logD("*tripsRequest", "token: " + token)
        logD("*tripsRequest", "number: " + number)
        logD("*tripsRequest", "id: " + id)

        logD(APP_TAG, "method Called")

        val getAllTripsRequest = GetAllTripsRequest(token, number, id,pageIndex)
        val json = Gson().toJson(getAllTripsRequest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)


        localService.getALLTrip(
                sendEncrptRequest
        ).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                logD("*tripsRequest", " 3333    Get All Trips calling ${t.localizedMessage}")
//                    setError(call, "")
                if (t is SocketTimeoutException) {
                    setError(call, "Internet Connectivity issue")
                } else {
                    setError(call, t.message)
                }

            }

            override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {
                    logD("*tripsRequest", " 44444    Get All Trips calling")
                    //decryption start
                    val sendEncrptRequest: SendEncryptionRequest? = response.body()
                    val jsonString: String = sendEncrptRequest?.getText().toString()
                    val decrypted: String = Cryptography_Android.Decrypt(
                            jsonString,
                            Constaint.mKey
                    )
                    val gson = Gson()
                    val token: TypeToken<ArrayList<TripsResponse>> =
                            object : TypeToken<ArrayList<TripsResponse>>() {}
                    val tripsResponse: ArrayList<TripsResponse> = gson.fromJson(decrypted, token.type)
                    // decryption end


                    setSuccess(tripsResponse)
//
                } else {
                    setError(response)
                }
            }
        })
    }

    fun getFeedbackSpecificTrip(
            token: String,
            number: String,
            id: String,
            startDate: String,
            endDate: String
    ) {
        sendRequest()
        logD("*tripsRequest", "Get All Trips calling")
        logD("*tripsRequest", "token: " + token)
        logD("*tripsRequest", "number: " + number)
        logD("*tripsRequest", "id: " + id)
        logD("*tripsRequest", "startDate:  $startDate ")
        logD("*tripsRequest", "endDate:  $endDate ")

        logD(APP_TAG, "method Called")

        val getTripFeedBack = GetTripFeedBackRequest("" + token, "" + number, "" + id, "" + startDate, "" + endDate)
        val json = Gson().toJson(getTripFeedBack)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)


        localService.getTripFeedBack(
                sendEncrptRequest
        ).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    setError(call, "Internet Connectivity issue")
                } else {
                    setError(call, t.message)
                }

            }

            override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {
                    //decryption start
                    val sendEncrptRequest: SendEncryptionRequest? = response.body()
                    val jsonString: String = sendEncrptRequest?.getText().toString()
                    val decrypted: String = Cryptography_Android.Decrypt(
                            jsonString,
                            Constaint.mKey
                    )
                    val gson = Gson()
                    val token: TypeToken<GetTripsFeedbackResponse> =
                            object : TypeToken<GetTripsFeedbackResponse>() {}
                    val statsModelResponse: GetTripsFeedbackResponse =
                            gson.fromJson(decrypted, token.type)
                    // decryption end

                    setSuccess(statsModelResponse)
//
                } else {
                    setError(response)
                }
            }
        })
    }

    fun getLatestFiveTrips(
            token: String,
            number: String,
            id: String
    ) {
        sendRequest()
        logD("*tripsRequest", "Get All Trips calling")
        logD("*tripsRequest", "token: " + token)
        logD("*tripsRequest", "number: " + number)
        logD("*tripsRequest", "id: " + id)

        logD(APP_TAG, "method Called")

        val getAllTripsRequest = GetAllTripsRequest(token, number, id)
        val json = Gson().toJson(getAllTripsRequest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)


        localService.getLatestFiveTrips(
                sendEncrptRequest
        ).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                logD("*tripsRequest", " 3333    Get All Trips calling ${t.localizedMessage}")
//                    setError(call, "")
                if (t is SocketTimeoutException) {
                    setError(call, "Internet Connectivity issue")
                } else {
                    setError(call, t.message)
                }

            }

            override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
            ) {
                if (response.code() == 200) {
                    logD("*tripsRequest", " 44444    Get All Trips calling")
                    //decryption start
                    val sendEncrptRequest: SendEncryptionRequest? = response.body()
                    val jsonString: String = sendEncrptRequest?.getText().toString()
                    val decrypted: String = Cryptography_Android.Decrypt(
                            jsonString,
                            Constaint.mKey
                    )
                    val gson = Gson()
                    val token: TypeToken<ArrayList<TripsDetailModel>> = object : TypeToken<ArrayList<TripsDetailModel>>() {}
                    val tripsResponse: ArrayList<TripsDetailModel> = gson.fromJson(decrypted, token.type)
                    // decryption end


                    setSuccess(tripsResponse)
//
                } else {
                    setError(response)
                }
            }
        })
    }

    private fun findDefaultPosition(carDetails: java.util.ArrayList<PostCarDetail>) {

        for (i in carDetails.indices) {
            if (carDetails[i].isDefaultCar) {
                selectedPosition = i
                break
            }
        }
    }

    fun storeCustomerLocally(customer: CustomerX) {
        launch {
            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            val insertResult = customerDao.insertAll(customer)
        }
//        prefsHelper.updateTime(System.nanoTime())
    }


    fun registerEntry(customer: CustomerX) {
        sendRequest()
        customer.password = ""
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

        localService.updateUser(sendEncrptRequest)
                .enqueue(object : Callback<SendEncryptionRequest> {
                    override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                        if (t is SocketTimeoutException) {
                            setError(call, "Internet Connectivity issue")
                        } else {
                            setError(call, t.message)
                        }

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
                            setError(response, null)
                        }

                    }

                })
    }

    fun shareWithFriend(number: String) {
        logD(APP_TAG, "method Called")
        sendRequest()
        val shareWithFriedsss = ShareWithFriendsModel(customerX.iD, number)

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
        localService.verifyShareWithFriends(sendEncrptRequest)
                .enqueue(object : Callback<SendEncryptionRequest> {
                    override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                        if (t is SocketTimeoutException) {
                            setError(call, "Internet Connectivity issue")
                        } else {
                            setError(call, t.message)
                        }
                    }

                    override fun onResponse(
                            call: Call<SendEncryptionRequest>,
                            response: Response<SendEncryptionRequest>
                    ) {
                        if (response.code() == 200) {
                            try {

                                val msg2 = response.body()

                                val successMsg: SendEncryptionRequest = msg2!!
                                val jsonString: String = successMsg?.getText().toString()
                                val decrypted: String = Cryptography_Android.Decrypt(
                                        jsonString,
                                        Constaint.mKey
                                )
                                val gson = Gson()
                                val token: TypeToken<String> =
                                        object : TypeToken<String>() {}
                                val successsMSgs: String = gson.fromJson(decrypted, token.type)

                                setSuccess(successsMSgs)

                            } catch (e: java.lang.Exception) {
                                setError(response, null)
                            }
                        } else {
                            setError(response, null)
                        }
                    }

                })
    }


}