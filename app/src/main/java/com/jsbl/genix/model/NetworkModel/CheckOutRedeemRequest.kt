package com.jsbl.genix.model.NetworkModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheckOutRedeemRequest(
    @SerializedName("UserID")
    var userId: String? = "",
    @SerializedName("Reedeems")
    var redeems: String? = "",
):Parcelable