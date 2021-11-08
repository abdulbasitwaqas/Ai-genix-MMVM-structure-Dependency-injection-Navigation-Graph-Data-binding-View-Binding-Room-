package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.FeedBackQuestionsModel
import com.jsbl.genix.model.NetworkModel.CheckOutRedeemRequest
import com.jsbl.genix.model.NetworkModel.UserRedeemsGetRequest
import com.jsbl.genix.model.profileManagement.GetCustomerFeedbackModel
import com.jsbl.genix.model.profileManagement.PostFeedBack
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

/**
 * Created by Muhammad Ali on 04-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class FeedbackViewModel(application: Application) : BaseViewModel(application) {
    val feedQuestionsss = false

    private fun storeCustomerLocally(customer: CustomerX) {
        launch {
            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            val insertResult = customerDao.insertAll(customer)
            setSuccess(customer)

        }
//        prefsHelper.updateTime(System.nanoTime())
    }


    fun getCustomer(): CustomerX {
        return customerX
    }

//post api for getfeedackQuestions
 /*   fun getFeedBackQues(getCustomerFeedbackModel: GetCustomerFeedbackModel) {
        sendRequest()

        localService.addFeedbackQuestions(getCustomerFeedbackModel)
            .enqueue(object : Callback<List<FeedBackQuestionsModel>> {
                override fun onFailure(call: Call<List<FeedBackQuestionsModel>>, t: Throwable) {
                    setError(call,t.message)

                }
                override fun onResponse(
                    call: Call<List<FeedBackQuestionsModel>>,
                    response: Response<List<FeedBackQuestionsModel>>
                ) {
                    if (response.code() == 200) {
                        setSuccess(response.body())
                    }
                    else if (response.code() == 400) {
                    }
                    else {
                        setError(response, "")
                    }
                }
            })
    }*/

    fun getFeedBackQues(userId:String) {
        sendRequest()
        val userRedeemsGetRequest = UserRedeemsGetRequest(userId)
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
        localService.addFeedbackQuestions(sendEncrptRequest)
            .enqueue(object : Callback<SendEncryptionRequest> {
                override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                   setError(call, "Internet Connectivity issue")

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
                        val token: TypeToken<ArrayList<FeedBackQuestionsModel?>?> =
                            object : TypeToken<ArrayList<FeedBackQuestionsModel?>?>() {}
                        val feedbackQuestionModelList: List<FeedBackQuestionsModel> = gson.fromJson(decrypted, token.type)

                        setSuccess(feedbackQuestionModelList)
                    }
                    else if (response.code() == 400) {
                    }
                    else {
                        setError(response, "")
                    }
                }
            })
    }




    fun addFeedback(feedBack: PostFeedBack) {
        logD(APP_TAG, "method Called")
        feedBack.UserID = prefsHelper.getCustomerId()
        customerX.feedBacks = feedBack
        val json = Gson().toJson(feedBack)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(json, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val Text = Request
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)
        localService.addFeedback(sendEncrptRequest).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                setError(call, "Internet Connectivity issue")

            }
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                 if (response.code() == 200) {
                     try {
                         setSuccess(response.body())
                     } catch (e: Exception) {
                         setError(response,"")
                     }
                 } else {
                     setError(response,"")
                 }
          /*      if (response.code() == 200) {
                    if (response.body() != null) {
                        try {
                            val rr = stringObj(
                                response.body().toString(),
                                DetailsResponse::class.java
                            )
                            if (rr != null) {
                                val rc = rr as DetailsResponse
                                if(rc.percentage!=null && rc.percentage!=0){
                                    customerX.percentage = rc.percentage
                                }else{
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

                        } catch (e: Exception) {
                            customerX.percentage = getProfilePercent(customerX)
                            try {
//                            setSuccess(response.body())
                                storeCustomerLocally(customerX)
                            } catch (e: Exception) {
                                setError(response, null)
                            }
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
                else {
                    setError(response, null)

                }*/
            }

        })
    }

}