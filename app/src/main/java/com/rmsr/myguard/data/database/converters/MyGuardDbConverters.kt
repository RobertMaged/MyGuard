package com.rmsr.myguard.data.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class MyGuardDbConverters {
    private val gson = Gson()
    private val listOfStringTypeToken = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromListToString(correctedData: List<String>): String {
        return gson.toJson(correctedData)
    }

    @TypeConverter
    fun fromStringToList(correctedData: String): List<String> {
        return gson.fromJson(correctedData, listOfStringTypeToken)
    }

    //    @TypeConverter
//    fun fromListToString(data: List<String>): String {
//        return Json.encodeToString(data)
//    }
//
//    @TypeConverter
//    fun fromStringToList(data: String): List<String> {
//        return Json.decodeFromString(data)
//    }

}