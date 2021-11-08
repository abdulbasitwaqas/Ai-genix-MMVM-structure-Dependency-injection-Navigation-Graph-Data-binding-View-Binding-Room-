package com.jsbl.genix.model

import com.google.gson.annotations.SerializedName

data class GetStatsFeedBackModel(

	@field:SerializedName("Items")
	val items: List<ItemsItem>? = null,

	@field:SerializedName("ErrorCode")
	val errorCode: Int? = null,

	@field:SerializedName("ErrorMessage")
	val errorMessage: String? = null
)

data class ItemsItem(

	@field:SerializedName("CommonDescription")
	val commonDescription: Any? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("References")
	val references: Any? = null,

	@field:SerializedName("Value")
	val value: Double? = null,

	@field:SerializedName("Title")
	val title: String? = null,

	@field:SerializedName("RangeMin")
	val rangeMin: Double? = null,

	@field:SerializedName("RangeMax")
	val rangeMax: Double? = null,

	@field:SerializedName("Key")
	val key: String? = null
)
