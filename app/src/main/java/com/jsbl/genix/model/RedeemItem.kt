package com.jsbl.genix.model

import android.os.Parcelable
import com.scope.mhub.models.Acceleration
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RedeemItem(
    var title: String = "",
    var subtitle: String = "",
    var icon: Int = 0,
    var points: Int = 0,
    var type : Int = 0,
    var status : Int = 0
) : Parcelable