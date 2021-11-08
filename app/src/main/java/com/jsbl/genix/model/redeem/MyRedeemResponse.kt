package com.jsbl.genix.model.redeem

import com.google.gson.annotations.SerializedName

data class MyRedeemResponse(

	@field:SerializedName("ReedemListActive")
	val reedemListActive: List<ReedemListActiveItem?>? = null,

	@field:SerializedName("ReedemListInActive")
	val reedemListInActive: List<ReedemListInActiveItem?>? = null
)

data class ReedemListActiveItem(

	@field:SerializedName("FilePath")
	val filePath: String? = null,

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("ExpiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("IsDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("ReedeemName")
	val reedeemName: Any? = null,

	@field:SerializedName("Title")
	val title: String? = null,

	@field:SerializedName("Point")
	val point: Int? = null,

	@field:SerializedName("ReedeemID")
	val reedeemID: Int? = null,

	@field:SerializedName("Flag")
	val flag: Any? = null,

	@field:SerializedName("responseCode")
	val responseCode: String? = null,

	@field:SerializedName("ReedeemCount")
	var reedeemCount: Int? = null,

	@field:SerializedName("ReedeemPoints")
	var reedeemPoints: Any? = null,

	@field:SerializedName("UserID")
	val userID: Int? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("responseMessage")
	val responseMessage: String? = null
)

data class ReedemListInActiveItem(

	@field:SerializedName("FilePath")
	val filePath: String? = null,

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("ExpiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("IsDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("ReedeemName")
	val reedeemName: Any? = null,

	@field:SerializedName("Title")
	val title: String? = null,

	@field:SerializedName("Point")
	val point: Double? = null,

	@field:SerializedName("ReedeemID")
	val reedeemID: Int? = null,

	@field:SerializedName("Flag")
	val flag: Any? = null,

	@field:SerializedName("responseCode")
	val responseCode: String? = null,

	@field:SerializedName("ReedeemCount")
	val reedeemCount: Int? = null,

	@field:SerializedName("ReedeemPoints")
	val reedeemPoints: Any? = null,

	@field:SerializedName("UserID")
	val userID: Int? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("responseMessage")
	val responseMessage: Any? = null
)
