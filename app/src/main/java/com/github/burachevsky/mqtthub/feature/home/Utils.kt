package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.feature.home.item.tile.ButtonTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.ChartTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.SwitchTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.TextTileItem

fun Tile.toListItem(): ListItem {
    return when (type) {
        Tile.Type.BUTTON -> ButtonTileItem(this)
        Tile.Type.TEXT -> TextTileItem(this)
        Tile.Type.SWITCH -> SwitchTileItem(this)
        Tile.Type.CHART -> ChartTileItem(this)
        Tile.Type.SLIDER -> SliderTileItem(this)
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
    const val SLIDER = 4
}