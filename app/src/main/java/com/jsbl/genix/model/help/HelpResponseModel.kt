package com.jsbl.genix.model.help

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class HelpResponseModel(

	@field:SerializedName("HelpResponseModel")
	val helpResponseModel: List<HelpResponseModelItem?>? = null
)

data class HelpResponseModelItem(

	@field:SerializedName("Status")
	val status: Any? = null,

	@field:SerializedName("Answer")
	val answer: String? = null,

	@field:SerializedName("ImagePath")
	val imagePath: String? = null,

	@field:SerializedName("CreatedDate")
	val createdDate: String? = null,

	@field:SerializedName("TotalRecords")
	val totalRecords: Any? = null,

	@field:SerializedName("Question")
	val question: String? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("Deleted")
	val deleted: Boolean? = null,

	@field:SerializedName("Step")
	val step: Any? = null
): Parcelable {
	constructor(parcel: Parcel) : this(
		TODO("status"),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		TODO("totalRecords"),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		TODO("step")
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(answer)
		parcel.writeString(imagePath)
		parcel.writeString(createdDate)
		parcel.writeString(question)
		parcel.writeValue(iD)
		parcel.writeValue(deleted)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<HelpResponseModelItem> {
		override fun createFromParcel(parcel: Parcel): HelpResponseModelItem {
			return HelpResponseModelItem(parcel)
		}

		override fun newArray(size: Int): Array<HelpResponseModelItem?> {
			return arrayOfNulls(size)
		}
	}
}
