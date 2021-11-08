package com.jsbl.genix.model.registration


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginMdl(
    @SerializedName("UserName")
    var mobile: String? = "",
    @SerializedName("Password")
    var password: String? = "",
    @SerializedName("IMEINO")
    var imei: String? = ""
) : Parcelable