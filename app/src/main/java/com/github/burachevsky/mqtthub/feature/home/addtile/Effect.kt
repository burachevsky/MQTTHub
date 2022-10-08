package com.github.burachevsky.mqtthub.feature.home.addtile

import com.github.burachevsky.mqtthub.common.effect.UIEffect
import com.github.burachevsky.mqtthub.data.entity.Tile

data class TileAdded(
    val tile: Tile
) : UIEffect

data class TileEdited(
    val tile: Tile
) : UIEffect