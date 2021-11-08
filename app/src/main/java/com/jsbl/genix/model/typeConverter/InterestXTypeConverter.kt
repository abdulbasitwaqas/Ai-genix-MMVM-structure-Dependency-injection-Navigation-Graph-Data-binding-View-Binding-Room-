package com.jsbl.genix.model.typeConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsbl.genix.model.profileManagement.InterestX
import com.jsbl.genix.model.profileManagement.PostCarDetail
import com.jsbl.genix.model.profileManagement.PostFeedBack
import java.lang.Exception
import java.lang.reflect.Type
import java.util.*


class InterestXTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun stringToInterestX(data: String?): List<InterestX> {
        if (data == null) {
            return Collections.emptyList()
        }

        try {
            val listType: Type =
                object : TypeToken<List<InterestX?>?>() {}.getType()
            return gson.fromJson(data, listType)
        } catch (e: Exception) {
            return Collections.emptyList()
        }
    }

    @TypeConverter
    fun InterestXListToString(someObjects: List<InterestX?>?): String? {
        if(someObjects==null){
            return null
        }
        return gson.toJson(someObjects)
    }


}