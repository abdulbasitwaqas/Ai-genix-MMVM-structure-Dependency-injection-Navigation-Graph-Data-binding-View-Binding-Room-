package com.jsbl.genix.trips

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class TripsDetailModel (
    @SerializedName("Id") val id : Int,
    @SerializedName("Distance") val distance : Double,
    @SerializedName("EndLocalTimestamp") val endLocalTimestamp : String,
    @SerializedName("EndUtcTimestamp") val endUtcTimestamp : String,
    @SerializedName("EndLocation") val endLocation : String,
    @SerializedName("EndLocationDescription") val endLocationDescription : String,
    @SerializedName("EndPosition") val endPosition : List<Double>,
    @SerializedName("EndLongitude") val endLongitude : Double,
    @SerializedName("EndLatitude") val endLatitude : Double,
    @SerializedName("MaxSpeed") val maxSpeed : Int,
    @SerializedName("NumberOfDriverBehaviourExceptions") val numberOfDriverBehaviourExceptions : String,
    @SerializedName("NumberOfExceptions") val numberOfExceptions : Int,
    @SerializedName("StartLocalTimestamp") val startLocalTimestamp : Date,
    @SerializedName("StartUtcTimestamp") val startUtcTimestamp : String,
    @SerializedName("StartLocation") val startLocation : String,
    @SerializedName("StartLocationDescription") val startLocationDescription : String,
    @SerializedName("StartPosition") val startPosition : List<Double>,
    @SerializedName("StartLongitude") val startLongitude : Double,
    @SerializedName("StartLatitude") val startLatitude : Double,
    @SerializedName("IsBusiness") val isBusiness : String,
    @SerializedName("SourceKey") val sourceKey : String,
    @SerializedName("Duration") val duration : String,
    @SerializedName("DurationMinutes") val durationMinutes : Int,
    @SerializedName("DurationInMinutes") val durationInMinutes : Int,
    @SerializedName("DurationSeconds") val durationSeconds : Int,
    @SerializedName("TripEvent") val TripEvent : List<TripsEventModel>
//    @SerializedName("TripEvent") val TripEvent : ArrayList<TripsEvent>
    ) : Parcelable