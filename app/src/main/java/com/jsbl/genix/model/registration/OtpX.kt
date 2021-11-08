package com.jsbl.genix.model.registration


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class OtpX(
    @SerializedName("MobileNo")
    @Expose
    var mobileNo: String?,
    @SerializedName("Type")
    @Expose
    var type: String?,
    @SerializedName("email")
    @Expose
    var email: String?,
    @SerializedName("OTP")
    @Expose
    var oTP: String?,
    @SerializedName("IMEINO")
    @Expose
    var imei: String? = ""


) : Parcelable