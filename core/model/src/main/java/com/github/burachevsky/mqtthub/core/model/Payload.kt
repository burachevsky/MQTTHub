package com.github.burachevsky.mqtthub.core.model

import com.github.burachevsky.mqtthub.core.common.Converter

interface Payload {

    val stringValue: String

    companion object {

        fun map(payload: String, type: Tile.Type): Payload {
            return when (type) {
                Tile.Type.CHART -> Converter.fromJson<ChartPayload>(payload)
                else -> StringPayload(payload)
            }
        }
    }
}