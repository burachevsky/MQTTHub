package com.github.burachevsky.mqtthub.core.ui.dialog.entertext

import androidx.lifecycle.ViewModel
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class EnterTextViewModel @Inject constructor(
    private val eventBus: EventBus,
    config: EnterTextConfig,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    val title = config.title
    val initText = config.initText
    private val actionId = config.actionId

    fun sendResult(text: String) {
        container.launch(Dispatchers.Main) {
            eventBus.send(TextEntered(actionId, text))

            container.navigator {
                back()
            }
        }
    }
}