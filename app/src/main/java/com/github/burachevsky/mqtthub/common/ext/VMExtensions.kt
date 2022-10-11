package com.github.burachevsky.mqtthub.common.ext

import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.effect.ToastMessage
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of

fun VM<*>.toast(str: Int) {
    container.raiseEffect {
        ToastMessage(Txt.of(str))
    }
}

