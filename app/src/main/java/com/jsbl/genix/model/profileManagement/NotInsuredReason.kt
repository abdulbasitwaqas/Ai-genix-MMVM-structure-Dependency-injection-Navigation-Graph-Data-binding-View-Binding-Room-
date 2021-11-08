package com.jsbl.genix.model.profileManagement


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class NotInsuredReason(
    @SerializedName("CreatedDate")
    var createdDate: String? = "",
    @SerializedName("Deleted")
    var deleted: Boolean?,
    @SerializedName("ID")
    var iD: Long? = -1,
    @SerializedName("ModifiedDate")
    var modifiedDate: String? = "",
    @SerializedName("Remarks")
    var remarks:String? = "",
    @SerializedName("Title")
    var title: String? = ""
):Parcelable{
    override fun toString(): String {
        return title.toString()
    }
}