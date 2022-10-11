package com.github.burachevsky.mqtthub.feature.home.publishtext

import com.github.burachevsky.mqtthub.common.effect.UIEffect

data class PublishTextEntered(
    val tileId: Long,
    val text: String,
) : UIEffect