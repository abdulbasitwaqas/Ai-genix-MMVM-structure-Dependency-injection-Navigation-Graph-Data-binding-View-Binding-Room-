package com.jsbl.genix.model

import com.google.gson.annotations.SerializedName

data class GetInterestsModel(

	@field:SerializedName("InterestID")
	val interestID: Int? = null,

	@field:SerializedName("SubInterestID")
	val subInterestID: Any? = null,

	@field:SerializedName("SubInterest")
	val subInterest: String? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("CustomerID")
	val customerID: Int? = null,

	@field:SerializedName("Deleted")
	val deleted: Boolean? = null
)
