package com.jsbl.genix.model.typeConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.model.profileManagement.*
import java.lang.Exception
import java.lang.reflect.Type
import java.util.*


class DropDownTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun stringToColor(data: String?): List<Color> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<Color?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }

    @TypeConverter
    fun ColorListToString(someObjects: List<Color?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }




// for car model
    @TypeConverter
    fun stringToCarModelss(data: String?): List<CarModelss> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<CarModelss?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }
    @TypeConverter
    fun carModelssToString(someObjects: List<CarModelss?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }



// for purpose
    @TypeConverter
    fun stringToPurpose(data: String?): List<Purpose> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<Purpose?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }


        @TypeConverter
    fun purposeToString(someObjects: List<Purpose?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }




// for purpose
    @TypeConverter
    fun stringToInterestSubInterest(data: String?): List<InterestSubInterest> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<InterestSubInterest?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }


        @TypeConverter
    fun interestSubInterestToString(someObjects: List<InterestSubInterest?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }





    // for SubManufacturerModel
    @TypeConverter
    fun stringToSubManufacturerModel(data: String?): List<SubManufacturerModel> {
        if (data == null) {
            return Collections.emptyList()
        }
        try {
            val listType: Type =
                object : TypeToken<List<SubManufacturerModel?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }

    @TypeConverter
    fun subManufacturerModelToString(someObjects: List<SubManufacturerModel?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }












    @TypeConverter
    fun stringToDeliveryMethod(data: String?): List<DeliveryMethod> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<DeliveryMethod?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }

    @TypeConverter
    fun DeliveryMethodListToString(someObjects: List<DeliveryMethod?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }




    @TypeConverter
    fun stringToDeviceType(data: String?): List<DeviceType> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<DeviceType?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }

    @TypeConverter
    fun DeviceTypeListToString(someObjects: List<DeviceType?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToMaker(data: String?): List<Maker> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<Maker?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }

    @TypeConverter
    fun MakerListToString(someObjects: List<Maker?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }


    @TypeConverter
    fun stringToManufacturer(data: String?): List<Manufacturer> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<Manufacturer?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }

    @TypeConverter
    fun ManufacturerListToString(someObjects: List<Manufacturer?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }



    @TypeConverter
    fun stringToMotorType(data: String?): List<MotorType> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<MotorType?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }

    @TypeConverter
    fun MotorTypeListToString(someObjects: List<MotorType?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }


    @TypeConverter
    fun stringToNotInsuredReason(data: String?): List<NotInsuredReason> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<NotInsuredReason?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }

    @TypeConverter
    fun NotInsuredReasonListToString(someObjects: List<NotInsuredReason?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }


}