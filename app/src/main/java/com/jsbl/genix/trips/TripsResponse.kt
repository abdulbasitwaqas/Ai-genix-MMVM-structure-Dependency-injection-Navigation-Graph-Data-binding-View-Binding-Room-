package com.jsbl.genix.trips

import com.google.gson.annotations.SerializedName
import java.util.*

data class TripsResponse(

	@SerializedName("Data") val tripsDetailsModelList : java.util.ArrayList<TripsDetailModel>,

	@field:SerializedName("Count")
	val count: Int? = null,

	@field:SerializedName("HasMoreResults")
	val hasMoreResults: Boolean? = null,

	@field:SerializedName("Date")
	val date: String = ""
)
