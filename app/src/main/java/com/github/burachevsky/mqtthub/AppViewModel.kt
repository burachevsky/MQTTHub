package com.github.burachevsky.mqtthub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.effect.ToastMessage
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.domain.usecase.currentids.InitCurrentIds
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class AppViewModel @Inject constructor(
    eventBus: EventBus,
    initCurrentIds: InitCurrentIds,
) : ViewModel(), VM<Navigator> {

    override val container = ViewModelContainer<Navigator>(viewModelScope)

    init {
        container.launch(Dispatchers.Main) {
            initCurrentIds()
        }

        eventBus.apply {
            subscribe<ToastMessage>(viewModelScope, container::raiseEffect)
        }
    }
}