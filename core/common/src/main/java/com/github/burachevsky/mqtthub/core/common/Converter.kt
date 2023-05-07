package com.github.burachevsky.mqtthub.core.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converter {

    inline fun <reified T> fromJson(json: String): T {
        return Gson().fromJson(json, object : TypeToken<T>() {}.type)
    }

    inline fun <reified T> toJson(model: T): String {
        return Gson().toJson(model)
    }
}