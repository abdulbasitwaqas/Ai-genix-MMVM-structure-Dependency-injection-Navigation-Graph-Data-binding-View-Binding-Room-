package com.jsbl.genix.model.redeem

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AvailableRedeemsModel(

	@field:SerializedName("AvailableRedeemsModel")
	val availableRedeemsModel: List<AvailableRedeemsModelItem?>? = null
)

data class AvailableRedeemsModelItem(

	@field:SerializedName("FilePath")
	val filePath: String? = null,

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("IsDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("ExpiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("WinningAmount")
	val winningAmount: Int? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("CreatedBy")
	val createdBy: Int? = null,

	@field:SerializedName("ReedeemName")
	val reedeemName: String? = null,

	@field:SerializedName("Title")
	val title: String? = null,

	@field:SerializedName("Duration")
	val duration: String? = null,

	@field:SerializedName("Point")
	val point: Int? = null,

	@field:SerializedName("Name")
	val name: Any? = null,

	@field:SerializedName("StartDate")
	val startDate: String? = null,

	@field:SerializedName("UpdatedBy")
	val updatedBy: Int? = null,

	@field:SerializedName("UpdatedOn")
	val updatedOn: String? = null,

	@field:SerializedName("TotalRecords")
	val totalRecords: Int? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("CreatedOn")
	val createdOn: String? = null
):Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		TODO("name"),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString()
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(filePath)
		parcel.writeValue(status)
		parcel.writeValue(isDeleted)
		parcel.writeString(expiryDate)
		parcel.writeValue(winningAmount)
		parcel.writeString(description)
		parcel.writeValue(createdBy)
		parcel.writeString(reedeemName)
		parcel.writeString(title)
		parcel.writeString(duration)
		parcel.writeValue(point)
		parcel.writeString(startDate)
		parcel.writeValue(updatedBy)
		parcel.writeString(updatedOn)
		parcel.writeValue(totalRecords)
		parcel.writeValue(iD)
		parcel.writeString(createdOn)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<AvailableRedeemsModelItem> {
		override fun createFromParcel(parcel: Parcel): AvailableRedeemsModelItem {
			return AvailableRedeemsModelItem(parcel)
		}

		override fun newArray(size: Int): Array<AvailableRedeemsModelItem?> {
			return arrayOfNulls(size)
		}
	}
}
