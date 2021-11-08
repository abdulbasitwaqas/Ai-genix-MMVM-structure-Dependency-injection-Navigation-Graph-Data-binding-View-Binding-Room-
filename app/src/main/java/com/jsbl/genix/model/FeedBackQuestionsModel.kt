package com.jsbl.genix.model

import com.google.gson.annotations.SerializedName

data class FeedBackQuestionsModel(

	@field:SerializedName("Status")
	val status: String? = null,

	@field:SerializedName("IsDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("UFQID")
	val uFQID: Int? = null,

	@field:SerializedName("Remarks")
	val remarks: String? = null,

	@field:SerializedName("FQID")
	val fQID: Int? = null,

	@field:SerializedName("TotalRecords")
	val totalRecords: Int? = null,

	@field:SerializedName("Question")
	val question: String? = null,

	@field:SerializedName("ID")
	val iD: Int,

	@field:SerializedName("AnswerStars")
	val answerStars: Int? = null
)
