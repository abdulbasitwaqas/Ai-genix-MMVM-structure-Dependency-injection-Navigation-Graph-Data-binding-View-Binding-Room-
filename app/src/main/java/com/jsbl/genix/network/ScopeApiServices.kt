package com.jsbl.genix.network

import retrofit2.Call
import retrofit2.http.GET

interface ScopeApiServices {
        @GET("/api/Trips")
        fun getTripList(
        ): Call<Any?>
    }
