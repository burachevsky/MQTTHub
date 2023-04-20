package com.github.burachevsky.mqtthub.feature.publishtext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.feature.publishtext.PublishTextDialogFragmentArgs
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PublishTextViewModel @Inject constructor(
    private val eventBus: EventBus,
    args: PublishTextDialogFragmentArgs
) : ViewModel(), VM<Navigator> {

    val tileName = args.tileName

    override val container = ViewModelContainer<Navigator>(viewModelScope)

    private val tileId = args.tileId

    fun sendResult(text: String) {
        container.launch(Dispatchers.Main) {
            eventBus.send(PublishTextEntered(tileId, text))

            container.navigator {
                back()
            }
        }
    }
}