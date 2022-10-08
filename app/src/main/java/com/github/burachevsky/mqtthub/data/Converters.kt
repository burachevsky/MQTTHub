package com.github.burachevsky.mqtthub.data

import androidx.room.TypeConverter
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {

    @TypeConverter
    fun fromStateList(list: List<Tile.State>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toStateList(json: String): List<Tile.State> {
        return Gson().fromJson(json, object : TypeToken<ArrayList<Tile.State>>() {}.type)
    }
}