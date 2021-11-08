package com.jsbl.genix.model.registration

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class OTPVerificationModel(
    @SerializedName("MobileNo")
    var mobileNo: String?,
    @SerializedName("Type")
    var type: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("OTP")
    var oTP: String?,
    @SerializedName("IMEINO")
    var imei: String? = "",
    @SerializedName("Username")
    var Username: String?,
    @SerializedName("Password")
    var Password: String? = ""
) : Parcelable