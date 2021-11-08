package com.jsbl.genix.model.policy


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterPolicyRequest(
    @SerializedName("AgreementAccepted")
    var agreementAccepted: Boolean? = true,
    @SerializedName("Password")
    var password: String? = "",
    @SerializedName("Reference")
    var reference: String? = "",
    @SerializedName("SerialNumber")
    var serialNumber: String? = "",
    @SerializedName("UserName")
    var userName: String? = ""
) : Parcelable