package com.jsbl.genix.trips

import android.os.Parcelable
import com.scope.mhub.models.Acceleration
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripItem(
    var startingAddress: String = "",
    var endingAddress: String = "",
    var filterLabel: String = "",
    var distanceValue: Double = 0.0,

    var trip: String = "",
    var startingPoint: Double = 0.0,
    var endingPoint: Double = 0.0,
    var time: Long = 0,
    var score: Int = 1,
    var points: Int = 0,

    var acceleration: Int = 0,
    var cornering: Int = 0,
    var speeding: Int = 0,
    var braking: Int = 0,
) : Parcelable