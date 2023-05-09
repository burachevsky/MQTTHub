package com.github.burachevsky.mqtthub.feature.addtile

import androidx.lifecycle.ViewModel
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.constant.Anim
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.event.AlertDialog
import com.github.burachevsky.mqtthub.core.ui.event.GoToNotificationSettings
import com.github.burachevsky.mqtthub.core.ui.event.RequestNotificationsPermissionIfNeeded
import com.github.burachevsky.mqtthub.core.ui.event.TileAdded
import com.github.burachevsky.mqtthub.core.ui.event.TileEdited
import com.github.burachevsky.mqtthub.core.ui.ext.toast
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.widget.ButtonItem
import com.github.burachevsky.mqtthub.core.ui.widget.FieldType
import com.github.burachevsky.mqtthub.core.ui.widget.InputFieldItem
import com.github.burachevsky.mqtthub.core.ui.widget.SwitchItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleOption
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.GetTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.UpdateTile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class AddTileViewModel (
    protected val eventBus: EventBus,
    protected val getTile: GetTile,
    protected val updateTile: UpdateTile,
    protected val addTile: AddTile,
    protected val dashboardId: Long,
    protected val tileId: Long,
    protected val dashboardPosition: Int,
) : ViewModel(), VM<Navigator> {
    abstract val title: Int

    override val container = viewModelContainer()

    private val _items: MutableStateFlow<List<ListItem>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    private val _itemChanged = MutableSharedFlow<Int>()
    val itemChanged: SharedFlow<Int> = _itemChanged

    protected var oldTile: Tile? = null

    protected val name = InputFieldItem(
        label = Txt.of(R.string.tile_name)
    )

    protected val subscribeTopic = InputFieldItem(
        label = Txt.of(R.string.subscribe_topic),
        type = FieldType.URI,
    )

    protected val publishTopic = InputFieldItem(
        label = Txt.of(R.string.publish_topic),
        type = FieldType.URI,
    )

    protected val retain = SwitchItem(
        text = Txt.of(R.string.retain)
    )

    protected val qos = ToggleGroupItem(
        title = Txt.of(R.string.qos),
        options = listOf(
            ToggleOption(
                id = QosId.Qos0,
                text = Txt.of(R.string.qos_0)
            ),
            ToggleOption(
                id = QosId.Qos1,
                text = Txt.of(R.string.qos_1)
            ),
            ToggleOption(
                id = QosId.Qos2,
                text = Txt.of(R.string.qos_2)
            ),
        ),
        selectedValue = QosId.Qos0
    )

    private var goneToSettingsToAllowNotifications = false
    private var notificationsPermissionAlreadyRequested = false
    protected val notifyPayloadUpdate = SwitchItem(
        text = Txt.of(R.string.notify_payload_update),
        onCheckChanged = { isChecked ->
            if (isChecked) {
                if (!notificationsPermissionAlreadyRequested) {
                    notificationsPermissionAlreadyRequested = true
                    container.raiseEffect(RequestNotificationsPermissionIfNeeded)
                } else {
                    container.raiseEffect(CheckForNotificationsPermission)
                }
            }
        }
    )

    protected val save = ButtonItem(Txt.of(R.string.save))

    protected fun update() {
        _items.value = list()
    }

    fun init() {
        container.launch(Dispatchers.Default) {
            if (isEditMode()) {
                oldTile = getTile(tileId)
                    .also(::initFields)
            }

            update()
        }
    }

    abstract fun initFields(tile: Tile)

    abstract fun list(): List<ListItem>

    fun isEditMode() = tileId > 0

    abstract fun collectTile(): Tile

    fun saveResult() {
        container.launch(Dispatchers.Main) {
            val tile = collectTile()

            if (isEditMode()) {
                updateTile(tile)
                toast(R.string.toast_changes_saved)
                eventBus.send(TileEdited(tile))
            } else {
                eventBus.send(TileAdded(addTile(tile)))
            }

            container.navigator {
                back()
            }
        }
    }

    fun onNotificationPermissionResult(isGranted: Boolean) {
        if (!isGranted) {
            container.launch(Dispatchers.Main) {
                notifyPayloadUpdate.isChecked = false

                delay(Anim.MEDIUM_DURATION)
                _itemChanged.emit(items.value.indexOf(notifyPayloadUpdate))

                delay(Anim.DEFAULT_DURATION)
                container.raiseEffect {
                    AlertDialog(
                        title = Txt.of(R.string.notification_required_dialog_title),
                        message = Txt.of(R.string.notification_required_dialog_message),
                        yes = AlertDialog.Button(
                            text = Txt.of(R.string.notification_required_dialog_settings_button),
                            action = {
                                goneToSettingsToAllowNotifications = true
                                container.raiseEffect(GoToNotificationSettings)
                            }
                        ),
                    )
                }
            }
        }
    }

    fun notificationPermissionWhenResumed(isGranted: Boolean) {
        if (goneToSettingsToAllowNotifications) {
            goneToSettingsToAllowNotifications = false

            notifyPayloadUpdate.isChecked = isGranted
            container.launch(Dispatchers.Main) {
                _itemChanged.emit(items.value.indexOf(notifyPayloadUpdate))
            }
        }
    }
}