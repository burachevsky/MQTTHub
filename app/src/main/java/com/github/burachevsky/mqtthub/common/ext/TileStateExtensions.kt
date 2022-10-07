package com.github.burachevsky.mqtthub.common.ext

import com.github.burachevsky.mqtthub.data.entity.Tile


fun List<Tile.State>.getPayload(id: Int): String? {
    return find { it.id == id }?.payload
}