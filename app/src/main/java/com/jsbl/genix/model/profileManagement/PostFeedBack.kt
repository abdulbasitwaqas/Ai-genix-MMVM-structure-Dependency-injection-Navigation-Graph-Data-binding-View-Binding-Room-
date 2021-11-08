package com.jsbl.genix.model.profileManagement


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.jsbl.genix.model.UserFeedbackQuestionModel
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PostFeedBack(
    @SerializedName("UserID")
    var UserID: Long?,
    @SerializedName("Remarks")
    var remarks: String?,
    @SerializedName("UserFeedbackQuestion")
    var userFeedbackQuestion: ArrayList<UserFeedbackQuestionModel>
):Parcelable