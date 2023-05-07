package com.github.burachevsky.mqtthub.core.ui.event

import com.github.burachevsky.mqtthub.core.database.entity.broker.Broker
import com.github.burachevsky.mqtthub.core.database.entity.dashboard.Dashboard
import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile
import com.github.burachevsky.mqtthub.core.eventbus.AppEvent
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

object StartNewBrokerConnection : AppEvent

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

data class DashboardCreated(
    val dashboard: Dashboard
) : AppEvent

data class DashboardEdited(
    val dashboard: Dashboard
) : AppEvent

data class DashboardDeleted(
    val dashboardId: Long,
) : AppEvent

data class BrokerAdded(
    val broker: Broker
) : AppEvent

data class BrokerEdited(
    val broker: Broker
) : AppEvent