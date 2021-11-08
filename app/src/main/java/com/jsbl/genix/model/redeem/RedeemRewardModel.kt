package com.jsbl.genix.model.redeem

import com.google.gson.annotations.SerializedName

data class RedeemRewardModel(

	@field:SerializedName("FilePath")
	val filePath: Any? = null,

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("ExpiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("IsDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("Description")
	val description: Any? = null,

	@field:SerializedName("WinningAmount")
	val winningAmount: Int? = null,

	@field:SerializedName("UserName")
	val userName: Any? = null,

	@field:SerializedName("ReedeemName")
	val reedeemName: Any? = null,

	@field:SerializedName("Title")
	val title: Any? = null,

	@field:SerializedName("Point")
	val point: Int? = null,

	@field:SerializedName("Count")
	val count: Int? = null,

	@field:SerializedName("ReedeemID")
	val reedeemID: Int? = null,

	@field:SerializedName("Flag")
	val flag: Any? = null,

	@field:SerializedName("responseCode")
	val responseCode: Any? = null,

	@field:SerializedName("ReedeemCount")
	val reedeemCount: Int? = null,

	@field:SerializedName("UserID")
	val userID: Int? = null,

	@field:SerializedName("ReedeemPoints")
	val reedeemPoints: Any? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("responseMessage")
	val responseMessage: Any? = null
)
