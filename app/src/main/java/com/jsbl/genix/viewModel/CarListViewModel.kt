package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.profileManagement.DeleteCarRequest
import com.jsbl.genix.model.profileManagement.PostCarDetail
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.logD
import com.jsbl.genix.views.dialogs.ProgressDialog
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class CarListViewModel(application: Application) : BaseViewModel(application) {
     lateinit var dialogP: ProgressDialog
    var fromDelCar: Boolean = false

    fun selectingDefault(id: Long) {
        logD(APP_TAG, "method Called")
        sendRequest()
        localService.setDefaultCar(id, customerX.iD!!)
            .enqueue(object : Callback<Void?> {
            override fun onFailure(call: Call<Void?>, t: Throwable) {
                setError(call,t.message)

            }

            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    try {
                        selectCar(id)
//                        setDetails(response)
//                        setSuccess(response.body())
                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    setError(response, null)

                }
            }

        })
    }





    fun deleteCar(postCarDetail: PostCarDetail, deleteCarRequest: DeleteCarRequest ) {
        logD(APP_TAG, "method Called")
//        logD("**delRegNo", "$regNo")
        sendRequest()

//        postCarDetail.registrationNo = regNo

        //encryption start
        val json = Gson().toJson(deleteCarRequest)
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


        localService.setDeleteCar(sendEncrptRequest).enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                setError(call,t.message)

            }

            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) {
                if (response.code() == 200) {
                    try {

                        //decryption start
//                        val sendEncrptRequest: SendEncryptionRequest? = response.body()
//                        val jsonString: String = sendEncrptRequest?.getText().toString()
//                        val decrypted: String = Cryptography_Android.Decrypt(
//                            jsonString,
//                            Constaint.mKey
//                        )
//                        val gson = Gson()
//                        val token: TypeToken<PostCarDetail> =
//                            object : TypeToken<PostCarDetail>() {}
//                        val postCarDetails: PostCarDetail = gson.fromJson(decrypted, token.type)
                        // decryption end



                        removeCarItem(postCarDetail)

                    } catch (e: Exception) {
                        setError(response, null)
                    }
                } else {
                    dismissDialog()
                    setError(response, null)

                }
            }

        })
    }

    fun selectCar(id: Long) {
        for (obj in customerX.carDetails!!.indices) {
            if (customerX.carDetails!![obj].iD == null) {
                break
            }
            customerX.carDetails!![obj].isDefaultCar = customerX.carDetails!![obj].iD == id
        }
    }

    fun setDetails(any: Any) {
        try {
            storeCustomerLocally(customerX)
        } catch (e: Exception) {
            setError(any, null)
        }
    }

    /*  fun setDeleteDetails(any: Any) {
          try {
              deleteCustomerLocally(customerX)
          } catch (e: Exception) {
              setError(any, null)
          }
      }*/


    private fun storeCustomerLocally(customer: CustomerX) {
        launch {
            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            val insertResult = customerDao.insertAll(customer)
            fetchFromDatabase()
        }
//        prefsHelper.updateTime(System.nanoTime())
    }
/*
    private fun deleteCar(postCarDetail: PostCarDetail) {
        launch {

            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            val insertResult = customerDao.deleteCarDetail(postCarDetail)
            setSuccess(postCarDetail)

        }
    }
    */
    private fun removeCarItem(postCarDetail: PostCarDetail){
        for (carItem in customerX.carDetails!!){
            if (carItem.iD == postCarDetail.iD){
                customerX.carDetails!!.remove(carItem)
                setSuccess(postCarDetail)
                break
            }
        }
        storeCustomerLocally(customerX)
    }

    private fun dismissDialog() {
        if (this::dialogP.isInitialized)
            if (dialogP.isAdded)
                dialogP.dismiss()
    }


}