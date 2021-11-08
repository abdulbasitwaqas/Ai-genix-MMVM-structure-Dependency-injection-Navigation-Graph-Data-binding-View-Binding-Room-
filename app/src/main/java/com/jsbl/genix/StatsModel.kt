package com.jsbl.genix

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*


data class StatsModel (

    @field:SerializedName("DrivingDistance")
    val drivingDistance: Double? = null,

    @field:SerializedName("MaxSpeed")
    val maxSpeed: Double? = null,

    @field:SerializedName("TimeOfDay")
    val timeOfDay: Double? = null,

    @field:SerializedName("DriverScore")
    val driverScore: Any? = null,

    @field:SerializedName("Braking")
    val braking: Double? = null,

    @field:SerializedName("Brakings")
    val brakings: Any? = null,

    @field:SerializedName("Cornering")
    val cornering: Double? = null,

    @field:SerializedName("Speeding")
    val speeding: Double? = null,

    @field:SerializedName("FilterEnd")
    val filterEnd: String? = null,

    @field:SerializedName("Accelerations")
    val accelerations: Any? = null,

    @field:SerializedName("Speedings")
    val speedings: Any? = null,



    @field:SerializedName("FilterBegin")
    val filterBegin: String? = null,

    @field:SerializedName("Cornerings")
    val cornerings: Any? = null,

    @field:SerializedName("DrivingTime")
    val drivingTime: Int? = null,

    @field:SerializedName("NumberOfExceptions")
    val numberOfExceptions: Int? = null,

    @field:SerializedName("Score")
    val score: Double? = null,

    @field:SerializedName("MaxScore")
    val maxScore: Double? = null,

    @field:SerializedName("Username")
    val username: Any? = null,

    @field:SerializedName("ReedeemPoints")
    var reedeemPoints: Int = 0,

    @field:SerializedName("TotalPoints")
    val totalPoints: Int = 0,

    @field:SerializedName("WinningPoints")
    val winningPoints: Int = 0,

    @field:SerializedName("RemainingPoints")
    val remainingPoints: Int = 0,


    @field:SerializedName("TimeOfDays")
    val timeOfDays: Any? = null,


    @field:SerializedName("Acceleration")
    val acceleration: Double? = null,

    @field:SerializedName("MinScore")
    val minScore: Double? = null
) 