package com.github.burachevsky.mqtthub.core.preferences

import kotlin.properties.ReadWriteProperty

inline fun <reified T> prefs(
    key: String,
    defaultValue: T? = null
): ReadWriteProperty<SharedPreferencesHolder, T> {
    return SharedPreferencesProperty(key, defaultValue)
}