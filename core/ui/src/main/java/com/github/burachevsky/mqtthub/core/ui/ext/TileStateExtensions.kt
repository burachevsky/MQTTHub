package com.github.burachevsky.mqtthub.core.ui.ext

import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile

fun List<Tile.State>.getPayload(id: Int): String? {
    return find { it.id == id }?.payload
}