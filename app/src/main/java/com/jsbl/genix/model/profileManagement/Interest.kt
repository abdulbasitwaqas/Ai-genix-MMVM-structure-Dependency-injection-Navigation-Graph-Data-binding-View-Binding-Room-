package com.jsbl.genix.model.profileManagement


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray

@Parcelize
data class Interest(
    @SerializedName("CustomerID")
    var customerID: Long?=-1,
    @SerializedName("InterestID")
    var interestID: String?,
    @SerializedName("SubInterest")
    var subInterestIdList: String
):Parcelable