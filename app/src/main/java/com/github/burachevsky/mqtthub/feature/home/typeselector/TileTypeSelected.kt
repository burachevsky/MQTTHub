package com.github.burachevsky.mqtthub.feature.home.typeselector

import com.github.burachevsky.mqtthub.common.effect.UIEffect
import com.github.burachevsky.mqtthub.data.entity.Tile

data class TileTypeSelected(
    val type: Tile.Type
) : UIEffect
