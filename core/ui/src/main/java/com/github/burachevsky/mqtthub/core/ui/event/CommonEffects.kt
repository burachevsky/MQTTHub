package com.github.burachevsky.mqtthub.core.ui.event

import android.net.Uri
import com.github.burachevsky.mqtthub.core.eventbus.AppEvent
import com.github.burachevsky.mqtthub.core.model.Broker
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import com.github.burachevsky.mqtthub.core.ui.text.Txt

data class ToastMessage(
    val text: Txt
) : AppEvent

data class Navigate(
    val navigateAction: (Navigator) -> Unit
) : AppEvent

data class AlertDialog(
    val title: Txt? = null,
    val message: Txt? = null,
    val yes: Button? = null,
    val no: Button? = null,
    val cancel: Button? = null,
    val cancelable: Boolean = true
) : AppEvent {

    data class Button(
        val text: Txt,
        val action: (() -> Unit)? = null
    )
}

object SwitchTheme : AppEvent

object RequestNotificationsPermissionIfNeeded : AppEvent

object GoToNotificationSettings : AppEvent

object StartNewMqttConnection : AppEvent

data class TileAdded(
    val tile: Tile
) : AppEvent

data class TileEdited(
    val tile: Tile
) : AppEvent

data class ItemSelected(val id: Int) : AppEvent

data class PublishTextEntered(
    val tileId: Long,
    val text: String,
) : AppEvent

data class BrokerAdded(val broker: Broker) : AppEvent

data class BrokerEdited(
    val broker: Broker
) : AppEvent

data class DashboardFileOpened(val uri: Uri) : AppEvent