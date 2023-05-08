package com.github.burachevsky.mqtthub.feature.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.eventbus.MQTT_EVENT_BUS
import com.github.burachevsky.mqtthub.core.model.BUTTON
import com.github.burachevsky.mqtthub.core.model.Broker
import com.github.burachevsky.mqtthub.core.model.CurrentIds
import com.github.burachevsky.mqtthub.core.model.Dashboard
import com.github.burachevsky.mqtthub.core.model.Payload
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.mqtt.MqttConnection
import com.github.burachevsky.mqtthub.core.mqtt.MqttConnectionEvent
import com.github.burachevsky.mqtthub.core.mqtt.MqttConnectionPool
import com.github.burachevsky.mqtthub.core.mqtt.MqttMessageArrived
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.constant.Anim
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.EnterTextActionId
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.TextEntered
import com.github.burachevsky.mqtthub.core.ui.dialog.selector.SelectorConfig
import com.github.burachevsky.mqtthub.core.ui.dialog.selector.SelectorItem
import com.github.burachevsky.mqtthub.core.ui.event.AlertDialog
import com.github.burachevsky.mqtthub.core.ui.event.DashboardDeleted
import com.github.burachevsky.mqtthub.core.ui.event.ItemSelected
import com.github.burachevsky.mqtthub.core.ui.event.PublishTextEntered
import com.github.burachevsky.mqtthub.core.ui.event.StartNewMqttConnection
import com.github.burachevsky.mqtthub.core.ui.event.TileAdded
import com.github.burachevsky.mqtthub.core.ui.event.TileEdited
import com.github.burachevsky.mqtthub.core.ui.event.ToastMessage
import com.github.burachevsky.mqtthub.core.ui.ext.get
import com.github.burachevsky.mqtthub.core.ui.ext.getPayload
import com.github.burachevsky.mqtthub.core.ui.ext.getSwitchOppositeStatePayload
import com.github.burachevsky.mqtthub.core.ui.ext.toast
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.text.withArgs
import com.github.burachevsky.mqtthub.core.ui.widget.ConnectionState
import com.github.burachevsky.mqtthub.core.ui.widget.DividerItem
import com.github.burachevsky.mqtthub.domain.usecase.broker.ObserveBrokers
import com.github.burachevsky.mqtthub.domain.usecase.currentids.ObserveCurrentIds
import com.github.burachevsky.mqtthub.domain.usecase.currentids.UpdateCurrentBroker
import com.github.burachevsky.mqtthub.domain.usecase.currentids.UpdateCurrentDashboard
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.DeleteDashboard
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.ExportDashboardToFile
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.ImportDashboardFromFile
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.ObserveCurrentDashboard
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.ObserveDashboards
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.UpdateDashboardName
import com.github.burachevsky.mqtthub.domain.usecase.tile.*
import com.github.burachevsky.mqtthub.feature.home.item.*
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerHeaderItem
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerLabelItem
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerMenuItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named

