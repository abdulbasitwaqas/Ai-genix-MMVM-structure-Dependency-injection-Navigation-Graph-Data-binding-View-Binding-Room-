package com.jsbl.genix.network

import android.content.Context
import com.jsbl.genix.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleRetrofitClient {
        fun apiServices(context: Context): GoogleApiServices {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL_GOOGLE_APIS)
                .build()

            return retrofit.create<GoogleApiServices>(GoogleApiServices::class.java)
        }
    }