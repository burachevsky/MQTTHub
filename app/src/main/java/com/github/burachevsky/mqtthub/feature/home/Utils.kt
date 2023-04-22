package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.feature.home.item.ButtonTileItem
import com.github.burachevsky.mqtthub.feature.home.item.ChartTileItem
import com.github.burachevsky.mqtthub.feature.home.item.SwitchTileItem
import com.github.burachevsky.mqtthub.feature.home.item.TextTileItem

fun Tile.toListItem(): ListItem {
    return when (type) {
        Tile.Type.BUTTON -> ButtonTileItem(this)
        Tile.Type.TEXT -> TextTileItem(this)
        Tile.Type.SWITCH -> SwitchTileItem(this)
        Tile.Type.CHART -> ChartTileItem(this)
    }
}

fun List<Tile>.mapToItems(): List<ListItem> {
    return map { it.toListItem() }
}

object TileTypeId {
    const val TEXT = 0
    const val BUTTON = 1
    const val SWITCH = 2
    const val CHART = 3
}