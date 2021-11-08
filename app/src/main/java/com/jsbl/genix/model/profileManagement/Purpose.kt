package com.jsbl.genix.model.profileManagement

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Purpose(
    @SerializedName("ID")
    var iD: Long? = -1,
    @SerializedName("Purpose")
    var name: String? = ""
):Parcelable{
    override fun toString(): String {
        return name.toString()
    }
}
