package com.jsbl.genix.model.profileManagement

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubManufacturerModel(
    @SerializedName("ID")
    var iD: Long? = -1,
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("Remarks")
    var remarks: String? = "",
    @SerializedName("TotalRecords")
    var totalRecords: String? = "",
    @SerializedName("ManufacturerID")
    var ManufacturerID: Long?




): Parcelable {
    override fun toString(): String {
        return name.toString()
    }
}

