package com.jsbl.genix.model.NetworkModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserRedeemsGetRequest(
    @SerializedName("UserID")
    var userId: String? = ""
) : Parcelable