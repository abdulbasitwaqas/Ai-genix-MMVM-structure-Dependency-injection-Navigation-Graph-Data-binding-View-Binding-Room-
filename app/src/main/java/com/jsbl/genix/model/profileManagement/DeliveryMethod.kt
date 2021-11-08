package com.jsbl.genix.model.profileManagement


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliveryMethod(
    @SerializedName("ID")
    var iD: Long? = -1,
    @SerializedName("Name")
    var name: String? = ""
):Parcelable