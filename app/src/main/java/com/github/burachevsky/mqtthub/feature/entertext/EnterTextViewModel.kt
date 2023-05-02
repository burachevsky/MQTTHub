package com.github.burachevsky.mqtthub.feature.entertext

import androidx.lifecycle.ViewModel
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.viewModelContainer
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class EnterTextViewModel @Inject constructor(
    private val eventBus: EventBus,
    args: EnterTextDialogFragmentArgs
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    val title = args.title
    val initText = args.initText
    private val actionId = args.actionId

    fun sendResult(text: String) {
        container.launch(Dispatchers.Main) {
            eventBus.send(TextEntered(actionId, text))

            container.navigator {
                back()
            }
        }
    }
}