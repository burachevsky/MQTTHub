package com.github.burachevsky.mqtthub.feature.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.constant.Anim
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.viewModelContainer
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
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.DeleteDashboard
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.ExportDashboardToFile
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.GetCurrentDashboardWithTiles
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.GetDashboardWithTiles
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.ImportDashboardFromFile
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.UpdateDashboard
import com.github.burachevsky.mqtthub.domain.usecase.tile.*
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerEdited
import com.github.burachevsky.mqtthub.feature.brokers.BrokerDeleted
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardEdited
import com.github.burachevsky.mqtthub.feature.addtile.TileAdded
import com.github.burachevsky.mqtthub.feature.addtile.TileEdited
import com.github.burachevsky.mqtthub.domain.connection.BrokerConnectionPool
import com.github.burachevsky.mqtthub.feature.connection.TerminateBrokerConnection
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardDeleted
import com.github.burachevsky.mqtthub.feature.entertext.EnterTextActionId
import com.github.burachevsky.mqtthub.feature.home.item.*
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItem
import com.github.burachevsky.mqtthub.feature.homedrawer.DashboardImported
import com.github.burachevsky.mqtthub.feature.entertext.TextEntered
import com.github.burachevsky.mqtthub.feature.selector.ItemSelected
import com.github.burachevsky.mqtthub.feature.selector.SelectorConfig
import com.github.burachevsky.mqtthub.feature.selector.SelectorItem
import com.github.burachevsky.mqtthub.feature.tiledetails.text.PublishTextEntered
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named

