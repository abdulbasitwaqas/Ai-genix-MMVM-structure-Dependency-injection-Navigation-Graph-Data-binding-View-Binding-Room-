package com.jsbl.genix.model.registration


import com.google.gson.annotations.SerializedName

data class ResponseUploadImage(
    @SerializedName("Detail")
    var detail: Any?,
    @SerializedName("isOTPSent")
    var isOTPSent: Boolean? = false,
    @SerializedName("Status")
    var status: Boolean? = false,
    @SerializedName("URL")
    var uRL: String? = ""
)