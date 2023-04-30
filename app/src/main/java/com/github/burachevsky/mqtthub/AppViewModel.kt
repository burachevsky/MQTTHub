package com.github.burachevsky.mqtthub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.viewModelContainer
import com.github.burachevsky.mqtthub.common.event.SwitchTheme
import com.github.burachevsky.mqtthub.common.event.ToastMessage
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.data.settings.Settings
import javax.inject.Inject

class AppViewModel @Inject constructor(
    eventBus: EventBus,
    private val settings: Settings,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    var themeIsInitialized = false

    init {
        eventBus.apply {
            subscribe<ToastMessage>(viewModelScope, container::raiseEffect)
            subscribe<SwitchTheme>(viewModelScope, container::raiseEffect)
        }
    }

    fun dynamicColorsEnabled(): Boolean {
        return settings.dynamicColorsEnabled
    }

    fun getTheme(): Int {
        return settings.theme
    }
}