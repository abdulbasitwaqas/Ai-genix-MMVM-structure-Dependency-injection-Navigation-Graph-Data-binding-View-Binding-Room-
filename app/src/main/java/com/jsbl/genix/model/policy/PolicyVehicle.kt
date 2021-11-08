package com.jsbl.genix.model.policy


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PolicyVehicle(
    @SerializedName("LicensePlate")
    var licensePlate: String?="",
    @SerializedName("Make")
    var make: String?="",
    @SerializedName("MakeModelCode")
    var makeModelCode: String? = "",
    @SerializedName("Model")
    var model: String?= "",
    @SerializedName("MotorType")
    var motorType: String?="",
    @SerializedName("OverrideData")
    var overrideData: Boolean? = false,
    @SerializedName("VIN")
    var vIN: String? = "",
    @SerializedName("YearOfInitialRegistration")
    var yearOfInitialRegistration: Int?=1970
) : Parcelable