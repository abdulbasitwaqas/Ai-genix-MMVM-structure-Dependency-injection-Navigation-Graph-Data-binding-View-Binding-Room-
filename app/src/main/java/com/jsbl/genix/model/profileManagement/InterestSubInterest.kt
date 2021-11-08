package com.jsbl.genix.model.profileManagement

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class InterestSubInterest(
    @SerializedName("InterestID")
    var interestID: Int,
    @SerializedName("InterestTitle")
    var interestTitle: String? = "",
    @SerializedName("InterestFilePath")
    var InterestFilePath: String? = "",
    @SerializedName("subInterest")
    var subInterestList:List<SubInterestList>,

    var isSelected: Boolean = false
) : Parcelable {
    override fun toString(): String {
        return interestTitle.toString()
    }
}