class HomeViewModel @Inject constructor(
    //private val saveUpdatedPayload: SaveUpdatedPayload,
    private val updateTiles: UpdateTiles,
    private val deleteTiles: DeleteTiles,
    private val getCurrentDashboardWithTiles: GetCurrentDashboardWithTiles,
    private val getDashboardWithTiles: GetDashboardWithTiles,
    private val addTile: AddTile,
    private val getCurrentBroker: GetCurrentBroker,
    private val exportDashboardToFile: ExportDashboardToFile,
    private val importDashboardFromFile: ImportDashboardFromFile,
    private val deleteDashboard: DeleteDashboard,
    private val updateDashboard: UpdateDashboard,
    internal val eventBus: EventBus,
    internal val updateCurrentBroker: UpdateCurrentBroker,
    internal val updateCurrentDashboard: UpdateCurrentDashboard,
    @Named(Name.MQTT_EVENT_BUS) private val mqttEventBus: EventBus,
    private val connectionPool: BrokerConnectionPool,
) : ViewModel(), VM<HomeNavigator> {

    override val container = viewModelContainer()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Empty)
    val connectionState: StateFlow<ConnectionState> = _connectionState

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

    private var dashboard: Dashboard? = null

    private val payloadsReceivedWhileEditing = ConcurrentHashMap<String, String>()

    private var currentBrokerId: Long? = null

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

            if (broker != null) {
                val connection = connectionPool.getConnection(broker.id)
                currentBrokerId = broker.id

                if (connection == null || !connection.isRunning) {
                    _connectionState.emit(ConnectionState.Connecting)
                    container.raiseEffect {
                        StartNewBrokerConnection(broker.id)
                    }
                }
            }
        }

        eventBus.apply {
            subscribe<TileAdded>(viewModelScope) {
                tileAdded(it.tile)
            }

            subscribe<TileEdited>(viewModelScope) {
                tileEdited(it.tile)
            }

            subscribe<ItemSelected>(viewModelScope) {
                itemSelected(it.id)
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

            subscribe(viewModelScope, ::textEntered)
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
        if (!editMode.value.isEditMode) {
            showEditMode(true, position)
            return true
        }

        return false
    }

    fun tileClicked(position: Int) {
        val item = items.get<TileItem>(position)
        val tile = item.tile

        if (editMode.value.isEditMode) {
            val itemEditMode = item.editMode ?: EditMode(true)

            _editMode.value.selectedTiles.apply {
                if (itemEditMode.isSelected) remove(tile) else add(tile)

                _editMode.update {
                    it.copy(selectedCount = size)
                }

                _items.update {
                    it.toMutableList().apply {
                        set(
                            position,
                            item.withEditMode(
                                itemEditMode.copy(isSelected = !itemEditMode.isSelected)
                            )
                        )
                    }
                }
            }

        } else {
            when (tile.type) {
                Tile.Type.BUTTON -> container.launch(Dispatchers.Default) {
                    brokerConnection {
                        publish(tile, tile.payload)
                    }
                }

                Tile.Type.SWITCH -> container.launch(Dispatchers.Default) {
                    brokerConnection {
                        val newPayload = tile.getSwitchOppositeStatePayload()
                        publish(tile, newPayload)
                    }
                }

                Tile.Type.TEXT -> container.raiseEffect {
                    OpenTextTileDetails(position, tile.id)
                }

                Tile.Type.CHART -> {}

                Tile.Type.SLIDER -> {}
            }
        }
    }

    fun showOptionsMenu() {
        container.navigator {
            navigateSelector(
                SelectorConfig(
                    items = listOf(
                        SelectorItem(
                            id = OptionMenuId.EXPORT,
                            text = Txt.of(R.string.export_dashboard),
                            icon = R.drawable.ic_export
                        ),
                        SelectorItem(
                            id = OptionMenuId.IMPORT    ,
                            text = Txt.of(R.string.import_dashboard),
                            icon = R.drawable.ic_import
                        ),
                        SelectorItem(
                            id = OptionMenuId.EDIT_NAME,
                            text = Txt.of(R.string.change_dashboard_name),
                            icon = R.drawable.ic_edit
                        ),
                        SelectorItem(
                            id = OptionMenuId.DELETE,
                            text = Txt.of(R.string.delete_dashboard),
                            icon = R.drawable.ic_delete
                        ),
                    )
                )
            )
        }
    }

    fun sliderValueChanged(position: Int, value: Float) {
        val item = items.get<SliderTileItem>(position)
        val newPayload = "$value"
        if (item.tile.payload != newPayload) {
            publish(item.tile.id, "$value")
        }
    }

    fun canMoveItem(): Boolean {
        return editMode.value.isEditMode && editMode.value.canMoveItem
    }

    fun editTileClicked() {
        findSelectedItemsPositions()?.firstOrNull()?.let(::editTileClicked)
    }

    fun duplicateTileClicked() {
        val positions = findSelectedItemsPositions() ?: return

        container.launch(Dispatchers.Default) {
            val addedTiles = ArrayList<Tile>()
            var dashboardPosition = items.value.size

            for (i in positions) {
                val tile = addTile(
                    items.get<TileItem>(i)
                        .tile
                        .copy(id = 0, dashboardPosition = dashboardPosition++)
                )

                addedTiles.add(tile)
            }

            addedTiles.forEach(::tileAdded)

            if (positions.size == 1) {
                toast(R.string.toast_tile_duplicated)
            } else {
                toast(R.string.toast_tiles_duplicated)
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
        val dashboardPosition = items.get<TileItem>(position).tile.dashboardPosition

        if (dashboardPosition == position && !editMode.value.isMovingMode) {
            return
        }

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

    fun editModeClicked() {
        showEditMode(!_editMode.value.isEditMode)
    }

    fun exportDashboardToFile(uri: Uri) {
        container.launch(Dispatchers.IO) {
            dashboard?.id?.let { dashboardId ->
                exportDashboardToFile(dashboardId, uri)
                toast(R.string.dashboard_exported_successfully)
            }
        }
    }

    fun importDashboard(uri: Uri) {
        container.launch(Dispatchers.IO) {
            try {
                val importedDashboard = importDashboardFromFile(uri)
                eventBus.send(DashboardImported(importedDashboard))
                toast(R.string.dashboard_imported_successfully)
            } catch (e: Exception) {
                Timber.e(e)
                toast(R.string.failed_to_import_dashboard)
            }
        }
    }

    private fun showEditMode(value: Boolean, selectedPosition: Int = -1) {
        if (!value) {
            _editMode.tryEmit(EditModeState(false))

            container.launch(Dispatchers.Default) {
                payloadsReceivedWhileEditing.forEach { (topic, payload) ->
                    updatePayload(topic = topic, payload = payload)
                }
            }
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
                Tile.Type.CHART -> navigateAddChart(dashboardId, tile.id, position)
                Tile.Type.SLIDER -> navigateAddSlider(dashboardId, tile.id, position)
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

            is BrokerConnectionEvent.Terminated -> {}
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

    private fun findSelectedItemsPositions(): List<Int>? {
        editMode.value.run {
            if (!isEditMode) return null
            val tiles = selectedTiles

            val positions = ArrayList<Int>()

            items.value.forEachIndexed { i, it ->
                if (it is TileItem && tiles.contains(it.tile)) {
                    positions.add(i)
                }
            }

            return positions
        }
    }

    private fun reconnect(force: Boolean = false) {
        container.launch(Dispatchers.Default) {
            currentBrokerId?.let { brokerId ->
                val connection = connectionPool.getConnection(brokerId)

                if (connection == null || connection.isCanceled) {
                    container.raiseEffect {
                        StartNewBrokerConnection(brokerId)
                    }
                } else if (connection.isDisconnected || force) {
                    _connectionState.tryEmit(ConnectionState.Connecting)
                    connection.restart()
                }
            }
        }
    }

    private fun changeBroker(broker: Broker?) {
        container.launch(Dispatchers.Default) {
            _noBrokersYet.emit(broker == null)

        currentBrokerId?.let { currentBrokerId ->
            mqttEventBus.send(TerminateBrokerConnection(currentBrokerId))
        }

        _connectionState.emit(ConnectionState.Connecting)

        currentBrokerId = broker?.id

        broker?.id?.let {
            container.raiseEffect {
                StartNewBrokerConnection(broker.id)
            }
        }

            updateCurrentBroker(broker?.id)
        }
    }

    private fun brokerDeleted(brokerId: Long) {
        container.launch(Dispatchers.Default) {
            brokerConnection {
                if (broker.id == brokerId) {
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

    private fun itemSelected(selectedItemId: Int) {
        val dashboardId = dashboard?.id ?: return

        container.launch(Dispatchers.Main) {
            when (selectedItemId) {
                TileTypeId.BUTTON -> container.navigator {
                    navigateAddButtonTile(dashboardId, dashboardPosition = items.value.size)
                }

                TileTypeId.TEXT -> container.navigator {
                    navigateAddTextTile(dashboardId, dashboardPosition = items.value.size)
                }

                TileTypeId.SWITCH -> container.navigator {
                    navigateAddSwitch(dashboardId, dashboardPosition = items.value.size)
                }

                TileTypeId.CHART -> container.navigator {
                    navigateAddChart(dashboardId, dashboardPosition = items.value.size)
                }

                TileTypeId.SLIDER -> container.navigator {
                    navigateAddSlider(dashboardId, dashboardPosition = items.value.size)
                }

                OptionMenuId.EXPORT -> exportDashboardClicked()

                OptionMenuId.IMPORT -> container.navigator {
                    container.raiseEffect(ImportDashboard)
                }

                OptionMenuId.DELETE -> container.raiseEffect {
                    AlertDialog(
                        title = Txt.of(R.string.remove_dashboard_title),
                        message = Txt.of(R.string.remove_dashboard_dialog_message),
                        yes = AlertDialog.Button(Txt.of(R.string.remove_dialog_yes)) {
                            container.launch(Dispatchers.Default) {
                                dashboard?.id?.let { dashboardId ->
                                    deleteDashboard(dashboardId)
                                    eventBus.send(DashboardDeleted(dashboardId))
                                    toast(R.string.dashboard_deleted)
                                }
                            }
                        },
                        no = AlertDialog.Button(Txt.of(R.string.remove_dashboard_export_button)) {
                            exportDashboardClicked()
                        },
                        cancel = AlertDialog.Button(Txt.of(R.string.remove_dialog_cancel))
                    )
                }

                OptionMenuId.EDIT_NAME -> container.navigator {
                    navigateEnterText(
                        actionId = EnterTextActionId.CHANGE_DASHBOARD_NAME,
                        title = Txt.of(R.string.dashboard_name),
                        initText = Txt.of(dashboard?.name)
                    )
                }
            }
        }
    }

    private fun textEntered(event: TextEntered) {
        when (event.actionId) {
            EnterTextActionId.CHANGE_DASHBOARD_NAME -> {
                container.launch(Dispatchers.Default) {
                    dashboard?.copy(name = event.text)?.let { updatedDashboard ->
                        updateDashboard(updatedDashboard)
                        eventBus.send(DashboardEdited(updatedDashboard))
                        toast(R.string.dashboard_name_changed)
                    }
                }
            }
        }
    }

    private fun exportDashboardClicked() {
        dashboard?.name?.let { dashboardName ->
            container.raiseEffect {
                ExportDashboard("$dashboardName.json")
            }
        }
    }

    private suspend fun tilesRemoved() {
        val tiles = editMode.value.selectedTiles
        _editMode.update {
            it.copy(selectedTiles = hashSetOf(), selectedCount = 0)
        }

        _items.update {
            it.filter { it is TileItem && !tiles.contains(it.tile) }
        }
        _noTilesYet.value = items.value.isEmpty()

        container.launch(Dispatchers.Default) {
            deleteTiles(tiles.toList())
            unsubscribeIfNoReceivers(tiles.map { it.subscribeTopic })
        }
    }

    private fun tileAdded(tile: Tile) {
        _items.update { it + tile.toListItem() }
        _noTilesYet.value = items.value.isEmpty()
        container.launch(Dispatchers.Default) {
            brokerConnection { subscribe(tile.subscribeTopic) }
        }
    }

    private fun tileEdited(tile: Tile) {
        val i = _items.value.indexOfFirst { it is TileItem && it.tile.id == tile.id }
        val oldItem = items.get<TileItem>(i)

        _editMode.update {  editModeS ->
            editModeS.copy(
                selectedTiles = editModeS.selectedTiles
                    .map { if (it.id == tile.id) tile else it }
                    .toHashSet()
            )
        }

        _items.update {
            it.toMutableList().apply {
                this[i] = oldItem.copyTile(tile) as ListItem
            }
        }

        val oldTopic = oldItem.tile.subscribeTopic

        if (oldTopic != tile.subscribeTopic) {
            container.launch(Dispatchers.Default) {
                unsubscribeIfNoReceivers(listOf(oldTopic))
                brokerConnection { subscribe(tile.subscribeTopic) }
            }
        }
    }

    private suspend fun mqttMessageReceived(messageEvent: MqttMessageArrived) {
        container.withContext(Dispatchers.Default) {
            val topic = messageEvent.topic
            val payload = messageEvent.message

            if (editMode.value.isEditMode) {
                payloadsReceivedWhileEditing[topic] = payload
            } else {
                updatePayload(topic = topic, payload = payload)
            }
        }
    }

    private suspend fun unsubscribeIfNoReceivers(topics: List<String>) {
        withContext(Dispatchers.Default) {
            val topicsSet = topics.toHashSet()

            items.value.asSequence()
                .filterIsInstance<TileItem>()
                .map { it.tile.subscribeTopic }
                .forEach(topicsSet::remove)

            brokerConnection {
                unsubscribe(topicsSet.toList())
            }
        }
    }

    private fun updatePayload(topic: String, payload: String) {
        _items.update { items ->
            items.map { item ->
                if (item is TileItem && item.tile.subscribeTopic == topic) {
                    item.copyTile(item.tile.copy(payload = payload).initPayload())
                } else item
            }
        }
    }

    private fun publish(tileId: Long, payload: String) {
        container.launch(Dispatchers.Default) {
            brokerConnection {
                items.value
                    .find { it is TileItem && it.tile.id == tileId }
                    .let { publish((it as TileItem).tile, payload) }
            }
        }
    }

    private suspend fun brokerConnection(action: suspend BrokerConnection.() -> Unit) {
        currentBrokerId?.let { brokerId ->
            withContext(Dispatchers.Default) {
                connectionPool.getConnection(brokerId)?.action()
            }
        }
    }
}