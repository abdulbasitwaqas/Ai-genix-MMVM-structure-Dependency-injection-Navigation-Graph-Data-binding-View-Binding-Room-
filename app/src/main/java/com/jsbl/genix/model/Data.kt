package com.jsbl.genix.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.jsbl.genix.model.profileManagement.*
import com.jsbl.genix.model.registration.OtpX
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    @SerializedName("OTP")
    var oTP: String? = "",
    @SerializedName("url")
    var filePath: String? = "",
    @SerializedName("customerInfo")
    var customerInfo: Customer?,
    @SerializedName("colors")
    var colors: List<Color>?,
    @SerializedName("interests")
    var interests: List<InterestX>?,
    @SerializedName("makers")
    var makers: List<Maker>?,
    @SerializedName("manufacturers")
    var manufacturers: List<Manufacturer>?,
    @SerializedName("notInsuredReasons")
    var notInsuredReasons: List<NotInsuredReason>?,
    @SerializedName("customerInterests")
    var customerInterest: List<InterestX>?,
    @SerializedName("carDetails")
    var carDetails: PostCarDetail?,
    @SerializedName("feedBacks")
    var feedBack: PostFeedBack?
) : Parcelable