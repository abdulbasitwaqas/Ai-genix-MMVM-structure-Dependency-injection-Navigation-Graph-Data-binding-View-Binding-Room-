package com.jsbl.genix.model.registration


import com.google.gson.annotations.SerializedName

data class RequestResetPassword(
    @SerializedName("newPassword")
    var newPassword: String?,
    @SerializedName("confirmPassword")
    var confirmPassword: String?,
    @SerializedName("Mobile")
    var Mobile: String?
)