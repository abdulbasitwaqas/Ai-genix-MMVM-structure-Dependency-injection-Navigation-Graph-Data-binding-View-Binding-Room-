package com.jsbl.genix.model.NetworkModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateRedeemCartRequest(
    @SerializedName("UserID")
    var userId: String? = "",
    @SerializedName("ReedeemID")
    var redeemId: String? = "",
    @SerializedName("Flag")
    var flag: String? = "",
    @SerializedName("Count")
    var count: String? = ""
) : Parcelable