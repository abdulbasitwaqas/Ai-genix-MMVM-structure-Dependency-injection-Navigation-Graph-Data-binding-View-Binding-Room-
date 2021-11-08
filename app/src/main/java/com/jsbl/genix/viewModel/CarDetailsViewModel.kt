package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.model.NetworkModel.UserRedeemsGetRequest
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.model.registration.LoginMdl
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.services.findDefaultPosition
import com.jsbl.genix.views.dialogs.ProgressDialog
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*


class CarDetailsViewModel(application: Application) : BaseViewModel(application) {

    var fromReg = false
    var fromAdCAR = false
    var fromLogin = false
    var createNew = true
    var selectedPosition = -1
    var policyPassword: String? = null
    public lateinit var dialogP: ProgressDialog
    val REGEX_CAR: String = "\\\\b([a-zA-Z0-9])\\\\1\\\\1+\\\\b"
    val Regex1 = "^[A-Za-z]{2}[0-9]{3}$"
    val Regex2 = "^[A-Za-z]{2}[0-9]{4}$"
    val Regex3 = "^[A-Za-z]{3}[0-9]{3}$"
    val Regex4 = "^[A-Za-z]{2}[0-9]{2}$"
    val Regex5 = "^[A-Za-z]{3}[0-9]{4}$"


    private fun storeCustomerLocally(customer: CustomerX) {
        launch {
            val customerDao: CustomerDao = AppDatabase(getApplication()).customerDao()
            val insertResult = customerDao.insertAll(customer)
            setSuccess(customer)
        }
//        prefsHelper.updateTime(System.nanoTime())
    }

    fun getCustomer(): CustomerX {
        return this.customerX
    }

    fun setTempAuth() {
        prefsHelper.updateAuth(customerX.token!!)
    }

    fun setTempAuthNull() {
        prefsHelper.updateAuth("")
    }


    fun addCarDetails(carDetail: PostCarDetail, isInsurance: Boolean) {
        if (createNew && fromReg)
            setTempAuth()
        logD(APP_TAG, "method Called")
        carDetail.customerID = customerX.iD

        //encryption start
        val json = Gson().toJson(carDetail)
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




        localService.setCarDetails(sendEncrptRequest).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                if (createNew && fromReg)
                    setTempAuthNull()
                setError(call, "Internet Connectivity issue")

            }

            override fun onResponse(
                call: Call<SendEncryptionRequest>,
                response: Response<SendEncryptionRequest>
            ) {
//                showPDialog()

                if (createNew && fromReg)
                    setTempAuthNull()
                /* if (response.code() == 200) {
                     try {
                         setSuccess(response.body())
                         storeCustomerLocally(customerX)
                     } catch (e: Exception) {
                         setError(response, null)
                     }
                 } else {
                     setError(response, null)

                 }*/
                if (response.code() == 200) {
                    try {
                        //decryption start
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
                        // decryption end





                        if (isInsurance) {
                            findCar(carDetail)
                            setDetails(response)
                        } else {
                            if (createNew) {
                                if (carDetail.isDefaultCar) {
                                    unCheckAll()
                                    carDetail.isDefaultCar = true
                                }
                                if (customerXResponse is CustomerX) {
                                    val body = customerXResponse
                                    carDetail.iD = body.iD
                                    if (customerX.carDetails == null) {
                                        customerX.carDetails = ArrayList<PostCarDetail>()
                                    }
                                    customerX.carDetails!!.add(carDetail)
                                } else {
                                    if (customerX.carDetails == null) {
                                        customerX.carDetails = ArrayList<PostCarDetail>()
                                    }
                                    customerX.carDetails!!.add(carDetail)
                                }
                            } else {
                                if (carDetail.isDefaultCar) {
                                    unCheckAll()
                                    carDetail.isDefaultCar = true
                                }
                                findCar(carDetail)
                            }


                            setDetails(response)
                        }
                    }catch (e : Exception){
                        setError(response,null)
                    }

                    fromAdCAR = true
                } else {
                    dismissDialog()
                    setError(response, null)

                }
            }

        })
    }

    fun findCar(carDetail: PostCarDetail) {
        if (carDetail.iD == null) {
            return
        }
        for (obj in customerX.carDetails!!.indices) {
            if (customerX.carDetails!![obj].iD == null) {
                break
            }
            if (customerX.carDetails!![obj].iD == carDetail.iD) {
                customerX.carDetails!!.add(obj, carDetail)
                customerX.carDetails!!.removeAt(obj + 1)
                return
            }
        }
        customerX.carDetails!!.add(carDetail)
    }

    fun unCheckAll() {
        if (!customerX.carDetails.isNullOrEmpty()) {
            for (cd in customerX.carDetails!!) {
                cd.isDefaultCar = false
            }
        }
    }

    fun setDetails(any: Any) {
        customerX.percentage = getProfilePercent(customerX)
        try {
//                            setSuccess(response.body())
            if (fromReg) {
                prefsHelper.updateAuth(customerX.token!!)
                prefsHelper.updateCustomerId(customerX.iD!!)
                val pos = findDefaultPosition(customerX.carDetails!!)
                if (pos != -1) {
                    prefsHelper.updateScopeName(customerX!!.carDetails!![pos].registrationNo!!)
                    prefsHelper.updateScopePass(policyPassword!!)
                }
            }
//                                    storeCustomerLocaly(response.body()!!)
            storeCustomerLocally(customerX)
        } catch (e: Exception) {
            setError(any, null)
        }
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

    fun showPDialog() {
        dialogP = ProgressDialog.newInstance()
        dialogP.isCancelable = false
    }

    fun dismissDialog() {
        if (this::dialogP.isInitialized)
            if (dialogP.isAdded)
                dialogP.dismiss()
    }


}