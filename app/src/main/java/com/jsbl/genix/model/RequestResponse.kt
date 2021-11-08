package com.jsbl.genix.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RequestResponse(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("Message")
    var message: String?,
    @SerializedName("Status")
    var status: Boolean?,
    @SerializedName("StatusCode")
    var statusCode: Int?,
    @SerializedName("Token")
    var token: String?
) : Parcelable