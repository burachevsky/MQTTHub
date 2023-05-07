package com.github.burachevsky.mqtthub.core.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.Boolean
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias SharedPreferencesGetter <T> = (SharedPreferences) -> T
typealias SharedPreferencesSetter <T> = (SharedPreferences, value: T) -> Unit

class SharedPreferencesProperty<T>(
    key: String,
    defaultValue: T?
) : ReadWriteProperty<SharedPreferencesHolder, T> {

    private val getter: SharedPreferencesGetter<T> = createPreferencesGetter(key, defaultValue)
    private val setter: SharedPreferencesSetter<T> = createPreferencesSetter(key, defaultValue)

    override fun getValue(thisRef: SharedPreferencesHolder, property: KProperty<*>): T {
        return getter.invoke(thisRef.sharedPreferences)
    }

    override fun setValue(thisRef: SharedPreferencesHolder, property: KProperty<*>, value: T) {
        return setter.invoke(thisRef.sharedPreferences, value)
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T> createPreferencesGetter(key: String, defaultValue: T?): SharedPreferencesGetter<T> {
    when (defaultValue) {
        is String -> return { it.getString(key, defaultValue) as T }
        is Int -> return { it.getInt(key, defaultValue) as T }
        is Long -> return { it.getLong(key, defaultValue) as T }
        is Float -> return { it.getFloat(key, defaultValue) as T }
        is Boolean -> return { it.getBoolean(key, defaultValue) as T }
        is Set<*> -> return { it.getStringSet(key, defaultValue as Set<String>) as T }
        else -> throw IllegalStateException()
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T> createPreferencesSetter(
    key: String,
    defaultValue: T?
): SharedPreferencesSetter<T> {
    when (defaultValue) {
        is String -> return { prefs, value -> prefs.edit { putString(key, value as String) } }
        is Int -> return { prefs, value -> prefs.edit { putInt(key, value as Int) } }
        is Long -> return { prefs, value -> prefs.edit { putLong(key, value as Long) } }
        is Float -> return { prefs, value -> prefs.edit { putFloat(key, value as Float) } }
        is Boolean -> return { prefs, value -> prefs.edit { putBoolean(key, value as Boolean) } }
        is Set<*> -> return { prefs, value ->
            prefs.edit { putStringSet(key, value as Set<String>) }
        }

        else -> throw IllegalStateException()
    }
}