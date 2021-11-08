package com.jsbl.genix.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponseModel(

	@field:SerializedName("MobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Email")
	val email: Any? = null,

	@field:SerializedName("IMEINO")
	val iMEINO: Any? = null,

	@field:SerializedName("EmailOTPExpiry")
	val emailOTPExpiry: Any? = null,

	@field:SerializedName("OTP")
	val oTP: Int? = null,

	@field:SerializedName("CustomerID")
	val customerID: Any? = null,

	@field:SerializedName("URL")
	val uRL: Any? = null,

	@field:SerializedName("Type")
	val type: Int? = null,

	@field:SerializedName("Verified")
	val verified: Any? = null,

	@field:SerializedName("isOTPSent")
	val isOTPSent: Any? = null,

	@field:SerializedName("Attempt")
	val attempt: Any? = null,

	@field:SerializedName("Username")
	val username: Any? = null,

	@field:SerializedName("GeneratedDate")
	val generatedDate: Any? = null,

	@field:SerializedName("ID")
	val iD: Any? = null,

	@field:SerializedName("Detail")
	val detail: Any? = null,

	@field:SerializedName("Password")
	val password: Any? = null,

	@field:SerializedName("OTPExpiry")
	val oTPExpiry: Any? = null
)
