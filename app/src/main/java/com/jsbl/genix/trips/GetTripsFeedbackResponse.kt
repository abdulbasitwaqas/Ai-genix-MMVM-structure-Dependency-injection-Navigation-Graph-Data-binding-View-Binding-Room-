package com.jsbl.genix.trips

import com.google.gson.annotations.SerializedName

data class GetTripsFeedbackResponse(

	@field:SerializedName("Items")
	val items: List<TripFeedBackItems>? = null,

	@field:SerializedName("ErrorCode")
	val errorCode: Int? = null,

	@field:SerializedName("ErrorMessage")
	val errorMessage: Any? = null
)
