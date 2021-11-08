package com.app.architecturekotlin.retrofit

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.jsbl.genix.network.retrofit.model.ApiResponse
import com.google.gson.Gson
import com.jsbl.genix.R
import com.jsbl.genix.network.retrofit.BaseCallBack
import com.jsbl.genix.views.dialogs.ProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class ResponseCallBack<T>(
    var context: Context,
    val fragmentManager: FragmentManager? = null
) : BaseCallBack<T>(), Callback<T> {
//    lateinit var dialogP: ProgressDialog
    lateinit var mProgressDialog: android.app.ProgressDialog


    override fun onResponse(
        call: Call<T>,
        response: Response<T>
    ) {
        dismissDialog()
        val taskItem = ApiResponse<T>()
        taskItem.isError = !response.isSuccessful
        taskItem.responseCode = response.code()
        try {
            if (response.code() in 400..598) {
                if (response.code() == 401) {
                    onSessionExpire()
//                    showSessionExpireToken(context)
                }
            } else {
                taskItem.response = Gson().toJson(response.body())
                taskItem.responseObject = response.body()
                onSuccess(taskItem)
            }
        } catch (e: Exception) {
            taskItem.isError = true
            taskItem.message = e.localizedMessage
            onFailure(taskItem)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        dismissDialog()
        val taskItem = ApiResponse<T>()
        taskItem.isError = true
        taskItem.responseCode = -100
        taskItem.message =
            if (t.localizedMessage.isNullOrEmpty()) {
                "Something went wrong. Please try again Later"
            } else t.localizedMessage
        onFailure(taskItem)
    }

    init {
        showPDialog()
    }

    protected fun showPDialog() {
        if (fragmentManager != null) {
            showProgress(true)
        }
    }

    private fun dismissDialog() {
        showProgress(false)
    }


    private fun showProgress(show: Boolean) {
        if (show) {
            mProgressDialog = android.app.ProgressDialog(context,
                    R.style.CustomFontDialog)
            mProgressDialog.setMessage(context.resources.getString(R.string.string_message_wait))
            mProgressDialog.setIndeterminate(false)
            mProgressDialog.setCancelable(false)
            mProgressDialog.show()
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss()
            }
        }
    }
}