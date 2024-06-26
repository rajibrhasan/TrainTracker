package com.example.location.Util

import androidx.room.TypeConverter
import com.example.location.Model.Coordinate
import com.example.location.Model.Route
import com.example.location.Model.Step
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converter {
    var gson: Gson = Gson()

    @TypeConverter
    fun stringToRoute(data: String?): List<Route?>? {
        val listType = object : TypeToken<List<Route?>?>(){}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromRoute(value:List<Route>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun stringToCoordinate(data : String?):List<Coordinate?>?{
        val listType = object : TypeToken<List<Coordinate?>?>(){}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromCoordinate(value:List<Coordinate>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun stringToStep(data: String?): List<Step?>? {

        val listType = object : TypeToken<List<Route?>?>(){}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromStep(value:List<Step>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun stringToList(data: String?): List<String?>? {

        val listType = object : TypeToken<List<String?>?>(){}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromList(value:List<String>?): String? {
        return gson.toJson(value)
    }
}
