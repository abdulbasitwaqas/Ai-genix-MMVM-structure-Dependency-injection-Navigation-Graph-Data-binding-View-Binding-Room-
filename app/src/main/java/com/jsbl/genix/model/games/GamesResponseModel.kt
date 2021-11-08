package com.jsbl.genix.model.games

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class GamesResponseModel(

        @field:SerializedName("GameListInActive")
        val gameListInActive: ArrayList<GameListInActiveItem?>? = null,

        @field:SerializedName("GameListAvailable")
        val gameListAvailable: ArrayList<GameListAvailableItem?>? = null,

        @field:SerializedName("GameListActive")
        val gameListActive: ArrayList<GameListActiveItem?>? = null,

        @field:SerializedName("standup")
        val standup: Standup? = null
)

data class GameListInActiveItem(

        @field:SerializedName("FilePath")
        val filePath: String? = null,

        @field:SerializedName("Status")
        val status: Boolean? = null,

        @field:SerializedName("IsDeleted")
        val isDeleted: Boolean? = null,

        @field:SerializedName("WinningAmount")
        val winningAmount: Int? = null,

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("CreatedBy")
        val createdBy: Int? = null,

        @field:SerializedName("Title")
        val title: String? = null,

        @field:SerializedName("Flag")
        val flag: Any? = null,

        @field:SerializedName("EndDate")
        val endDate: Any? = null,

        @field:SerializedName("StartDate")
        val startDate: String? = null,

        @field:SerializedName("UserID")
        val userID: Int? = null,

        @field:SerializedName("Games")
        val games: Any? = null,

        @field:SerializedName("TotalRecords")
        val totalRecords: Int? = null,

        @field:SerializedName("ID")
        val iD: Int? = null,

        @field:SerializedName("GameID")
        val gameID: Int? = null
) : FaltuInterface, Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
                parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                TODO("flag"),
                TODO("endDate"),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                TODO("games"),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(filePath)
                parcel.writeValue(status)
                parcel.writeValue(isDeleted)
                parcel.writeValue(winningAmount)
                parcel.writeString(description)
                parcel.writeValue(createdBy)
                parcel.writeString(title)
                parcel.writeString(startDate)
                parcel.writeValue(userID)
                parcel.writeValue(totalRecords)
                parcel.writeValue(iD)
                parcel.writeValue(gameID)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<GameListInActiveItem> {
                override fun createFromParcel(parcel: Parcel): GameListInActiveItem {
                        return GameListInActiveItem(parcel)
                }

                override fun newArray(size: Int): Array<GameListInActiveItem?> {
                        return arrayOfNulls(size)
                }
        }
}

data class GameListAvailableItem(

        @field:SerializedName("CategoryID")
        val categoryID: Int? = null,

        @field:SerializedName("FilePath")
        val filePath: String? = null,

        @field:SerializedName("Status")
        val status: Boolean? = null,

        @field:SerializedName("AverageSpeed")
        val averageSpeed: Int? = null,

        @field:SerializedName("IsDeleted")
        val isDeleted: Boolean? = null,

        @field:SerializedName("ExpiryDate")
        val expiryDate: Any? = null,

        @field:SerializedName("WinningAmount")
        val winningAmount: Int? = null,

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("CreatedBy")
        val createdBy: Int? = null,

        @field:SerializedName("KmDriven")
        val kmDriven: Int? = null,

        @field:SerializedName("Title")
        val title: String? = null,

        @field:SerializedName("ReedemStatus")
        val reedemStatus: Boolean? = null,

        @field:SerializedName("TotalRecords")
        val totalRecords: Int? = null,

        @field:SerializedName("ID")
        val iD: Int? = null,

        @field:SerializedName("CreatedOn")
        val createdOn: String? = null,

        @field:SerializedName("GameCategory")
        val gameCategory: Any? = null
) : FaltuInterface, Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
                TODO("expiryDate"),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                TODO("gameCategory")) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(categoryID)
                parcel.writeString(filePath)
                parcel.writeValue(status)
                parcel.writeValue(averageSpeed)
                parcel.writeValue(isDeleted)
                parcel.writeValue(winningAmount)
                parcel.writeString(description)
                parcel.writeValue(createdBy)
                parcel.writeValue(kmDriven)
                parcel.writeString(title)
                parcel.writeValue(reedemStatus)
                parcel.writeValue(totalRecords)
                parcel.writeValue(iD)
                parcel.writeString(createdOn)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<GameListAvailableItem> {
                override fun createFromParcel(parcel: Parcel): GameListAvailableItem {
                        return GameListAvailableItem(parcel)
                }

                override fun newArray(size: Int): Array<GameListAvailableItem?> {
                        return arrayOfNulls(size)
                }
        }
}

data class Standup(

        @field:SerializedName("FilePath")
        val filePath: String? = null,

        @field:SerializedName("Category")
        val category: String? = null,

        @field:SerializedName("TotalRecords")
        val totalRecords: Int? = null,

        @field:SerializedName("WinningPoint")
        val winningPoint: Int? = null,

        @field:SerializedName("ID")
        val iD: Int? = null
)

data class GameListActiveItem(

        @field:SerializedName("FilePath")
        val filePath: String? = null,

        @field:SerializedName("Status")
        val status: Boolean? = null,

        @field:SerializedName("IsDeleted")
        val isDeleted: Boolean? = null,

        @field:SerializedName("WinningAmount")
        val winningAmount: Int? = null,

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("CreatedBy")
        val createdBy: Int? = null,

        @field:SerializedName("Title")
        val title: String? = null,

        @field:SerializedName("Flag")
        val flag: Any? = null,

        @field:SerializedName("EndDate")
        val endDate: Any? = null,

        @field:SerializedName("StartDate")
        val startDate: String? = null,

        @field:SerializedName("UserID")
        val userID: Int? = null,

        @field:SerializedName("Games")
        val games: Any? = null,

        @field:SerializedName("TotalRecords")
        val totalRecords: Int? = null,

        @field:SerializedName("ID")
        val iD: Int? = null,

        @field:SerializedName("GameID")
        val gameID: Int? = null
) : FaltuInterface,Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
                parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                TODO("flag"),
                TODO("endDate"),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                TODO("games"),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(filePath)
                parcel.writeValue(status)
                parcel.writeValue(isDeleted)
                parcel.writeValue(winningAmount)
                parcel.writeString(description)
                parcel.writeValue(createdBy)
                parcel.writeString(title)
                parcel.writeString(startDate)
                parcel.writeValue(userID)
                parcel.writeValue(totalRecords)
                parcel.writeValue(iD)
                parcel.writeValue(gameID)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<GameListActiveItem> {
                override fun createFromParcel(parcel: Parcel): GameListActiveItem {
                        return GameListActiveItem(parcel)
                }

                override fun newArray(size: Int): Array<GameListActiveItem?> {
                        return arrayOfNulls(size)
                }
        }
}
