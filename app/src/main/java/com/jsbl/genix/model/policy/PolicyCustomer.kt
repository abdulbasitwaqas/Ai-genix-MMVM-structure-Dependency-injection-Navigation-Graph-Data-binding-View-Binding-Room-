package com.jsbl.genix.model.policy


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PolicyCustomer(
    @SerializedName("Contacts")
    var policyContacts: PolicyContacts? = null,
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("Number")
    var number: String? = "",
    @SerializedName("OverrideData")
    var overrideData: Boolean? = true
) : Parcelable