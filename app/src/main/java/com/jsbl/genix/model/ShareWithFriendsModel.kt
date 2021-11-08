package com.jsbl.genix.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShareWithFriendsModel (
    @SerializedName("CustomerID")
    var CustomerID: Long? ,
    @SerializedName("ShareCode")
    var ShareCode: String? = ""
    ) : Parcelable
