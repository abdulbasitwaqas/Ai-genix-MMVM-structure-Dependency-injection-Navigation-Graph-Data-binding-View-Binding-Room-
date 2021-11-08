package com.jsbl.genix.model.NetworkModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PasswordRequest(
    @SerializedName("Password")
    var oTP: String? = ""
): Parcelable