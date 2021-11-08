package com.jsbl.genix.model.questions


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import android.webkit.DateSorter
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuestionResponseItem(
    @SerializedName("ID")
    var iD: Int? = 0,
    @SerializedName("IsActive")
    var isActive: Boolean? = false,
    @SerializedName("Lenght")
    var lenght: Int? = 0,
    @SerializedName("Placeholder")
    var placeholder: String? = "",
    @SerializedName("Question")
    var question: String? = "",
    @SerializedName("QuestionType")
    var questionType: String? = "",
    @SerializedName("Identifier")
    var identifier: Int = 0,
    @SerializedName("Step")
    var step: Int? = 0
) : Parcelable