package com.jsbl.genix.model.deletecar

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeleteCar(
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("regNo")
    var regNo: String? = ""
) : Parcelable