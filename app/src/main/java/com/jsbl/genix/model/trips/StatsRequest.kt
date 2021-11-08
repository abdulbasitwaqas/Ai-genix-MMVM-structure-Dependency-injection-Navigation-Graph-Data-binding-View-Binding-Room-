package com.jsbl.genix.model.trips

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StatsRequest (
    @SerializedName("token")
    var token: String = "",
    @SerializedName("policyNo")
    var policyNo: String = "",
    @SerializedName("span")
    var span: String = "",
    @SerializedName("UserID")
    var UserID: String = ""
): Parcelable