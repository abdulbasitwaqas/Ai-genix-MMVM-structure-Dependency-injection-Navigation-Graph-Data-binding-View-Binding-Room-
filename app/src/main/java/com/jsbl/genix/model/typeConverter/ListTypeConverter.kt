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


class ListTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun <T>stringToObjList(data: String?,CLAZZ: Class<T>): List<T>? {
        if (data == null) {
            return Collections.emptyList()
        }

        return try {
            val listType: Type =
                object : TypeToken<List<T?>?>() {}.getType()
            gson.fromJson(data, listType)
        } catch (e: Exception) {
            Collections.emptyList()
        }
    }

    @TypeConverter
    fun <T>ObjListToString(someObjects: List<T?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }


}