class HomeViewModel @Inject constructor(
    private val updateTiles: UpdateTiles,
    private val deleteTiles: DeleteTiles,
    private val addTile: AddTile,
    private val exportDashboardToFile: ExportDashboardToFile,
    private val importDashboardFromFile: ImportDashboardFromFile,
    private val deleteDashboard: DeleteDashboard,
    private val updateDashboardName: UpdateDashboardName,
    private val getDashboardTiles: GetDashboardTiles,
    private val eventBus: EventBus,
    @Named(MQTT_EVENT_BUS) private val mqttEventBus: EventBus,
    private val connectionPool: MqttConnectionPool,
    private val updateCurrentDashboard: UpdateCurrentDashboard,
    private val updateCurrentBroker: UpdateCurrentBroker,
    observeCurrentIds: ObserveCurrentIds,
    observeCurrentDashboard: ObserveCurrentDashboard,
    observeDashboards: ObserveDashboards,
    observeBrokers: ObserveBrokers,
) : ViewModel(), VM<HomeNavigator> {

    override val container = viewModelContainer()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Empty)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _items = MutableStateFlow<List<ListItem>>(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    private val _editMode = MutableStateFlow(EditModeState())
    val editMode: StateFlow<EditModeState> = _editMode

    private val payloadsReceivedWhileEditing = ConcurrentHashMap<String, String>()

    private val currentIds: Flow<CurrentIds> = observeCurrentIds()

    private val brokerId: StateFlow<Long?> = currentIds
        .map { it.currentBrokerId }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)

    private val dashboard: StateFlow<Dashboard?> = observeCurrentDashboard()
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val itemStore = HomeDrawerMenuItemStore()

    private val currentDashboardId: Flow<Long?> = currentIds
        .map { it.currentDashboardId }
        .distinctUntilChanged()

    private val currentBrokerId: Flow<Long?> = currentIds
        .map { it.currentBrokerId }
        .distinctUntilChanged()

    val dashboardName: StateFlow<String> = dashboard
        .map { it?.name.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val noBrokersYet: StateFlow<Boolean> = brokerId
        .map { it == null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val noTilesYet: Flow<Boolean> = items
        .combine(noBrokersYet) { items, noBrokersYet ->
            !noBrokersYet && items.isEmpty()
        }

    val drawerItems: StateFlow<List<ListItem>> =
        observeDashboards()
            .map { dashboards ->
                dashboards.map(DrawerMenuItem::map)
            }
            .selectCurrentItem(currentDashboardId, updateCurrentDashboard::invoke)
            .combine(
                observeBrokers()
                    .map { brokers ->
                        if (brokers.size == 1) {
                            updateCurrentBroker(brokers.single().id)
                        }
                        brokers.map(DrawerMenuItem::map)
                    }
                    .selectCurrentItem(currentBrokerId, updateCurrentBroker::invoke),
                ::makeItemList
            )
            .stateIn(container.scope, SharingStarted.Eagerly, listOf(DrawerHeaderItem))

    init {
        container.launch(Dispatchers.Default) {
            dashboard
                .filterNotNull()
                .distinctUntilChangedBy { it.id }
                .collect { dashboard ->
                    getDashboardTiles(dashboard.id)
                        .mapToItems()
                        .let {
                            _items.emit(it)
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

            subscribe(viewModelScope, ::textEntered)
        }

        mqttEventBus.apply {
            subscribe(viewModelScope, ::mqttConnectionEventReceived)
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
                    mqttConnection {
                        val payload = tile.stateList.getPayload(BUTTON) ?: return@mqttConnection
                        publish(tile, payload)
                    }
                }

                Tile.Type.SWITCH -> container.launch(Dispatchers.Default) {
                    mqttConnection {
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
                    title = Txt.of(R.string.dashboard_actions),
                    items = listOf(
                        SelectorItem(
                            id = OptionMenuId.EXPORT,
                            text = Txt.of(R.string.export_dashboard),
                            icon = R.drawable.ic_export
                        ),
                        SelectorItem(
                            id = OptionMenuId.IMPORT,
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
        if (item.tile.payload.stringValue != newPayload) {
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
            dashboard.value?.id?.let { dashboardId ->
                exportDashboardToFile(dashboardId, uri)
                toast(R.string.dashboard_exported_successfully)
            }
        }
    }

    fun importDashboard(uri: Uri) {
        container.launch(Dispatchers.IO) {
            try {
                importDashboardFromFile(uri)
                toast(R.string.dashboard_imported_successfully)
            } catch (e: Exception) {
                Timber.e(e)
                toast(R.string.failed_to_import_dashboard)
            }
        }
    }

    fun onDrawerLabelButtonClick(position: Int) {
        val item = drawerItems.get<DrawerLabelItem>(position)
        buttonClicked(item.id)
    }

    fun onDrawerMenuItemClick(position: Int) {
        val item = drawerItems.get<DrawerMenuItem>(position)

        when (item.type) {
            is DrawerMenuItem.Type.Dashboard -> {
                dashboardClicked(position, item.type.dashboard)
            }

            is DrawerMenuItem.Type.Button -> {
                buttonClicked(item.type.buttonId)
            }

            is DrawerMenuItem.Type.Broker -> {
                brokerClicked(position, item.type.broker)
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
        val dashboardId = dashboard.value?.id ?: return
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

    private fun mqttConnectionEventReceived(event: MqttConnectionEvent) {
        Timber.d("HomeViewModel: mqttConnectionEvent: $event")
        when (event) {
            is MqttConnectionEvent.Connected -> {
                _connectionState.tryEmit(ConnectionState.Connected)
            }

            is MqttConnectionEvent.FailedToConnect -> {
                _connectionState.tryEmit(ConnectionState.Disconnected)

                val broker = event.connection.broker

                container.raiseEffect {
                    ToastMessage(
                        text = Txt.of(R.string.error_failed_to_connect_to_broker)
                            .withArgs(broker.name, broker.getServerAddress())
                    )
                }
            }

            is MqttConnectionEvent.LostConnection -> {
                _connectionState.tryEmit(ConnectionState.Disconnected)

                val broker = event.connection.broker

                container.raiseEffect {
                    ToastMessage(
                        text = Txt.of(R.string.error_lost_connection_with_broker)
                            .withArgs(broker.name, broker.getServerAddress())
                    )
                }
            }

            is MqttConnectionEvent.Terminated -> {}
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
            brokerId.value?.let { brokerId ->
                val connection = connectionPool.getConnection(brokerId)

                if (connection == null || connection.isCanceled) {
                    eventBus.send(StartNewMqttConnection)
                } else if (connection.isDisconnected || force) {
                    _connectionState.tryEmit(ConnectionState.Connecting)
                    connection.restart()
                }
            }
        }
    }

    private fun itemSelected(selectedItemId: Int) {
        val dashboardId = dashboard.value?.id ?: return

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

                OptionMenuId.DELETE -> {
                    val dashboardsCount = drawerItems.value.count {
                        it is DrawerMenuItem && it.type is DrawerMenuItem.Type.Dashboard
                    }

                    if (dashboardsCount == 1) {
                        container.raiseEffect {
                            AlertDialog(
                                message = Txt.of(R.string.dashboards_min_items_dialog_message),
                                yes = AlertDialog.Button(
                                    text = Txt.of(R.string.dashboards_min_items_dialog_button)
                                )
                            )
                        }
                    } else {
                        container.raiseEffect {
                            AlertDialog(
                                title = Txt.of(R.string.remove_dashboard_title),
                                message = Txt.of(R.string.remove_dashboard_dialog_message),
                                yes = AlertDialog.Button(Txt.of(R.string.remove_dialog_yes)) {
                                    container.launch(Dispatchers.Default) {
                                        dashboard.value?.id?.let { dashboardId ->
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
                    }
                }

                OptionMenuId.EDIT_NAME -> container.navigator {
                    navigateEnterText(
                        actionId = EnterTextActionId.CHANGE_DASHBOARD_NAME,
                        title = Txt.of(R.string.dashboard_name),
                        initText = Txt.of(dashboardName.value)
                    )
                }
            }
        }
    }

    private fun textEntered(event: TextEntered) {
        when (event.actionId) {
            EnterTextActionId.CHANGE_DASHBOARD_NAME -> {
                container.launch(Dispatchers.Default) {
                    dashboard.value?.id?.let { dashboardId ->
                        updateDashboardName(dashboardId, event.text)
                    }
                }
            }
        }
    }

    private fun exportDashboardClicked() {
        dashboardName.value.ifEmpty { "Dashboard" }.let { dashboardName ->
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

        _items.update { list ->
            list.filter { it is TileItem && !tiles.contains(it.tile) }
        }

        container.launch(Dispatchers.Default) {
            deleteTiles(tiles.toList())
        }
    }

    private fun tileAdded(tile: Tile) {
        _items.update { it + tile.toListItem() }
    }

    private fun tileEdited(tile: Tile) {
        val i = _items.value.indexOfFirst { it is TileItem && it.tile.id == tile.id }
        val oldItem = items.get<TileItem>(i)

        _editMode.update { editMode ->
            editMode.copy(
                selectedTiles = editMode.selectedTiles
                    .map { if (it.id == tile.id) tile else it }
                    .toHashSet()
            )
        }

        _items.update {
            it.toMutableList().apply {
                this[i] = oldItem.copyTile(tile) as ListItem
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

    private fun updatePayload(topic: String, payload: String) {
        _items.update { items ->
            items.map { item ->
                if (item is TileItem && item.tile.subscribeTopic == topic) {
                    val tile = item.tile
                    item.copyTile(item.tile.copy(payload = Payload.map(payload, tile.type)))
                } else item
            }
        }
    }

    private fun publish(tileId: Long, payload: String) {
        container.launch(Dispatchers.Default) {
            mqttConnection {
                items.value
                    .find { it is TileItem && it.tile.id == tileId }
                    .let { publish((it as TileItem).tile, payload) }
            }
        }
    }

    private suspend fun mqttConnection(action: suspend MqttConnection.() -> Unit) {
        brokerId.value?.let { brokerId ->
            withContext(Dispatchers.Default) {
                connectionPool.getConnection(brokerId)?.action()
            }
        }
    }

    private fun makeItemList(
        dashboards: List<DrawerMenuItem>,
        brokers: List<DrawerMenuItem>
    ): List<ListItem> {
        Timber.d("HomeDrawer: updating")
        return ArrayList<ListItem>().apply {
            add(DrawerHeaderItem)
            add(DividerItem)
            add(itemStore.dashboardsLabel)
            addAll(dashboards)
            add(itemStore.createDashboardButton)
            add(DividerItem)
            add(itemStore.brokersLabel)
            addAll(brokers)
            add(itemStore.addBrokerButton)
            add(DividerItem)
            add(itemStore.settingsButton)
        }
    }

    private fun buttonClicked(buttonId: Int) {
        when (buttonId) {
            DrawerMenuId.DRAWER_BUTTON_CREATE_NEW_DASHBOARD -> closeDrawerAndNavigate {
                navigateEditDashboards(addNew = true)
            }

            DrawerMenuId.DRAWER_BUTTON_ADD_NEW_BROKER -> closeDrawerAndNavigate {
                navigateAddBroker()
            }

            DrawerMenuId.DRAWER_BUTTON_SETTINGS -> closeDrawerAndNavigate {
                navigateSettings()
            }

            DrawerMenuId.DRAWER_BUTTON_HELP_AND_FEEDBACK -> {}

            DrawerMenuId.DRAWER_BUTTON_EDIT_BROKERS -> closeDrawerAndNavigate {
                navigateEditBrokers()
            }

            DrawerMenuId.DRAWER_BUTTON_EDIT_DASHBOARDS -> closeDrawerAndNavigate {
                navigateEditDashboards()
            }
        }
    }

    private fun dashboardClicked(position: Int, dashboard: Dashboard) {
        container.launch(Dispatchers.Default) {
            container.raiseEffect(CloseHomeDrawer)

            delay(Anim.DEFAULT_DURATION)

            if (!drawerItems.get<DrawerMenuItem>(position).isSelected) {
                updateCurrentDashboard(dashboard.id)
            }
        }
    }

    private fun brokerClicked(position: Int, broker: Broker) {
        container.launch(Dispatchers.Default) {
            if (!drawerItems.get<DrawerMenuItem>(position).isSelected) {
                updateCurrentBroker(broker.id)
            }

            container.raiseEffect(CloseHomeDrawer)
        }
    }

    private fun closeDrawerAndNavigate(navigate: HomeNavigator.() -> Unit) {
        container.launch(Dispatchers.Main) {
            container.raiseEffect(CloseHomeDrawer)
            delay(Anim.DEFAULT_DURATION)
            container.navigator { navigate() }
        }
    }
}