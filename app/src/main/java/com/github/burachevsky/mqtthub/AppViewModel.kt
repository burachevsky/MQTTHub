package com.github.burachevsky.mqtthub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.core.data.settings.Settings
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.event.StartNewBrokerConnection
import com.github.burachevsky.mqtthub.core.ui.event.SwitchTheme
import com.github.burachevsky.mqtthub.core.ui.event.ToastMessage
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import javax.inject.Inject

class AppViewModel @Inject constructor(
    eventBus: com.github.burachevsky.mqtthub.core.eventbus.EventBus,
    private val settings: Settings,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    var themeIsInitialized = false

    init {
        eventBus.apply {
            subscribe<ToastMessage>(viewModelScope, container::raiseEffect)
            subscribe<SwitchTheme>(viewModelScope, container::raiseEffect)
            subscribe<StartNewBrokerConnection>(viewModelScope, container::raiseEffect)
        }
    }

    fun dynamicColorsEnabled(): Boolean {
        return settings.dynamicColorsEnabled
    }

    fun getTheme(): Int {
        return settings.theme
    }
}