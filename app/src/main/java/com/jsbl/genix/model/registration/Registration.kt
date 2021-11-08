package com.jsbl.genix.model.registration


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Registration(
    @SerializedName("BirthPlace")
    var birthPlace: String? = "",
    @SerializedName("CNIC")
    var cNIC: String? = "1234567890123",
    @SerializedName("CNICExpiry")
    var cNICExpiry: String? = "",
    @SerializedName("CurrentAddress")
    var currentAddress: String? = "",
    @SerializedName("DOB")
    var dOB: String? = "",
    @SerializedName("Email")
    var email: String? = "",
    @SerializedName("IMEINO")
    var imei: String? = "",
    @SerializedName("Image")
    var image: String? = "",
    @SerializedName("MaritalStatus")
    var maritalStatus: String? = "",
    @SerializedName("Mobile")
    var mobile: String? = "",
    @SerializedName("MotherName")
    var motherName: String? = "",
    @SerializedName("Password")
    var password: String? = "",
    @SerializedName("Phone")
    var phone: String? = "",
    @SerializedName("ProfileImagePath")
    var profileImagePath: String? = ""
) : Parcelable