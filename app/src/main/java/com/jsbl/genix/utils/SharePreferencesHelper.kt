package com.jsbl.genix.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.jsbl.genix.StatsModel
import com.jsbl.genix.utils.services.ServiceState
import com.google.gson.Gson
import com.jsbl.genix.Encrpition.Constaint
import com.jsbl.genix.Encrpition.Cryptography_Android
import com.jsbl.genix.Encrpition.SendEncryptionRequest
import com.jsbl.genix.model.NetworkModel.UserRedeemsGetRequest


/**
 * Created by Muhammad Ali on 19-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class SharePreferencesHelper {

    // Test
    companion object {
        private const val PREFS_TIME = "prefs_time"
        private const val AUTH_TOKEN = "Auth_token"
        private const val CUSTOMER_ID = "customer_id"
        private const val SCOPE_PASS = "scope_pass"
        private const val SCOPE_NAME = "scope_name"
        private const val DefaultCarPos = "default_car_pos"
        private const val carPos = "car_pos"
        private const val STATS_MODEL = "stats_model"

        private const val SERVICE_STATE = "genixForegroundServiceState"
         var prefs: SharedPreferences? = null
        var sharedEditor: SharedPreferences.Editor? = null

        @Volatile
        private var instance: SharePreferencesHelper? = null
        private var lock = Any()


        operator fun invoke(context: Context): SharePreferencesHelper =
            instance ?: kotlin.synchronized(lock) {
                instance ?: buildHelper(context).also {
                    instance = it
                }
            }

        private fun buildHelper(context: Context): SharePreferencesHelper {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharePreferencesHelper()
        }
    }

    fun updateTime(time: Long) {
        prefs?.edit(commit = true) {
            putLong(PREFS_TIME, time)
        }
    }

    fun updateAuth(auth: String) {
        prefs?.edit(commit = true) {
            putString(AUTH_TOKEN, auth)
        }
    }

    fun updateScopeName(auth: String) {
        prefs?.edit(commit = true) {
            putString(SCOPE_NAME, auth)
        }
    }

    fun updateScopePass(auth: String) {
        prefs?.edit(commit = true) {
            putString(SCOPE_PASS, auth)
        }
    }

    fun updateCustomerId(id: Long) {
        prefs?.edit(commit = true) {
            putLong(CUSTOMER_ID, id)
        }
    }

    fun setRegPassword(password: String) {
        prefs?.edit(commit = true) {
            putString(SCOPE_PASS, password)
        }
    }
    fun getRegPassword(): String = if (prefs != null) {
        prefs?.getString(SCOPE_PASS, "")!!
    } else {
        ""
    }

    fun setDefaultCarPos(postition: Int) {
        prefs?.edit(commit = true) {
            putInt(carPos, postition)
        }
    }
    fun getDefaultCarPos(): Int = if (prefs != null) {
        prefs?.getInt(carPos, 0)!!
    } else {
        0
    }

    fun setScopeToken(scopeToken: String) {
        prefs?.edit(commit = true) {
            putString(DefaultCarPos, scopeToken)
        }
    }
    fun getScopeToken(): String = if (prefs != null) {
        prefs?.getString(DefaultCarPos, "")!!
    } else {
        ""
    }

    fun updateForegroundServiceState(state: ServiceState) {
        prefs?.edit(commit = true) {
            putString(SERVICE_STATE, state.name)
        }
    }

    fun getTime() = prefs?.getLong(PREFS_TIME, 0L)

    fun getAuth(): String = if (prefs != null) {
        prefs?.getString(AUTH_TOKEN, "")!!
    } else {
        ""
    }
    fun getScopeName(): String = if (prefs != null) {
        prefs?.getString(SCOPE_NAME, "")!!
    } else {
        ""
    }

    fun getScopePass(): String = if (prefs != null) {
        prefs?.getString(SCOPE_PASS, "")!!
    } else {
        ""
    }
    fun getForegroundServiceState(): ServiceState = if (prefs != null) {
         ServiceState.valueOf(prefs?.getString(SERVICE_STATE, ServiceState.STOPPED.name)!!)
    } else {
        ServiceState.STOPPED
    }

    fun getCustomerId(): Long = if (prefs != null) {
        prefs?.getLong(CUSTOMER_ID, 0)!!
    } else {
        0
    }

    fun getCachePreferences() = prefs?.getString("duration", "")

    fun logout(){
        prefs?.edit()!!.clear().commit()
    }
    fun saveStatsModel(statsModel: StatsModel){
        val gson = Gson()
        val json = gson.toJson(statsModel)
        prefs?.edit(commit = true) {
            putString(STATS_MODEL, json)
        }
    }

    fun getStatsModel():StatsModel {
        if (prefs != null) {
            val gson = Gson()
            val json: String = prefs?.getString(STATS_MODEL, "")!!
            val statsModel = gson.fromJson(json, StatsModel::class.java)
            if (statsModel == null)
                return StatsModel()
            else
                return statsModel
        } else {
            return StatsModel()
        }
    }

    fun encryptUserId(iD: String): String {
        val userRedeemsGetRequest = UserRedeemsGetRequest(iD)
        val json = Gson().toJson(userRedeemsGetRequest)
        var Request: String? = null

        try {
            Request = Cryptography_Android.Encrypt(iD, Constaint.mKey)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        var newVariable = Request
        if (Request!!.contains("\n")){
            newVariable = Request.replace("\n","")
        }
        val Text = newVariable
        val sendEncrptRequest = SendEncryptionRequest()
        sendEncrptRequest.setText(Text)
        return sendEncrptRequest.text
    }

}