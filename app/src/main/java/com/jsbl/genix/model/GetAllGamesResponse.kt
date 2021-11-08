package com.jsbl.genix.model

import com.google.gson.annotations.SerializedName

data class GetAllGamesResponse(

	@field:SerializedName("GetAllGamesResponse")
	val getAllGamesResponse: List<GetAllGamesResponseItem?>? = null
)

data class GetAllGamesResponseItem(

	@field:SerializedName("FilePath")
	val filePath: String? = null,

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("IsDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("ExpiryDate")
	val expiryDate: Any? = null,

	@field:SerializedName("WinningAmount")
	val winningAmount: Int? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("CreatedBy")
	val createdBy: Int? = null,

	@field:SerializedName("Title")
	val title: String? = null,

	@field:SerializedName("TotalRecords")
	val totalRecords: Int? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("CreatedOn")
	val createdOn: String? = null,

	@field:SerializedName("ReedemStatus")
	val reedemStatus: Boolean? = null
)
