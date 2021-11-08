package com.jsbl.genix.model.typeConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.model.profileManagement.Interest
import com.jsbl.genix.model.profileManagement.InterestX
import com.jsbl.genix.model.profileManagement.PostCarDetail
import com.jsbl.genix.model.profileManagement.PostFeedBack
import java.lang.Exception
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class CarDetailsTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun stringToPostCarDetails(data: String?): ArrayList<PostCarDetail>? {
        if (data == null) {
            return arrayListOf()
        }

        return try {
            val listType: Type =
                object : TypeToken<List<PostCarDetail?>?>() {}.getType()
            gson.fromJson(data, listType)
        } catch (e: Exception) {
            return arrayListOf()

        }
    }

    @TypeConverter
    fun PostCarDetailsToString(someObjects: List<PostCarDetail>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }
}