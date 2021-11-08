package com.jsbl.genix.viewModel

import android.app.Application
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.db.dao.CustomerDao
import com.jsbl.genix.di.modules.AppModule
import com.jsbl.genix.di.components.DaggerServiceComponent
import com.jsbl.genix.model.profileManagement.ResponseFillDropDown
import com.jsbl.genix.model.registration.CustomerX
import com.jsbl.genix.network.LocalService
import com.jsbl.genix.utils.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

/**
 * Created by Muhammad Ali on 04-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class ProfileManagementViewModel(application: Application) : BaseViewModel(application) {



/*
    fun getDropDown() {
        logD(APP_TAG, "method Called")
        localService.getDropDown().enqueue(object : Callback<ResponseFillDropDown> {
            override fun onFailure(call: Call<ResponseFillDropDown>, t: Throwable) {
                setError(call,null)
            }

            override fun onResponse(
                call: Call<ResponseFillDropDown>,
                response: Response<ResponseFillDropDown>
            ) {
                if (response.code() == 200) {
                    try {

                        setSuccess(response.body())
                    }catch (e:Exception){
                        setError(response,null)
                    }
                } else {
                    setError(response,null)
                }
            }

        })
    }*/



}