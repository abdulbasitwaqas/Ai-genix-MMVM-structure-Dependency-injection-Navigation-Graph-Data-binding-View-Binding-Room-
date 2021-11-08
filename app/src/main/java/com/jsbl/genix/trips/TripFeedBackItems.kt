package com.jsbl.genix.trips

import com.google.gson.annotations.SerializedName

data class TripFeedBackItems(

	@field:SerializedName("CommonDescription")
	val commonDescription: Any? = null,

	@field:SerializedName("Description")
	val description: Any? = null,

	@field:SerializedName("References")
	val references: Any? = null,

	@field:SerializedName("Value")
	val value: Double? = null,

	@field:SerializedName("Title")
	val title: Any? = null,

	@field:SerializedName("RangeMin")
	val rangeMin: Int? = null,

	@field:SerializedName("RangeMax")
	val rangeMax: Int? = null,

	@field:SerializedName("Key")
	val key: Any? = null,

	@field:SerializedName("Name")
	val name: String? = null
)
