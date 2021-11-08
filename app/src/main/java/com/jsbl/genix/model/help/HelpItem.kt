package com.jsbl.genix.model.help

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HelpItem(var id: Int, var icon: String,var drawable:Int, var question: String, var description: String) :
    Parcelable