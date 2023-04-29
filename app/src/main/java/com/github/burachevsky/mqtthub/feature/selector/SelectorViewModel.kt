package com.github.burachevsky.mqtthub.feature.selector

import androidx.lifecycle.ViewModel
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.viewModelContainer
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.text.Txt
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