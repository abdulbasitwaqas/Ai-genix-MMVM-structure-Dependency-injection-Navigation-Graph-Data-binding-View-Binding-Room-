package com.jsbl.genix.model.profileManagement

import com.google.gson.annotations.SerializedName

data class GetCustomerInterestById(

	@field:SerializedName("GetCustomerInterestById")
	val getCustomerInterestById: ArrayList<GetCustomerInterestByIdItem?>? = null
)

data class GetCustomerInterestByIdItem(

	@field:SerializedName("InterestID")
	val interestID: Int? = null,

	@field:SerializedName("SubInterestID")
	val subInterestID: Any? = null,

	@field:SerializedName("SubInterest")
	val subInterest: Any? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("CustomerID")
	val customerID: Int? = null,

	@field:SerializedName("Deleted")
	val deleted: Boolean? = null
)
