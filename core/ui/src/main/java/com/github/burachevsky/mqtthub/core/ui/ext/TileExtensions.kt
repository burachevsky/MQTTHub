package com.github.burachevsky.mqtthub.core.ui.ext

import com.github.burachevsky.mqtthub.core.model.SWITCH_OFF
import com.github.burachevsky.mqtthub.core.model.SWITCH_ON
import com.github.burachevsky.mqtthub.core.model.Tile

fun List<Tile.State>.getPayload(id: Int): String? {
    return find { it.id == id }?.payload
}

fun Tile.isState(stateId: Int): Boolean {
    return stateList.getPayload(stateId) == payload.stringValue
}

fun Tile.getSwitchOppositeStatePayload(): String {
    return when {
        isState(SWITCH_ON) -> stateList.getPayload(SWITCH_OFF)
        else -> stateList.getPayload(SWITCH_ON)
    }.orEmpty()
}