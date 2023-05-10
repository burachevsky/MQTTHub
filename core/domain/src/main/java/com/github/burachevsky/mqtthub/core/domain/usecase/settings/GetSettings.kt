package com.github.burachevsky.mqtthub.core.domain.usecase.settings

import com.github.burachevsky.mqtthub.core.data.SettingsImpl
import com.github.burachevsky.mqtthub.core.model.Settings
import javax.inject.Inject

class GetSettings @Inject constructor(
    private val settingsImpl: SettingsImpl
) {

    operator fun invoke(): Settings {
        return settingsImpl
    }
}