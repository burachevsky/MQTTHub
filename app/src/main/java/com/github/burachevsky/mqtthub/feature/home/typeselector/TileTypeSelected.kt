package com.github.burachevsky.mqtthub.feature.home.typeselector

import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent
import com.github.burachevsky.mqtthub.data.entity.Tile

data class TileTypeSelected(
    val type: Tile.Type
) : AppEvent
