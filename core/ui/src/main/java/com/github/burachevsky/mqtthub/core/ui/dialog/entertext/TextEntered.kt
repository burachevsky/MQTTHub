package com.github.burachevsky.mqtthub.core.ui.dialog.entertext

import com.github.burachevsky.mqtthub.core.eventbus.AppEvent

data class TextEntered(
    val actionId: Int,
    val text: String,
) : AppEvent