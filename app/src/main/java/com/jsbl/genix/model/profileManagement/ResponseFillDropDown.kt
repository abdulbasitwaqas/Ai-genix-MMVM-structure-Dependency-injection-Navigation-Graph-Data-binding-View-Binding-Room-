package com.jsbl.genix.model.profileManagement


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class ResponseFillDropDown(
    @SerializedName("colors")
    var colors: List<Color>?,
    @SerializedName("interests")
    var interests: List<InterestX>?,
    @SerializedName("makers")
    var makers: List<Maker>?,
    @SerializedName("model")
    var model: List<SubManufacturerModel>?,
    @SerializedName("purpose")
    var purpose: List<Purpose>?,
    @SerializedName("manufacturers")
    var manufacturers: List<Manufacturer>?,
    @SerializedName("InterestSubInterest")
    var InterestSubInterest: List<InterestSubInterest>?,

    @SerializedName("notInsuredReasons")
    var notInsuredReasons: List<NotInsuredReason>?,
    @SerializedName("deliveryMethods")
    var deliveryMethods: List<DeliveryMethod>?,
    @SerializedName("motorTypes")
    var motorTypes: List<MotorType>?,
    @SerializedName("deviceTypes")
    var deviceTypes: List<DeviceType>?,
    @PrimaryKey(autoGenerate = false)
    var objId: Int = 1
) : Parcelable