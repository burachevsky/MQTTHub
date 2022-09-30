package com.github.burachevsky.mqtthub.feature.brokers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.effect.AlertDialog
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.feature.brokers.item.BrokerItem
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.text.withArgs
import com.github.burachevsky.mqtthub.domain.usecase.broker.DeleteBroker
import com.github.burachevsky.mqtthub.domain.usecase.broker.GetBrokers
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerAdded
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerInfo
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerUpdated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BrokersViewModel @Inject constructor(
    private val getBrokers: GetBrokers,
    private val deleteBroker: DeleteBroker,
    eventBus: EventBus,
) : ViewModel() {

    val container = ViewModelContainer<BrokersNavigator>(viewModelScope)

    private val _items: MutableStateFlow<List<ListItem>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    val noBrokersYet: Flow<Boolean> = items.map { it.isEmpty() }

    init {
        eventBus.apply{
            subscribe<BrokerAdded>(viewModelScope) {
                addBrokerToList(it.brokerInfo)
            }

            eventBus.subscribe<BrokerUpdated>(viewModelScope) {
                updateBrokerInList(it.brokerInfo)
            }
        }

        container.launch(Dispatchers.Main) {
            _items.value = getBrokers()
                .map { domain ->
                    BrokerItem(BrokerInfo.map(domain))
                }
        }
    }

    fun brokerClicked(position: Int) {
        container.navigator {
            navigateHome(
                _items.get<BrokerItem>(position).info.id
            )
        }
    }

    fun addBrokerClicked() {
        container.navigator {
            navigateAddBroker()
        }
    }

    fun editBrokerClicked(position: Int) {
        container.navigator {
            navigateAddBroker(_items.get<BrokerItem>(position).info)
        }
    }

    fun showBrokerRemoveDialog(position: Int) {
        val brokerInfo = items.get<BrokerItem>(position).info

        container.raiseEffect {
            AlertDialog(
                title = Txt.of(R.string.broker_remove_dialog_title)
                    .withArgs(brokerInfo.name),
                message = Txt.of(R.string.broker_remove_dialog_message),
                yes = AlertDialog.Button(Txt.of(R.string.broker_remove_dialog_yes)) {
                    removeBroker(position)
                },
                no = AlertDialog.Button(Txt.of(R.string.broker_remove_dialog_cancel))
            )
        }
    }

    private fun removeBroker(position: Int) {
        container.launch(Dispatchers.Main) {
            val id = items.get<BrokerItem>(position).info.id
            deleteBroker(id)
            _items.value = _items.value.filterIndexed { i, _ -> i != position }
        }
    }

    private fun addBrokerToList(brokerInfo: BrokerInfo) {
        _items.update {
            it + BrokerItem(brokerInfo)
        }
    }

    private fun updateBrokerInList(brokerInfo: BrokerInfo) {
        val pos = _items.value.indexOfFirst {
            it is BrokerItem && it.info.id == brokerInfo.id
        }

        if (pos >= 0) {
            _items.value = items.value.toMutableList().apply {
                this[pos] = (this[pos] as BrokerItem).copy(info = brokerInfo)
            }
        }
    }
}
