package com.jsbl.genix.model.trips

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetAllTripsRequest (
    @SerializedName("token")
    var scopeToken: String = "",
    @SerializedName("policyNo")
    var policyNo: String = "",
    @SerializedName("UserID")
    var UserID: String = "",
    @SerializedName("pageIndex")
    var pageIndex : Int = 0,
    @SerializedName("pageSize")
    var pageSize : Int = 10
): Parcelable
