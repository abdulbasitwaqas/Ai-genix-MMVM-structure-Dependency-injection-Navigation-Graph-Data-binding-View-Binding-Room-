package com.jsbl.genix.trips

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetTripFeedBackRequest (
    @SerializedName("token")
    var scopeToken: String = "",
    @SerializedName("policyNo")
    var policyNo: String = "",
    @SerializedName("UserID")
    var UserID: String = "",
    @SerializedName("beginDate")
    var beginDate: String = "",
    @SerializedName("endDate")
    var endDate : String = ""
): Parcelable