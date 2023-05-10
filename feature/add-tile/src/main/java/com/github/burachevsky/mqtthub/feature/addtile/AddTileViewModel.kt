package com.github.burachevsky.mqtthub.feature.addtile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.ObserveTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.UpdateTile
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
import com.github.burachevsky.mqtthub.core.ui.widget.SwitchItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

abstract class AddTileViewModel(
    protected val eventBus: EventBus,
    observeTile: ObserveTile,
    protected val updateTile: UpdateTile,
    protected val addTile: AddTile,
    protected val dashboardId: Long,
    protected val tileId: Long,
    protected val dashboardPosition: Int,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    protected val isEditMode = tileId > 0

    abstract val title: Int

    protected val itemStore = AddTileItemStore()

    protected val tile: LiveData<Tile?> = when {
        isEditMode -> observeTile(tileId).distinctUntilChanged().asLiveData()
        else -> MutableLiveData(null)
    }

    private val triggerListUpdate = MutableLiveData<Unit>()

    val items: LiveData<List<ListItem>> = MediatorLiveData<List<ListItem>>()
        .apply {
            addSource(tile) { tile ->
                value = makeItemsListFromTile(tile)
            }

            addSource(triggerListUpdate) {
                value = makeItemsList()
            }
        }

    private val _itemChanged = MutableSharedFlow<Int>()
    val itemChanged: SharedFlow<Int> = _itemChanged

    open val showHelpButton = false

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

    open fun showHelp() {}

    fun onNotificationPermissionResult(isGranted: Boolean) {
        if (!isGranted) {
            container.launch(Dispatchers.Main) {
                notifyPayloadUpdate.isChecked = false

                delay(Anim.MEDIUM_DURATION)
                items.value?.indexOf(notifyPayloadUpdate)?.let { i ->
                    _itemChanged.emit(i)
                }

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
                items.value?.indexOf(notifyPayloadUpdate)?.let { i ->
                    _itemChanged.emit(i)
                }
            }
        }
    }

    protected fun update() {
        triggerListUpdate.postValue(Unit)
    }

    abstract fun collectTile(): Tile

    fun saveResult() {
        container.launch(Dispatchers.IO) {
            val tile = collectTile()

            if (isEditMode) {
                updateTile(tile)
                eventBus.send(TileEdited(tile))
                toast(R.string.toast_changes_saved)
            } else {
                eventBus.send(TileAdded(addTile(tile)))
            }

            container.navigator {
                back()
            }
        }
    }

    abstract fun makeItemsListFromTile(tile: Tile?): List<ListItem>

    abstract fun makeItemsList(): List<ListItem>
}