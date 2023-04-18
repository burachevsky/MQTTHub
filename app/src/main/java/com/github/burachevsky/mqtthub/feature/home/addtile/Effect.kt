package com.github.burachevsky.mqtthub.feature.home.addtile

import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent
import com.github.burachevsky.mqtthub.data.entity.Tile

data class TileAdded(
    val tile: Tile
) : AppEvent

data class TileEdited(
    val tile: Tile
) : AppEvent