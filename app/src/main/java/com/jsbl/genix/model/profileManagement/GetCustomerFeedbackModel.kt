package com.jsbl.genix.model.profileManagement

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.jsbl.genix.model.UserFeedbackQuestionModel
import kotlinx.android.parcel.Parcelize


@Parcelize
data class GetCustomerFeedbackModel(
    @SerializedName("UserID")
    var UserID: Long?
): Parcelable