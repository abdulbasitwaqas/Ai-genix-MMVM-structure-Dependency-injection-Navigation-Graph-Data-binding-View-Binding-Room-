package com.jsbl.genix.trips

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class TripsEvent(


    @SerializedName("T")
    @Expose
    val t: String?,

    @SerializedName("E")
    @Expose
    val E: String?,

    @SerializedName("p")
    @Expose
    val p: ArrayList<LatLng>
) : Parcelable

