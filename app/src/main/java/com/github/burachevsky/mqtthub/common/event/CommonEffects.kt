package com.github.burachevsky.mqtthub.common.event

import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent

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

data class NotifyPayloadUpdate(val tile: Tile) : AppEvent