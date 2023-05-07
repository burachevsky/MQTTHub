package com.github.burachevsky.mqtthub.core.ui.ext

import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.event.ToastMessage
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of

fun VM<*>.toast(str: Int) {
    container.raiseEffect {
        ToastMessage(Txt.of(str))
    }
}

