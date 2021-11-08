package com.jsbl.genix.model.profileManagement

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeleteCarRequest (
    @SerializedName("id")
    var id: Long? =-1,
    @SerializedName("regNo")
    var regNo: String? = ""
): Parcelable