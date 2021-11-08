package com.jsbl.genix.viewModel

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.model.GetAllGamesResponseItem
import com.jsbl.genix.model.NetworkModel.UserRedeemsGetRequest
import com.jsbl.genix.model.games.GamesResponseModel
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.logD
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class GamificationViewModel(application: Application) : BaseViewModel(application) {


    fun getUserGames(iD: String) {
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
        localService.getUserGamesGET(sendEncrptRequest).enqueue(object : Callback<SendEncryptionRequest> {
            override fun onResponse(
                call: Call<SendEncryptionRequest>,
                response: Response<SendEncryptionRequest>
            ) {
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
                        val token: TypeToken<GamesResponseModel> =
                            object : TypeToken<GamesResponseModel>() {}
                        val gamificationList: GamesResponseModel =
                            gson.fromJson(decrypted, token.type)
                        // decryption end
                        setSuccess(gamificationList)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    setError(response, null)
                }
            }

            override fun onFailure(call: Call<SendEncryptionRequest>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    setError(call, "Internet Connectivity issue")
                } else {
                    setError(call, t.message)
                }

            }

        })
    }
}