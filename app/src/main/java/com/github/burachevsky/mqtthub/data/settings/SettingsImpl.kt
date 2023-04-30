package com.github.burachevsky.mqtthub.data.settings

import android.content.Context
import com.github.burachevsky.mqtthub.data.APP_PREFS_NAME
import com.github.burachevsky.mqtthub.data.prefs.SharedPreferencesHolder
import com.github.burachevsky.mqtthub.data.prefs.prefs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsImpl @Inject constructor(
    context: Context
) : Settings, SharedPreferencesHolder {

    override val sharedPreferences = context
        .getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE)

    override var dynamicColorsEnabled by prefs(KEY_DYNAMIC_COLORS_ENABLED, true)

    override var theme by prefs(KEY_THEME, Theme.SYSTEM)

    companion object {
        private const val KEY_DYNAMIC_COLORS_ENABLED = "dynamic_colors_enabled"
        private const val KEY_THEME = "theme"
    }
}