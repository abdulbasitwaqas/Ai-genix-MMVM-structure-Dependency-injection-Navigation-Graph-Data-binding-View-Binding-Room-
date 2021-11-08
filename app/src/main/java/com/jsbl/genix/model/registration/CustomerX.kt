package com.jsbl.genix.model.registration


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.jsbl.genix.model.profileManagement.Interest
import com.jsbl.genix.model.profileManagement.InterestX
import com.jsbl.genix.model.profileManagement.PostCarDetail
import com.jsbl.genix.model.profileManagement.PostFeedBack
import com.jsbl.genix.model.typeConverter.CarDetailsTypeConverter
import com.jsbl.genix.model.typeConverter.FeedbackTypeConverter
import com.jsbl.genix.model.typeConverter.InterestTypeConverter
import com.jsbl.genix.model.typeConverter.InterestXTypeConverter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class CustomerX(
    @SerializedName("BirthPlace")
    var birthPlace: String? = "",
    @SerializedName("CNIC")
    var cNIC: String? = "",
    @SerializedName("CNICFrontImagePath")
    var cNIC_frontImageUrl: String? = "",
    @SerializedName("CNICBackImagePath")
    var cNIC_backImageUrl: String? = "",
    @SerializedName("CNICExpiry")
    var cNICExpiry: String? = "",
    @SerializedName("Isinsured")
    var Isinsured: Boolean = false,
    @TypeConverters(CarDetailsTypeConverter::class)
    @SerializedName("carDetails")
    var carDetails: ArrayList<PostCarDetail>? = arrayListOf<PostCarDetail>(),
    @SerializedName("CurrentAddress")
    var currentAddress: String? = "",
    @TypeConverters(InterestTypeConverter::class)
    @SerializedName("customerInterests")
    var customerInterests: List<Interest>? = null,
    @SerializedName("DOB")
    var dOB: String? = "",
    @SerializedName("Email")
    var email: String? = "",
    @TypeConverters(FeedbackTypeConverter::class)
    @SerializedName("feedBacks")
    var feedBacks: PostFeedBack? = null,
    @SerializedName("Gender")
    var gender: String? = "",
    @PrimaryKey(autoGenerate = false)
    @SerializedName("ID")
    var iD: Long? = 0,
    @SerializedName("IMEINO")
    var imei: String? = "",
    @SerializedName("MaritalStatus")
    var maritalStatus: String? = "",
    @SerializedName("Mobile")
    var mobile: String? = "",
    @SerializedName("MotherName")
    var motherName: String? = "",
    @SerializedName("Name")
    var name: String? = "Name",
    @SerializedName("Username")
    var userName: String= "",
    @SerializedName("isOTPSent")
    var isOTPSent: Boolean = false,
    @SerializedName("MobileNo")
    var mobileNo: String?="",
    @SerializedName("Password")
    var password: String? = "",
    @SerializedName("Phone")
    var phone: String? = "",
    @SerializedName("ProfileImagePath")
    var profileImagePath: String? = "",
    @SerializedName("Status")
    var status: Boolean? = false,
    @SerializedName("ScopeId")
    var scopeId: String? = null,
    @SerializedName("scopeToken")
    var scopeToken: String? = "",
    @SerializedName("Token")
    var token: String? = "",
    @SerializedName("Percentage")
    var percentage: Int? = 0
) : Parcelable
/*
{
    init {
        birthPlace="Islamabad"
        cNICExpiry="01-04-2021"
        dOB="01-04-1990"
        motherName="Mother Name"
        name="Name"
    }
}*/
