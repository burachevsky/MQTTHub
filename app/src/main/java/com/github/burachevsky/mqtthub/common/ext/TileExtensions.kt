package com.github.burachevsky.mqtthub.common.ext

import com.github.burachevsky.mqtthub.common.constant.SWITCH_OFF
import com.github.burachevsky.mqtthub.common.constant.SWITCH_ON
import com.github.burachevsky.mqtthub.data.entity.Tile

fun Tile.isState(stateId: Int): Boolean {
    return stateList.getPayload(stateId) == payload
}

fun Tile.getSwitchOppositeStatePayload(): String {
    return when {
        isState(SWITCH_ON) -> stateList.getPayload(SWITCH_OFF)
        else -> stateList.getPayload(SWITCH_ON)
    }.orEmpty()
}