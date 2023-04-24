package com.github.burachevsky.mqtthub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.viewModelContainer
import com.github.burachevsky.mqtthub.common.event.ToastMessage
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import javax.inject.Inject

class AppViewModel @Inject constructor(
    eventBus: EventBus,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    init {
        eventBus.apply {
            subscribe<ToastMessage>(viewModelScope, container::raiseEffect)
        }
    }
}