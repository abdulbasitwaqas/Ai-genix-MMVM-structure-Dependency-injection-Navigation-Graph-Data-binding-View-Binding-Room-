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


class FeedbackTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun stringToPostFeedBack(data: String?): PostFeedBack? {
        if (data == null) {
            return null
        }
        try {
            return gson.fromJson(data, PostFeedBack::class.java)
        } catch (e: Exception) {
            return null
        }
    }

    @TypeConverter
    fun PostFeedBackToString(someObjects: PostFeedBack?): String? {
        if(someObjects==null){
            return null
        }
        return gson.toJson(someObjects)
    }

}