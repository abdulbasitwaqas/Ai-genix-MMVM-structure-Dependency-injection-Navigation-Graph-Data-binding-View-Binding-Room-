package com.jsbl.genix.trips

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripItemResponse (
    @SerializedName("Data") val tripsDetailsModelList : List<TripsDetailModel>
): Parcelable