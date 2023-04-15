package com.github.burachevsky.mqtthub.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.effect.AlertDialog
import com.github.burachevsky.mqtthub.common.effect.ToastMessage
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.ext.getServerAddress
import com.github.burachevsky.mqtthub.common.ext.getSwitchOppositeStatePayload
import com.github.burachevsky.mqtthub.common.ext.toast
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.text.withArgs
import com.github.burachevsky.mqtthub.common.widget.ConnectionState
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.usecase.broker.GetBrokerWithTiles
import com.github.burachevsky.mqtthub.domain.usecase.tile.*
import com.github.burachevsky.mqtthub.feature.home.addtile.TileAdded
import com.github.burachevsky.mqtthub.feature.home.addtile.TileEdited
import com.github.burachevsky.mqtthub.feature.home.item.*
import com.github.burachevsky.mqtthub.feature.home.publishtext.PublishTextEntered
import com.github.burachevsky.mqtthub.feature.home.typeselector.TileTypeSelected
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getBrokerWithTiles: GetBrokerWithTiles,
    private val saveUpdatedPayload: SaveUpdatedPayload,
    private val deleteTile: DeleteTile,
    private val updateTiles: UpdateTiles,
    private val deleteTiles: DeleteTiles,
    private val addTile: AddTile,
    eventBus: EventBus,
    args: HomeFragmentArgs,
) : ViewModel(), VM<HomeNavigator> {

    override val container = ViewModelContainer<HomeNavigator>(viewModelScope)

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Connecting)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private var mqtt: MqttClient? = null

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _items = MutableStateFlow<List<ListItem>>(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    val noTilesYet: Flow<Boolean> = items.map { it.isEmpty() }

    private val topicHandler = ConcurrentHashMap<String, MutableSharedFlow<MqttMessage>>()

    private var broker: Broker? = null

    private val _editMode = MutableStateFlow(EditModeState())
    val editMode: StateFlow<EditModeState> = _editMode

    private var itemReleased = true

    init {
        container.launch(Dispatchers.Main) {
            val brokerWithTiles = getBrokerWithTiles(args.brokerId)

            broker = brokerWithTiles.broker
            _title.value = broker?.name.orEmpty()

            _items.value = brokerWithTiles.tiles.map(::makeTileItem)

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

            subscribe<TileTypeSelected>(viewModelScope) {
                navigateAddTile(it.type)
            }

            subscribe<PublishTextEntered>(viewModelScope) {
                publish(it.tileId, it.text)
            }
        }
    }

    fun addTileClicked() {
        container.navigator {
            navigateSelectTileType()
        }
    }

    fun tileLongClicked(position: Int): Boolean {
        if (editMode.value.isEditMode) {
            tileClicked(position)
            return true
        }

        itemReleased = false

        showEditMode(true, position)

        return true
    }

    fun tileClicked(position: Int) {
        val item = items.get<TileItem>(position)
        val tile = item.tile

        if (editMode.value.isEditMode) {
            val itemEditMode = item.editMode!!

            _editMode.value.selectedTiles.apply {
                if (itemEditMode.isSelected) remove(tile) else add(tile)

                if (isEmpty()) {
                    showEditMode(false)
                } else {
                    _editMode.update {
                        it.copy(selectedCount = size)
                    }

                    _items.update {
                        it.toMutableList().apply {
                            set(
                                position,
                                item.withEditMode(itemEditMode.copy(!itemEditMode.isSelected))
                            )
                        }
                    }
                }
            }

        } else {
            when (tile.type) {
                Tile.Type.BUTTON -> {
                    publish(tile, tile.payload)
                }

                Tile.Type.SWITCH -> {
                    val newPayload = tile.getSwitchOppositeStatePayload()

                    publish(tile, newPayload)
                }

                Tile.Type.TEXT -> if (tile.publishTopic.isNotEmpty()) {
                    container.navigator {
                        navigatePublishTextDialog(tile.id, tile.name)
                    }
                }
            }
        }
    }

    fun canMoveItem(): Boolean {
        return editMode.value.canMoveItem && itemReleased
    }

    fun showEditMode(value: Boolean, selectedPosition: Int = -1) {
        if (!value) {
            _editMode.tryEmit(EditModeState(false))
        } else {
            val selected = selectedPosition >= 0
            _editMode.tryEmit(
                EditModeState(
                    isEditMode = true,
                    selectedTiles = if (selected) {
                        hashSetOf(items.get<TileItem>(selectedPosition).tile)
                    } else {
                        hashSetOf()
                    },
                    selectedCount = if (selected) 1 else 0,
                    canMoveItem = selected
                )
            )

        }

        _items.update { list ->
            list.mapIndexed { i, it ->
                if (it is TileItem)
                    it.withEditMode(
                        if (value) EditMode(isSelected = i == selectedPosition) else null
                    )
                else it
            }
        }
    }

    fun editTileClicked() {
        findSingleSelectedItemPosition()?.let(::editTileClicked)
        showEditMode(false)
    }

    fun duplicateTileClicked() {
        findSingleSelectedItemPosition()?.let { position ->
            toast(R.string.toast_tile_duplicated)

            showEditMode(false)

            container.launch(Dispatchers.Default) {
                val tile = addTile(
                    items.get<TileItem>(position)
                        .tile
                        .copy(id = 0, dashboardPosition = items.value.size)
                )

                tileAdded(tile)
            }
        }
    }

    fun editTileClicked(position: Int) {
        val tile = items.get<TileItem>(position).tile
        val brokerId = broker?.id ?: return
        container.navigator {
            when (tile.type) {
                Tile.Type.BUTTON -> navigateAddButtonTile(brokerId, tile.id, position)
                Tile.Type.TEXT -> navigateAddTextTile(brokerId, tile.id, position)
                Tile.Type.SWITCH -> navigateAddSwitch(brokerId, tile.id, position)
            }
        }
    }

    fun deleteTilesClicked() {
        container.raiseEffect {
            AlertDialog(
                title = Txt.of(
                    if (editMode.value.selectedCount == 1) R.string.remove_tile
                    else R.string.remove_selected_tiles
                ),
                message = Txt.of(R.string.remove_dialog_message),
                yes = AlertDialog.Button(Txt.of(R.string.remove_dialog_yes)) {
                    container.launch(Dispatchers.Main) {
                        tilesRemoved()
                    }
                },
                no = AlertDialog.Button(Txt.of(R.string.remove_dialog_cancel))
            )
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

    private fun findSingleSelectedItemPosition(): Int? {
        editMode.value.run {
            if (!isEditMode) return null
            if (selectedTiles.size != 1) return null
            val tile = selectedTiles.first()

            val position = items.value.indexOfFirst { it is TileItem && it.tile == tile }

            if (position >= 0) {
                return position
            }

            return null
        }
    }

    fun connectionClicked() {
        if (connectionState.value == ConnectionState.Disconnected) {
            container.launch(Dispatchers.IO) {
                broker?.let { broker ->
                    initMqttClient(broker)
                    topicHandler.clear()
                    items.value.forEach {
                        if (it is TileItem) {
                            subscribeIfNotSubscribed(it.tile.subscribeTopic)
                        }
                    }
                }
            }
        }
    }

    fun navigateUp() {
        if (editMode.value.isEditMode) {
            showEditMode(false)

        } else container.navigator {
            back()
        }
    }

    fun moveItem(positionFrom: Int, positionTo: Int) {
        _editMode.update {
            it.copy(isMovingMode = true)
        }
        _items.update {
            val list = it.toMutableList()
            val item = list.removeAt(positionFrom)
            list.add(positionTo, item)
            list
        }
    }

    fun commitReorder(position: Int) {
        itemReleased = true

        val dashboardPosition = items.get<TileItem>(position).tile.dashboardPosition

        if (dashboardPosition == position && !editMode.value.isMovingMode) {
            _editMode.update {
                if (it.isEditMode) it.copy(canMoveItem = false) else it
            }
            return
        }

        showEditMode(false)

        _items.update { list ->
            val result = list.mapIndexed { i, it ->
                if (it is TileItem) {
                    it.copyTile(it.tile.copy(dashboardPosition = i))
                } else {
                    it
                }
            }

            container.launch(Dispatchers.IO) {
                updateTiles(
                    result.filterIsInstance<TileItem>()
                        .map { it.tile }
                )
            }

            result
        }
    }

    private fun makeTileItem(tile: Tile): ListItem {
        return when (tile.type) {
            Tile.Type.BUTTON -> ButtonTileItem(tile)
            Tile.Type.TEXT -> TextTileItem(tile)
            Tile.Type.SWITCH -> SwitchTileItem(tile)
        }
    }

    private suspend fun initMqttClient(broker: Broker) {
        _connectionState.tryEmit(ConnectionState.Connecting)

        withContext(Dispatchers.IO) {
            try {
                mqtt = MqttClient(broker.getServerAddress(), broker.clientId, MemoryPersistence())
                mqtt?.setCallback(
                    object : MqttCallbackExtended {
                        override fun connectionLost(cause: Throwable?) {
                            _connectionState.tryEmit(ConnectionState.Disconnected)

                            container.raiseEffect {
                                ToastMessage(
                                    text = cause?.message?.let(Txt::of)
                                        ?: Txt.of(R.string.error_failed_to_connect)
                                )
                            }

                            Timber.e(cause)
                        }

                        override fun messageArrived(topic: String?, message: MqttMessage?) {}

                        override fun deliveryComplete(token: IMqttDeliveryToken?) {}

                        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                            if (mqtt?.isConnected == true) {
                                _connectionState.tryEmit(ConnectionState.Connected)
                            } else {
                                _connectionState.tryEmit(ConnectionState.Disconnected)
                            }
                            Timber.d("connect complete (reconnect: $reconnect)")
                        }
                    }
                )
                mqtt?.connect()
            } catch (e: Exception) {
                Timber.e(e)
                _connectionState.tryEmit(ConnectionState.Disconnected)
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

    private fun navigateAddTile(type: Tile.Type) {
        val brokerId = broker?.id ?: return

        when (type) {
            Tile.Type.BUTTON -> container.navigator {
                navigateAddButtonTile(brokerId, dashboardPosition = items.value.size)
            }

            Tile.Type.TEXT -> container.navigator {
                navigateAddTextTile(brokerId, dashboardPosition = items.value.size)
            }

            Tile.Type.SWITCH -> container.navigator {
                navigateAddSwitch(brokerId, dashboardPosition = items.value.size)
            }
        }
    }

    private suspend fun tilesRemoved() {
        val tiles = editMode.value.selectedTiles

        showEditMode(false)

        _items.update {
            it.filter {
                it is TileItem && !tiles.contains(it.tile)
            }
        }

        container.launch(Dispatchers.IO) {
            deleteTiles(tiles.toList())

            tiles.forEach {
                unsubscribeIfNoReceivers(it.subscribeTopic)
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
            it + makeTileItem(tile)
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
        if (topic.isNotEmpty() && !topicHandler.contains(topic)) {
            topicHandler[topic] = MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )

            container.launch(Dispatchers.Default) {
                topicHandler[topic]?.collect { message ->
                    messageReceived(topic, message)
                }
            }

            container.withContext(Dispatchers.IO) {
                mqtt?.subscribe(topic) { subscribeTopic, message ->
                    topicHandler[subscribeTopic]?.tryEmit(message)
                }
            }
        }
    }

    private suspend fun messageReceived(subscribeTopic: String, message: MqttMessage) {
        val payload = String(message.payload)

        updatePayload(subscribeTopic, payload = payload)

        broker?.id?.let { brokerId ->
            saveUpdatedPayload(PayloadUpdate(brokerId, subscribeTopic, payload))
        }
    }

    private suspend fun unsubscribeIfNoReceivers(topic: String) {
        val hasReceiver = items.value.any {
            it is TileItem && it.tile.subscribeTopic == topic
        }

        if (!hasReceiver && topic.isNotEmpty()) {
            container.withContext(Dispatchers.IO) {
                mqtt?.unsubscribe(topic)
            }
        }
    }

    private fun updatePayload(topic: String, payload: String) {
        _items.update { item ->
            item.map {
                if (it is TileItem && it.tile.subscribeTopic == topic) {
                    it.copyTile(it.tile.copy(payload = payload))
                } else it
            }
        }
    }

    private fun publish(tileId: Long, payload: String) {
        items.value
            .find { it is TileItem && it.tile.id == tileId }
            .let {
                publish((it as TileItem).tile, payload)
            }
    }

    private fun publish(tile: Tile, payload: String) {
        if (tile.publishTopic.isNotEmpty()) {
            container.launch(Dispatchers.IO) {
                Timber.d("publishing $payload, ${tile.publishTopic}")
                mqtt?.publish(tile.publishTopic, payload.toByteArray(), tile.qos, tile.retained)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                mqtt?.disconnect()
            } catch (_: Exception) {
            } finally {
                cancel()
            }
        }
    }
}