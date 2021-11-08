package com.jsbl.genix.model.policy


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PolicyContacts(
    @SerializedName("Address")
    var address: String? = "",
    @SerializedName("City")
    var city: String? = "",
    @SerializedName("Country")
    var country: String? = "",
    @SerializedName("Email")
    var email: String? = "",
    @SerializedName("FirstName")
    var firstName: String? = "",
    @SerializedName("MobilePhone")
    var mobilePhone: String? = "",
//    @SerializedName("Username")
//    var userName: String? = "",
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("ZipCode")
    var zipCode: String? = ""
) : Parcelable