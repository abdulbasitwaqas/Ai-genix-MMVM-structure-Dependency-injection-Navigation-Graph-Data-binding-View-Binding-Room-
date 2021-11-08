package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.utils.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException

/**
 * Created by Muhammad Ali on 04-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class CameraCaptureViewModel(application: Application) : BaseViewModel(application) {



    fun uploadImage(file: File, callFrom: Int) {
        logD(APP_TAG, "method Called")
        sendRequest()
        localService.uploadImage(file, callFrom)
            .enqueue(object : Callback<SendEncryptionRequest> {
                override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                   setError(call, "Internet Connectivity issue")

                }

                override fun onResponse(
                    call: Call<SendEncryptionRequest>,
                    response: Response<SendEncryptionRequest>
                ) {
                    if (response.code() == 200) {
                        val obj:JSONArray
                        try {
                            val sendEncrptRequest: SendEncryptionRequest? = response.body()
                            val jsonString: String? = sendEncrptRequest?.getText().toString()
                            val decrypted: String = Cryptography_Android.Decrypt(
                                jsonString,
                                Constaint.mKey
                            )
                            obj = JSONArray(decrypted)
                            val gson = Gson()
                            val collectionType =
                                object : TypeToken<Collection<String?>?>() {}.type
                            val enums: Collection<String> =
                                gson.fromJson<Collection<String>>(
                                    obj.toString(),
                                    collectionType
                                )



                            val imagePathList: List<String> =
                                enums as List<String>
//

                            setSuccess(imagePathList)
                        } catch (e: Exception) {
                            setError(response, "")
                        }
                    } else {
                        setError(response, "")

                    }
                }

            })
    }

    fun uploadCNICImages(frontImage: File,backImage:File) {
        logD(APP_TAG, "method Called")
        sendRequest()
        logD("**cnicImg","FRONT:  "+frontImage)
        logD("**cnicImg","BACK:  "+backImage)
//        logD("**cnicImg","CALLFROM:  "+callFrom)


        localService.uploadCNICImages(frontImage,backImage, 2)
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
                            val obj:JSONArray
                            try {
                                val sendEncrptRequest: SendEncryptionRequest? = response.body()
                                val jsonString: String? = sendEncrptRequest?.getText().toString()
                                val decrypted: String = Cryptography_Android.Decrypt(
                                    jsonString,
                                    Constaint.mKey
                                )
                                obj = JSONArray(decrypted)
                                val gson = Gson()
                                val collectionType =
                                    object : TypeToken<Collection<String?>?>() {}.type
                                val enums: Collection<String> =
                                    gson.fromJson<Collection<String>>(
                                        obj.toString(),
                                        collectionType
                                    )



                                val imagePathList: List<String> =
                                    enums as List<String>
//

                                setSuccess(imagePathList)
                            } catch (e: Exception) {
                                setError(response, "")
                            }
//                            setSuccess(response.body())
                        } catch (e: Exception) {
                            setError(response, "")
                        }
                    } else {
                        setError(response, "")

                    }
                }

            })
    }

    fun uploadProfileImages(frontImage: File) {
        logD(APP_TAG, "method Called")
        sendRequest()
        logD("**cnicImg","FRONT:  "+frontImage)

        localService.uploadPofileImages(frontImage, 1)
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
                            val obj:JSONArray
                            try {
                                val sendEncrptRequest: SendEncryptionRequest? = response.body()
                                val jsonString: String? = sendEncrptRequest?.getText().toString()
                                val decrypted: String = Cryptography_Android.Decrypt(
                                    jsonString,
                                    Constaint.mKey
                                )
                                obj = JSONArray(decrypted)
                                val gson = Gson()
                                val collectionType =
                                    object : TypeToken<Collection<String?>?>() {}.type
                                val enums: Collection<String> =
                                    gson.fromJson<Collection<String>>(
                                        obj.toString(),
                                        collectionType
                                    )



                                val imagePathList: List<String> =
                                    enums as List<String>
//

                                setSuccess(imagePathList)
                            } catch (e: Exception) {
                                setError(response, "")
                            }
                        } catch (e: Exception) {
                            setError(response, "")
                        }
                    } else {
                        setError(response, "")

                    }
                }

            })
    }
/*

    override fun sendRequest() {
        logD(APP_TAG, "method sendRequest")

        requestHandlerMLD.value = RequestHandler(
            loading = true,
            isSuccess = false,
            any = null
        )
    }

    override fun setError(obj: Any?, msg: String?, showAlert: Boolean) {
        logD(APP_TAG, "method error")
        requestHandlerMLD.value = RequestHandler(
            loading = false,
            isSuccess = false,
            showAlert = showAlert,
            any = obj
        )
    }

    override fun setSuccess(obj: Any?) {
        logD(APP_TAG, "method Success")
        requestHandlerMLD.value = RequestHandler(
            loading = false,
            isSuccess = true,
            any = obj
        )
    }
*/

    override fun onCleared() {
        super.onCleared()
    }
}