package com.github.burachevsky.mqtthub.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.effect.ToastMessage
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.text.withArgs
import com.github.burachevsky.mqtthub.domain.usecase.broker.GetBroker
import com.github.burachevsky.mqtthub.domain.usecase.tile.GetBrokerTiles
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerInfo
import com.github.burachevsky.mqtthub.feature.home.addtile.text.TileAdded
import com.github.burachevsky.mqtthub.feature.home.item.TextTileItem
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getBroker: GetBroker,
    private val getBrokerTiles: GetBrokerTiles,
    private val eventBus: EventBus,
    args: HomeFragmentArgs,
) : ViewModel() {

    val container = ViewModelContainer<HomeNavigator>(viewModelScope)

    private var mqtt: MqttClient? = null

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _items = MutableStateFlow<List<ListItem>>(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    private val subscribeTopics = mutableListOf<String>()

    private var brokerInfo: BrokerInfo? = null

    init {
        container.launch(Dispatchers.Main) {
            val broker = getBroker(args.brokerId).let(BrokerInfo::map)
            brokerInfo = broker
            _title.value = broker.name

            initMqttClient(broker)

            getBrokerTiles(broker.id)
                .forEach { tileAdded(UITile.map(it)) }
        }

        eventBus.subscribe<TileAdded>(viewModelScope) {
            tileAdded(it.tile)
        }
    }

    fun addTileClicked() {
        container.navigator {
            brokerInfo?.id?.let { brokerId ->
                navigateAddTextTile(brokerId)
            }
        }
    }

    private fun initMqttClient(broker: BrokerInfo) {
        try {
            mqtt = MqttClient(broker.getServerAddress(), broker.clientId, MemoryPersistence())
            mqtt?.connect()
        } catch (e: Exception) {
            container.raiseEffect {
                Timber.d("$broker")
                ToastMessage(
                    text = Txt.of(R.string.error_failed_to_connect_to_broker)
                        .withArgs(broker.name, broker.getServerAddress())
                )
            }
        }
    }

    private fun tileAdded(tile: UITile) {
        _items.update {
            it + TextTileItem(tile, "")
        }

        trySubscribe(tile.subscribeTopic)
    }

    private fun trySubscribe(topic: String) {
        container.launch(Dispatchers.IO) {
            if (!subscribeTopics.contains(topic)) {
                subscribeTopics.add(topic)
                mqtt?.subscribe(topic) { topic, message ->
                    container.launch(Dispatchers.Main) {
                        updatePayload(topic, payload = String(message.payload))
                    }
                }
            }
        }
    }

    private fun updatePayload(topic: String, payload: String) {
        _items.update { item ->
            item.map {
                if (it is TileItem && it.tile.subscribeTopic == topic) {
                    it.copyTile(payload) as ListItem
                } else it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            mqtt?.disconnect()
        } catch (_: Exception) {}
    }
}