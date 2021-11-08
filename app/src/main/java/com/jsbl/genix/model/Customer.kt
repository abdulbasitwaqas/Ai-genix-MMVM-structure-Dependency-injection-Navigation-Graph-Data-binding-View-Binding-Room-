package com.jsbl.genix.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Customer(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("ID")
    var iD: Long?,
    @SerializedName("Active")
    var active: Boolean?,
    @SerializedName("BirthPlace")
    var birthPlace: String?,
    @SerializedName("CNIC")
    var cNIC: String?,
    @SerializedName("CNICExpiry")
    var cNICExpiry: String?,
    @SerializedName("CreatedDate")
    var createdDate: String?,
    @SerializedName("CurrentAddress")
    var currentAddress: String?,
    @SerializedName("DOB")
    var dOB: String?,
    @SerializedName("Deleted")
    var deleted: Boolean?,
    @SerializedName("Email")
    var email: String?,
    @SerializedName("Image")
    var image: String?,
    @SerializedName("MaritalStatus")
    var maritalStatus: String?,
    @SerializedName("Mobile")
    var mobile: String?,
    @SerializedName("ModifiedDate")
    var modifiedDate: String?,
    @SerializedName("MotherName")
    var motherName: String?,
    @SerializedName("Name")
    var name: String?,
    @SerializedName("Password")
    var password: String?,
    @SerializedName("Phone")
    var phone: String?,
    @SerializedName("ProfileImagePath")
    var profileImagePath: String?,
    @SerializedName("Status")
    var status: String?
) : Parcelable