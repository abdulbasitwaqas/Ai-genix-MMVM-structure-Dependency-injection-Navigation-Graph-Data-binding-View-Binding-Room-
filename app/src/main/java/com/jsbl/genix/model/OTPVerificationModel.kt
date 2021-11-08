package com.jsbl.genix.model

import com.google.gson.annotations.SerializedName

data class OTPVerificationModel(

	@field:SerializedName("MobileNo")
	val mobileNo: String?
)
