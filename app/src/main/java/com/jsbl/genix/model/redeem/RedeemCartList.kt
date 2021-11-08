package com.jsbl.genix.model.redeem

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RedeemCartList(

	@field:SerializedName("RedeemCartList")
	val redeemCartList: List<RedeemCartListItem?>? = null
)

data class RedeemCartListItem(

	@field:SerializedName("FilePath")
	val filePath: String? = null,

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("ExpiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("IsDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("Description")
	val description: Any? = null,

	@field:SerializedName("ReedeemName")
	val reedeemName: String? = null,

	@field:SerializedName("Title")
	val title: Any? = null,

	@field:SerializedName("Point")
	val point: Int? = null,

	@field:SerializedName("Count")
	val count: Int? = null,

	@field:SerializedName("ReedeemID")
	val reedeemID: Int? = null,

	@field:SerializedName("Flag")
	val flag: Any? = null,

	@field:SerializedName("responseCode")
	val responseCode: String? = null,

	@field:SerializedName("ReedeemCount")
	var reedeemCount: Int? = null,

	@field:SerializedName("ReedeemPoints")
	var reedeemPoints: Int? = null,

	@field:SerializedName("UserID")
	val userID: Int? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("responseMessage")
	val responseMessage: Any? = null
):Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		parcel.readString(),
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		TODO("description"),
		parcel.readString(),
		TODO("title"),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		TODO("flag"),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		TODO("reedeemPoints"),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		TODO("responseMessage")
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(filePath)
		parcel.writeValue(status)
		parcel.writeString(expiryDate)
		parcel.writeValue(isDeleted)
		parcel.writeString(reedeemName)
		parcel.writeValue(point)
		parcel.writeValue(count)
		parcel.writeValue(reedeemID)
		parcel.writeString(responseCode)
		parcel.writeValue(reedeemCount)
		parcel.writeValue(userID)
		parcel.writeValue(iD)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<RedeemCartListItem> {
		override fun createFromParcel(parcel: Parcel): RedeemCartListItem {
			return RedeemCartListItem(parcel)
		}

		override fun newArray(size: Int): Array<RedeemCartListItem?> {
			return arrayOfNulls(size)
		}
	}
}
