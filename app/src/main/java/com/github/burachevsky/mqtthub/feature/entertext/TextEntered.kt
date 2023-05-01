package com.github.burachevsky.mqtthub.feature.entertext

import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent

data class TextEntered(
    val actionId: Int,
    val text: String,
) : AppEvent