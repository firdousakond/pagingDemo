package com.gituser.paging.data.room

import androidx.room.TypeConverter
import com.gituser.paging.data.model.UserData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {

    @TypeConverter
    fun toUserData(userStr: String?): UserData {
        val type = object : TypeToken<UserData>() {}.type
        return Gson().fromJson(userStr, type)
    }

    @TypeConverter
    fun fromUserData(user: UserData?): String {
        val type = object : TypeToken<UserData>() {}.type
        return Gson().toJson(user, type)
    }
}