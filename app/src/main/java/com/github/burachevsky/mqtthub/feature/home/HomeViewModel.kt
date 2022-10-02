package com.github.burachevsky.mqtthub.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.effect.AlertDialog
import com.github.burachevsky.mqtthub.common.effect.ToastMessage
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.ext.getServerAddress
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.text.withArgs
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.usecase.broker.GetBrokerWithTiles
import com.github.burachevsky.mqtthub.domain.usecase.tile.DeleteTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.PayloadUpdate
import com.github.burachevsky.mqtthub.domain.usecase.tile.SaveUpdatedPayload
import com.github.burachevsky.mqtthub.feature.home.addtile.text.TileAdded
import com.github.burachevsky.mqtthub.feature.home.addtile.text.TileEdited
import com.github.burachevsky.mqtthub.feature.home.item.TextTileItem
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getBrokerWithTiles: GetBrokerWithTiles,
    private val saveUpdatedPayload: SaveUpdatedPayload,
    private val deleteTile: DeleteTile,
    eventBus: EventBus,
    args: HomeFragmentArgs,
) : ViewModel() {

    val container = ViewModelContainer<HomeNavigator>(viewModelScope)

    private var mqtt: MqttClient? = null

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _items = MutableStateFlow<List<ListItem>>(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    val noTilesYet: Flow<Boolean> = items.map { it.isEmpty() }

    private val subscribeTopics = mutableListOf<String>()

    private var broker: Broker? = null

    init {
        container.launch(Dispatchers.Main) {
            val brokerWithTiles = getBrokerWithTiles(args.brokerId)

            broker = brokerWithTiles.broker
            _title.value = broker?.name.orEmpty()

            _items.value = brokerWithTiles.tiles.map(::TextTileItem)

            broker?.let { brokerInfo ->
                initMqttClient(brokerInfo)

                brokerWithTiles.tiles.forEach {
                    subscribeIfNotSubscribed(it.subscribeTopic)
                }
            }
        }

        eventBus.apply{
            subscribe<TileAdded>(viewModelScope) {
                tileAdded(it.tile)
            }

            subscribe<TileEdited>(viewModelScope) {
                tileEdited(it.tile)
            }
        }
    }

    fun addTileClicked() {
        container.navigator {
            broker?.id?.let { brokerId ->
                navigateAddTextTile(brokerId)
            }
        }
    }

    fun editTileClicked(position: Int) {
        val tileId = items.get<TileItem>(position).tile.id

        container.navigator {
            broker?.id?.let { brokerId ->
                navigateAddTextTile(brokerId, tileId)
            }
        }
    }

    fun deleteTileClicked(position: Int) {
        val tile = items.get<TileItem>(position).tile

        container.raiseEffect {
            AlertDialog(
                title = Txt.of(R.string.remove_dialog_title)
                    .withArgs(tile.name),
                message = Txt.of(R.string.remove_dialog_message),
                yes = AlertDialog.Button(Txt.of(R.string.remove_dialog_yes)) {
                    container.launch(Dispatchers.Main) {
                        tileRemoved(position)
                    }
                },
                no = AlertDialog.Button(Txt.of(R.string.remove_dialog_cancel))
            )
        }
    }

    private suspend fun initMqttClient(broker: Broker) {
        withContext(Dispatchers.IO) {
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
    }

    private suspend fun tileRemoved(position: Int) {
        val tile = items.get<TileItem>(position).tile

        _items.update {
            it.filterIndexed { i, _ -> i != position }
        }

        container.launch(Dispatchers.IO) {
            deleteTile(tile.id)
        }

        unsubscribeIfNoReceivers(tile.subscribeTopic)
    }

    private suspend fun tileAdded(tile: Tile) {
        _items.update {
            it + TextTileItem(tile)
        }

        subscribeIfNotSubscribed(tile.subscribeTopic)
    }

    private suspend fun tileEdited(tile: Tile) {
        val i = _items.value.indexOfFirst { it is TileItem && it.tile.id == tile.id }
        val oldItem = items.get<TileItem>(i)

        _items.update {
            it.toMutableList().apply {
                this[i] = oldItem.copyTile(tile) as ListItem
            }
        }

        val oldTopic = oldItem.tile.subscribeTopic

        if (oldTopic != tile.subscribeTopic) {
            unsubscribeIfNoReceivers(oldTopic)
            subscribeIfNotSubscribed(tile.subscribeTopic)
        }
    }

    private suspend fun subscribeIfNotSubscribed(topic: String) {
        if (topic.isNotEmpty() && !subscribeTopics.contains(topic)) {
            subscribeTopics.add(topic)

            container.withContext(Dispatchers.IO) {
                mqtt?.subscribe(topic) { subscribeTopic, message ->
                    container.launch(Dispatchers.Main) {
                        val payload = String(message.payload)

                        updatePayload(subscribeTopic, payload = payload)

                        broker?.id?.let { brokerId ->
                            saveUpdatedPayload(PayloadUpdate(brokerId, subscribeTopic, payload))
                        }
                    }
                }
            }
        }
    }

    private suspend fun unsubscribeIfNoReceivers(topic: String) {
        val hasReceiver = items.value.any {
            it is TileItem && it.tile.subscribeTopic == topic
        }

        if (!hasReceiver) {
            container.withContext(Dispatchers.IO) {
                mqtt?.unsubscribe(topic)
            }
        }
    }

    private fun updatePayload(topic: String, payload: String) {
        _items.update { item ->
            item.map {
                if (it is TileItem && it.tile.subscribeTopic == topic) {
                    it.copyTile(it.tile.copy(payload = payload)) as ListItem
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