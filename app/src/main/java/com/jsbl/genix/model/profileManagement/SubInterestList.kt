package com.jsbl.genix.model.profileManagement

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class SubInterestList (
    @SerializedName("ID")
    var iD: Int,
    @SerializedName("Title")
    var title: String? = "",
    @SerializedName("InterestID")
    var parentInterestID: Int? ,
    @SerializedName("Interest")
    var Interest:String?,
    @SerializedName("Deleted")
    var Deleted:String?,
    @SerializedName("Status")
    var Status:String?,
    @SerializedName("TotalRecords")
    var TotalRecords:Int?,
    @SerializedName("FilePath")
    var FilePath:String?,
    var isSelected: Boolean = false

) : Parcelable {
    override fun toString(): String {
        return title.toString()
    }
}