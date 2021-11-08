package com.jsbl.genix.model.profileManagement


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jsbl.genix.model.policy.PolicyRequest
import com.jsbl.genix.views.fragments.CarDetails.Companion.UNCHECKED
import kotlinx.android.parcel.Parcelize

@Parcelize

data class PostCarDetail(
    @SerializedName("ChasisNo")
    var chasisNo: String? = null,
    @SerializedName("ColorID")
    var colorID: Long? = 0,
    @SerializedName("ID")
    var iD: Long? = 0,
    @SerializedName("CustomerID")
    var customerID: Long? = null,
    @SerializedName("Deductible")
    var deductible: String? = null,
    @SerializedName("EngineNo")
    var engineNo: String? = null,
    @SerializedName("InsuranceCompany")
    var insuranceCompany: String? = null,
    @SerializedName("InsurancePremium")
    var insurancePremium: String? = null,
    @SerializedName("Insured")
    var insured: Boolean = false,
    @SerializedName("CarManufacturerIcon")
    var CarManufacturerIcon: String? = null,
    @SerializedName("CarColorPath")
    var CarColorPath: String? = null,
    @SerializedName("isDefaultCar")
    var isDefaultCar: Boolean =false,
    @SerializedName("MakeID")
    var makeID: Long? = null,
    @SerializedName("ModelID")
    var modelID: Long? = null,
    @SerializedName("PurposeID")
    var purposeID: Long? = null,
    @SerializedName("ManufacturerID")
    var manufacturerID: Long? = 0,
    @SerializedName("NotInsuredReasonID")
    var notInsuredReasonID: Long? = 0,
    @SerializedName("ReasonForCurrentInsurer")
    var reasonForCurrentInsurer: String? = null,
    @SerializedName("RegistrationNo")
    var registrationNo: String? = null,
    @SerializedName("YearOfInitailRegistration")
    var registrationYear: String? = null,
    @SerializedName("RenewalDate")
    var renewalDate: String? = null,
    @SerializedName("ThingsThatCanBeImproved")
    var thingsThatCanBeImproved: String? = null,
    @SerializedName("VIN")
    var policyVin: String? = null,
    @SerializedName("PolicyNumber")
    var policyNumber: String? = null,
    @SerializedName("DeviceType")
    var policyDeviceType: String? = "",
    @SerializedName("DeliveryMethod")
    var policyDeliveryMethod: String? = "",
    @SerializedName("MotorType")
    var policyMotorType: Long? = 0,
    @SerializedName("scopeToken")
    var scopeToken: String? = "",
    @SerializedName("Flag")
    var flag: String? = "",
    @SerializedName("InsFlag")
    var InsFlag: String? = "",
    @SerializedName("policyRequest")
    var policyRequest: PolicyRequest? = null,
    @SerializedName("isDefault")
    @Expose(serialize = false, deserialize = false)
    var isDefault : Boolean = false
) : Parcelable