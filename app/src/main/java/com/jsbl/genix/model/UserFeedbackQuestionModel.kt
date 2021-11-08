package com.jsbl.genix.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserFeedbackQuestionModel(

	@field:SerializedName("FQID")
	val fQID: String? = null,

	@field:SerializedName("AnswerStars")
	val answerStars: Int? = null
): Parcelable
