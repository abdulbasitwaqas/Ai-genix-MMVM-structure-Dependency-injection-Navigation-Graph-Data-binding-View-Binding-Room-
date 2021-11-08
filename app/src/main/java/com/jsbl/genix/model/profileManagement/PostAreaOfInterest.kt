package com.jsbl.genix.model.profileManagement


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostAreaOfInterest(
    @SerializedName("interests")
    var interests: List<Interest>?
) : Parcelable