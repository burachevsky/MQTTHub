package com.github.burachevsky.mqtthub.feature.brokers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.core.domain.usecase.broker.DeleteBroker
import com.github.burachevsky.mqtthub.core.domain.usecase.broker.ObserveBrokers
import com.github.burachevsky.mqtthub.core.domain.usecase.currentids.UpdateCurrentBroker
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.event.AlertDialog
import com.github.burachevsky.mqtthub.core.ui.ext.get
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.text.withArgs
import com.github.burachevsky.mqtthub.feature.brokers.item.BrokerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class BrokersViewModel @Inject constructor(
    private val deleteBroker: DeleteBroker,
    private val updateCurrentBroker: UpdateCurrentBroker,
    observeBrokers: ObserveBrokers,
) : ViewModel(), VM<BrokersNavigator> {

    override val container = viewModelContainer()

    val items: StateFlow<List<ListItem>> = observeBrokers()
        .map { it.map(::BrokerItem) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val noBrokersYet: StateFlow<Boolean> = items
        .map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun brokerClicked(position: Int) {
        container.launch(Dispatchers.Default) {
            updateCurrentBroker(items.get<BrokerItem>(position).broker.id)
            container.navigator {
                back()
            }
        }
    }

    fun addBrokerClicked() {
        container.navigator {
            navigateAddBroker()
        }
    }

    fun editBrokerClicked(position: Int) {
        container.navigator {
            navigateAddBroker(items.get<BrokerItem>(position).broker.id)
        }
    }

    fun deleteBrokerClicked(position: Int) {
        val brokerInfo = items.get<BrokerItem>(position).broker

        container.raiseEffect {
            AlertDialog(
                title = Txt.of(R.string.remove_dialog_title)
                    .withArgs(brokerInfo.name),
                message = Txt.of(R.string.remove_dialog_message),
                yes = AlertDialog.Button(Txt.of(R.string.remove_dialog_yes)) {
                    removeBroker(position)
                },
                no = AlertDialog.Button(Txt.of(R.string.remove_dialog_cancel))
            )
        }
    }

    private fun removeBroker(position: Int) {
        container.launch(Dispatchers.Default) {
            deleteBroker(items.get<BrokerItem>(position).broker.id)
        }
    }
}
