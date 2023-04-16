package com.github.burachevsky.mqtthub.feature.brokers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.effect.AlertDialog
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.feature.brokers.item.BrokerItem
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.text.withArgs
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.domain.usecase.broker.DeleteBroker
import com.github.burachevsky.mqtthub.domain.usecase.broker.GetBrokers
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerAdded
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerEdited
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BrokersViewModel @Inject constructor(
    private val getBrokers: GetBrokers,
    private val deleteBroker: DeleteBroker,
    private val eventBus: EventBus,
) : ViewModel(), VM<BrokersNavigator> {

    override val container = ViewModelContainer<BrokersNavigator>(viewModelScope)

    private val _items: MutableStateFlow<List<ListItem>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    val noBrokersYet: Flow<Boolean> = items.map { it.isEmpty() }

    init {
        eventBus.apply {
            subscribe<BrokerAdded>(viewModelScope) {
                addBrokerToList(it.broker)
            }

            eventBus.subscribe<BrokerEdited>(viewModelScope) {
                updateBrokerInList(it.broker)
            }
        }

        container.launch(Dispatchers.Main) {
            _items.value = getBrokers().map(::BrokerItem)
        }
    }

    fun brokerClicked(position: Int) {
    }

    fun addBrokerClicked() {
        container.navigator {
            navigateAddBroker()
        }
    }

    fun editBrokerClicked(position: Int) {
        container.navigator {
            navigateAddBroker(_items.get<BrokerItem>(position).broker.id)
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
        container.launch(Dispatchers.Main) {
            val id = items.get<BrokerItem>(position).broker.id
            deleteBroker(id)
            _items.value = _items.value.filterIndexed { i, _ -> i != position }
            eventBus.send(BrokerDeleted(id))
        }
    }

    private fun addBrokerToList(broker: Broker) {
        _items.value = _items.value.toMutableList().apply {
            add(NEW_ITEM_POSITION, BrokerItem(broker))
        }
    }

    private fun updateBrokerInList(broker: Broker) {
        val pos = _items.value.indexOfFirst {
            it is BrokerItem && it.broker.id == broker.id
        }

        if (pos >= 0) {
            _items.value = items.value.toMutableList().apply {
                this[pos] = (this[pos] as BrokerItem).copy(broker = broker)
            }
        }
    }

    companion object {
        const val NEW_ITEM_POSITION = 0
    }
}
