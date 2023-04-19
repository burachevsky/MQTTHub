package com.github.burachevsky.mqtthub.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.constant.Anim
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.event.AlertDialog
import com.github.burachevsky.mqtthub.common.event.ToastMessage
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
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
import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.di.Name
import com.github.burachevsky.mqtthub.domain.connection.BrokerConnection
import com.github.burachevsky.mqtthub.domain.connection.BrokerConnectionEvent
import com.github.burachevsky.mqtthub.domain.connection.MqttMessageArrived
import com.github.burachevsky.mqtthub.domain.usecase.broker.GetCurrentBroker
import com.github.burachevsky.mqtthub.domain.usecase.currentids.UpdateCurrentBroker
import com.github.burachevsky.mqtthub.domain.usecase.currentids.UpdateCurrentDashboard
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.GetCurrentDashboardWithTiles
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.GetDashboardWithTiles
import com.github.burachevsky.mqtthub.domain.usecase.tile.*
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerEdited
import com.github.burachevsky.mqtthub.feature.brokers.BrokerDeleted
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardEdited
import com.github.burachevsky.mqtthub.feature.home.addtile.TileAdded
import com.github.burachevsky.mqtthub.feature.home.addtile.TileEdited
import com.github.burachevsky.mqtthub.feature.home.item.*
import com.github.burachevsky.mqtthub.feature.home.publishtext.PublishTextEntered
import com.github.burachevsky.mqtthub.feature.home.typeselector.TileTypeSelected
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.eclipse.paho.client.mqttv3.*
import javax.inject.Inject
import javax.inject.Named

