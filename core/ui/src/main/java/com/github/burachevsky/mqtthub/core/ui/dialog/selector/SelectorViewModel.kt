package com.github.burachevsky.mqtthub.core.ui.dialog.selector

import androidx.lifecycle.ViewModel
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.event.ItemSelected
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SelectorViewModel @Inject constructor(
    config: SelectorConfig,
    private val eventBus: EventBus,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    val title: Txt? = config.title

    val items: List<SelectorItem> = config.items

    fun tileTypeClicked(position: Int) {
        container.run {
            launch(Dispatchers.Main) {
                eventBus.send(ItemSelected(items[position].id))
                navigator { back() }
            }
        }
    }
}