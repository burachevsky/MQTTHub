package com.github.burachevsky.mqtthub.feature.tiledetails.text

import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent

data class PublishTextEntered(
    val tileId: Long,
    val text: String,
) : AppEvent