class HomeViewModel @Inject constructor(
    private val saveUpdatedPayload: SaveUpdatedPayload,
    private val updateTiles: UpdateTiles,
    private val deleteTiles: DeleteTiles,
    private val getCurrentDashboardWithTiles: GetCurrentDashboardWithTiles,
    private val getDashboardWithTiles: GetDashboardWithTiles,
    private val addTile: AddTile,
    private val getCurrentBroker: GetCurrentBroker,
    internal val eventBus: EventBus,
    internal val updateCurrentBroker: UpdateCurrentBroker,
    internal val updateCurrentDashboard: UpdateCurrentDashboard,
    @Named(Name.MQTT_EVENT_BUS) private val mqttEventBus: EventBus,
) : ViewModel(), VM<HomeNavigator> {

    override val container = ViewModelContainer<HomeNavigator>(viewModelScope)

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Empty)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private var brokerConnection: BrokerConnection? = null
        set(value) {
            field?.stop()
            field = value
        }

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _items = MutableStateFlow<List<ListItem>>(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    private val _noBrokersYet = MutableStateFlow(false)
    val noBrokersYet: StateFlow<Boolean> = _noBrokersYet

    private val _noTilesYet = MutableStateFlow(false)
    val noTilesYet: StateFlow<Boolean> = _noTilesYet

    private val _editMode = MutableStateFlow(EditModeState())
    val editMode: StateFlow<EditModeState> = _editMode

    private var itemReleased = true

    private var dashboard: Dashboard? = null

    init {
        container.launch(Dispatchers.Default) {
            val dashboardWithTiles = getCurrentDashboardWithTiles()
            val currentDashboard = dashboardWithTiles.dashboard
            dashboard = currentDashboard
            _title.emit(currentDashboard.name)

            val broker = getCurrentBroker()

            _noBrokersYet.emit(broker == null)
            _noTilesYet.emit(!_noBrokersYet.value && dashboardWithTiles.tiles.isEmpty())

            _items.emit(dashboardWithTiles.tiles.mapToItems())

            brokerConnection = broker?.toBrokerConnection()
            brokerConnection {
                _connectionState.emit(ConnectionState.Connecting)
                start()
            }
        }

        eventBus.apply {
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

            subscribe<DashboardEdited>(viewModelScope) {
                if (dashboard?.id == it.dashboard.id) {
                    _title.value = it.dashboard.name
                    dashboard = it.dashboard
                }
            }

            subscribe<BrokerEdited>(viewModelScope) {
                brokerConnection {
                    if (broker.id == it.broker.id) {
                        changeBroker(it.broker)
                    }
                }
            }

            subscribe<BrokerDeleted>(viewModelScope) {
                brokerDeleted(it.brokerId)
            }

            subscribe<BrokerChanged>(viewModelScope) {
                changeBroker(it.broker)
            }

            subscribe<DashboardChanged>(viewModelScope) {
                changeDashboard(it.dashboard)
            }
        }

        mqttEventBus.apply {
            subscribe(viewModelScope, ::brokerConnectionEventReceived)
            subscribe(viewModelScope, ::mqttMessageReceived)
        }
    }

    fun addTileClicked() {
        container.navigator { navigateSelectTileType() }
    }

    fun addFirstBroker() {
        container.navigator { navigateAddBroker() }
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
                Tile.Type.BUTTON -> brokerConnection {
                    publish(tile, tile.payload)
                }

                Tile.Type.SWITCH -> brokerConnection {
                    val newPayload = tile.getSwitchOppositeStatePayload()
                    publish(tile, newPayload)
                }

                Tile.Type.TEXT -> container.raiseEffect {
                    OpenTextTileDetails(position, tile.id)
                }
            }
        }
    }

    fun canMoveItem(): Boolean {
        return editMode.value.canMoveItem && itemReleased
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

    fun connectionClicked() {
        reconnect()
    }

    fun navigateUp() {
        if (editMode.value.isEditMode) {
            showEditMode(false)
        } else {
            container.raiseEffect(CloseHomeDrawerOrNavigateUp)
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

    private fun showEditMode(value: Boolean, selectedPosition: Int = -1) {
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

    private fun editTileClicked(position: Int) {
        val tile = items.get<TileItem>(position).tile
        val dashboardId = dashboard?.id ?: return
        container.navigator {
            when (tile.type) {
                Tile.Type.BUTTON -> navigateAddButtonTile(dashboardId, tile.id, position)
                Tile.Type.TEXT -> navigateAddTextTile(dashboardId, tile.id, position)
                Tile.Type.SWITCH -> navigateAddSwitch(dashboardId, tile.id, position)
            }
        }
    }

    private fun brokerConnectionEventReceived(event: BrokerConnectionEvent) {
        when (event) {
            is BrokerConnectionEvent.Connected -> {
                _connectionState.tryEmit(ConnectionState.Connected)

                container.launch(Dispatchers.Default) {
                    subscribeToAllTiles()
                }
            }

            is BrokerConnectionEvent.FailedToConnect -> {
                _connectionState.tryEmit(ConnectionState.Disconnected)

                val broker = event.connection.broker

                container.raiseEffect {
                    ToastMessage(
                        text = Txt.of(R.string.error_failed_to_connect_to_broker)
                            .withArgs(broker.name, broker.getServerAddress())
                    )
                }
            }

            is BrokerConnectionEvent.LostConnection -> {
                _connectionState.tryEmit(ConnectionState.Disconnected)

                val broker = event.connection.broker

                container.raiseEffect {
                    ToastMessage(
                        text = Txt.of(R.string.error_lost_connection_with_broker)
                            .withArgs(broker.name, broker.getServerAddress())
                    )
                }
            }
        }
    }

    private suspend fun subscribeToAllTiles() {
        container.withContext(Dispatchers.Default) {
            brokerConnection {
                items.value
                    .mapNotNull {
                        if (it is TileItem) it.tile.subscribeTopic
                        else null
                    }
                    .also { _noTilesYet.emit(it.isEmpty()) }
                    .let(::subscribe)
            }
        }
    }

    private suspend fun unsubscribeFromAllTiles() {
        container.withContext(Dispatchers.Default) {
            brokerConnection {
                _items.value
                    .mapNotNull {
                        if (it is TileItem) it.tile.subscribeTopic
                        else null
                    }
                    .let(::unsubscribe)
            }

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

    private fun reconnect(force: Boolean = false) {
        brokerConnection {
            if (force || isDisconnected) {
                _connectionState.tryEmit(ConnectionState.Connecting)
                restart()
            }
        }
    }

    private fun changeBroker(broker: Broker?) {
        container.launch(Dispatchers.Default) {
            _noBrokersYet.emit(broker == null)

            _connectionState.emit(ConnectionState.Connecting)
            brokerConnection = broker?.toBrokerConnection()
            brokerConnection { start() }

            updateCurrentBroker(broker?.id)
        }
    }

    private fun brokerDeleted(brokerId: Long) {
        brokerConnection {
            if (broker.id == brokerId) {
                container.launch(Dispatchers.Default) {
                    changeBroker(getCurrentBroker())
                }
            }
        }
    }

    private fun changeDashboard(newDashboard: Dashboard) {
        if (dashboard?.id == newDashboard.id) return

        container.launch(Dispatchers.Default) {
            delay(Anim.DEFAULT_DURATION)

            _title.emit(newDashboard.name)

            unsubscribeFromAllTiles()

            _items.emit(emptyList())

            updateCurrentDashboard(newDashboard.id)
            val dashboardWithTiles = getDashboardWithTiles(newDashboard.id)
            val currentDashboard = dashboardWithTiles.dashboard
            dashboard = currentDashboard
            _noTilesYet.emit(dashboardWithTiles.tiles.isEmpty())

            _items.emit(dashboardWithTiles.tiles.mapToItems())

            subscribeToAllTiles()
        }
    }

    private fun navigateAddTile(type: Tile.Type) {
        val dashboardId = dashboard?.id ?: return

        when (type) {
            Tile.Type.BUTTON -> container.navigator {
                navigateAddButtonTile(dashboardId, dashboardPosition = items.value.size)
            }

            Tile.Type.TEXT -> container.navigator {
                navigateAddTextTile(dashboardId, dashboardPosition = items.value.size)
            }

            Tile.Type.SWITCH -> container.navigator {
                navigateAddSwitch(dashboardId, dashboardPosition = items.value.size)
            }
        }
    }

    private suspend fun tilesRemoved() {
        val tiles = editMode.value.selectedTiles

        showEditMode(false)

        _items.update {
            it.filter { it is TileItem && !tiles.contains(it.tile) }
        }

        container.launch(Dispatchers.Default) {
            deleteTiles(tiles.toList())
            unsubscribeIfNoReceivers(tiles.map { it.subscribeTopic })
        }
    }

    private fun tileAdded(tile: Tile) {
        _items.update { it + tile.toListItem() }
        brokerConnection { subscribe(tile.subscribeTopic) }
    }

    private fun tileEdited(tile: Tile) {
        val i = _items.value.indexOfFirst { it is TileItem && it.tile.id == tile.id }
        val oldItem = items.get<TileItem>(i)

        _items.update {
            it.toMutableList().apply {
                this[i] = oldItem.copyTile(tile) as ListItem
            }
        }

        val oldTopic = oldItem.tile.subscribeTopic

        if (oldTopic != tile.subscribeTopic) {
            unsubscribeIfNoReceivers(listOf(oldTopic))
            brokerConnection { subscribe(tile.subscribeTopic) }
        }
    }

    private suspend fun mqttMessageReceived(messageEvent: MqttMessageArrived) {
        val topic = messageEvent.topic
        val payload = messageEvent.message

        updatePayload(topic, payload = payload)

        dashboard?.id?.let { dashboardId ->
            saveUpdatedPayload(PayloadUpdate(dashboardId, topic, payload))
        }
    }

    private fun unsubscribeIfNoReceivers(topics: List<String>) {
        val topicsSet = topics.toHashSet()

        items.value.asSequence()
            .filterIsInstance<TileItem>()
            .map { it.tile.subscribeTopic }
            .forEach(topicsSet::remove)

        brokerConnection {
            unsubscribe(topicsSet.toList())
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
        brokerConnection {
            items.value
                .find { it is TileItem && it.tile.id == tileId }
                .let { publish((it as TileItem).tile, payload) }
        }
    }

    private fun Broker.toBrokerConnection(): BrokerConnection {
        return BrokerConnection(this, mqttEventBus)
    }

    private inline fun brokerConnection(block: BrokerConnection.() -> Unit) {
        brokerConnection?.block()
    }

    override fun onCleared() {
        super.onCleared()
        brokerConnection = null
    }
}