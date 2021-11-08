package com.jsbl.genix.model.policy


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PolicyRequest(
    @SerializedName("Customer")
    var policyCustomer: PolicyCustomer?= null,
    @SerializedName("DeliveryMethod")
    var deliveryMethod: String? ="",
    @SerializedName("ExpirationDate")
    var expirationDate: String? = "",
    @SerializedName("Number")
    var number: String? = "",
    @SerializedName("registerPolicyRequest")
    var registerPolicyRequest: RegisterPolicyRequest? = null,
    @SerializedName("RequestedDeviceType")
    var requestedDeviceType: String? = "",
    @SerializedName("SignDate")
    var signDate: String? = "",
    @SerializedName("StartDate")
    var startDate: String? = "",
    @SerializedName("Vehicle")
    var policyVehicle: PolicyVehicle? = null
) : Parcelable