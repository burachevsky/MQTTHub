package com.github.burachevsky.mqtthub.common.effect

import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.text.Txt

data class ToastMessage(
    val text: Txt
) : UIEffect

data class Navigate(
    val navigateAction: (Navigator) -> Unit
) : UIEffect

data class AlertDialog(
    val title: Txt? = null,
    val message: Txt? = null,
    val yes: Button? = null,
    val no: Button? = null,
    val cancel: Button? = null,
    val cancelable: Boolean = true
) : UIEffect {

    data class Button(
        val text: Txt,
        val action: (() -> Unit)? = null
    )
}