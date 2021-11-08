package com.jsbl.genix.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reward(
    var trip: String = "",
    var startingAddress: String = "",
    var endingAddress: String = "",
    var startingPoint: Double = 0.0,
    var endingPoint: Double = 0.0,
    var time: Long = 0,
    var distance: Double = 0.0,
    var score: Int = 1
) : Parcelable