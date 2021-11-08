package com.jsbl.genix.model.profileManagement


import com.google.gson.annotations.SerializedName

data class DetailsResponse(
    @SerializedName("Message")
    var message: String? = "",
    @SerializedName("OTP")
    var oTP: String? = "",
    @SerializedName("Percentage")
    var percentage: Int? = 0,
    @SerializedName("Status")
    var status: Boolean?  = true,
    @SerializedName("StatusCode")
    var statusCode: Int?  = 0,
    @SerializedName("Token")
    var token: String? = ""
)