package com.github.burachevsky.mqtthub.feature.home.addtile.text

import com.github.burachevsky.mqtthub.common.effect.UIEffect
import com.github.burachevsky.mqtthub.feature.home.UITile

data class TileAdded(
    val tile: UITile
) : UIEffect
