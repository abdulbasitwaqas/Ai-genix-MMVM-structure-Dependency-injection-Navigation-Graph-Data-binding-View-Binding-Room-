package com.jsbl.genix.model.profileManagement

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetCustomerInterestModel(
	val interestID: String,
	val customerID: Long?
) : Parcelable

