package com.jsbl.genix.network

import com.utsman.samplegooglemapsdirection.kotlin.model.DirectionResponses
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleApiServices {
        @GET("maps/api/directions/json")
        fun getDirection(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
//            @Query("sensor") sensor: Boolean = true,
//            @Query("units") units: String = "metric",
//            @Query("mode") mode: String = "driving",
            @Query("key") apiKey: String
        ): Call<DirectionResponses>
    }