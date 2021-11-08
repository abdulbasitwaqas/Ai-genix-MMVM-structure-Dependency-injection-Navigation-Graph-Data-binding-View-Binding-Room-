package com.jsbl.genix.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class getTripsModel (
    var token: String = "",
    var span: String = ""
    ) :Parcelable


