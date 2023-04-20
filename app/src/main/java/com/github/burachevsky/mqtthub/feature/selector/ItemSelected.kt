package com.github.burachevsky.mqtthub.feature.selector

import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent

data class ItemSelected(val id: Int) : AppEvent
