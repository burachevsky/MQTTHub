package com.github.burachevsky.mqtthub.core.data.settings

import android.content.Context
import android.content.SharedPreferences
import com.github.burachevsky.mqtthub.core.preferences.APP_PREFS_NAME
import com.github.burachevsky.mqtthub.core.preferences.SharedPreferencesHolder
import com.github.burachevsky.mqtthub.core.preferences.prefs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsImpl @Inject constructor(
    context: Context
) : Settings, SharedPreferencesHolder {

    override val sharedPreferences: SharedPreferences = context
        .getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE)

    override var dynamicColorsEnabled by prefs(KEY_DYNAMIC_COLORS_ENABLED, true)

    override var theme by prefs(KEY_THEME, Theme.SYSTEM)

    companion object {
        private const val KEY_DYNAMIC_COLORS_ENABLED = "dynamic_colors_enabled"
        private const val KEY_THEME = "theme"
    }
}