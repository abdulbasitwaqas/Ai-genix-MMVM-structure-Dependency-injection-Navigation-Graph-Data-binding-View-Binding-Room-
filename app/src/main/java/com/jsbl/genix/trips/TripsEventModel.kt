package com.jsbl.genix.trips

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class TripsEventModel (
    @SerializedName("Point")
    val point : ArrayList<Double>?,
    @SerializedName("Time")
    val time : Date,
    @SerializedName("Event")
    val event : String="",
    @SerializedName("EventDescription")
    val EventDescription : String?
): Parcelable