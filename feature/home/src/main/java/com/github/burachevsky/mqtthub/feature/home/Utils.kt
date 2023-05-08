package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerMenuItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.ButtonTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.ChartTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.SwitchTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.TextTileItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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

object OptionMenuId {
    const val EXPORT = 5
    const val IMPORT = 6
    const val DELETE = 7
    const val EDIT_NAME = 8
}

object DrawerMenuId {
    const val DRAWER_BUTTON_CREATE_NEW_DASHBOARD = -1
    const val DRAWER_BUTTON_ADD_NEW_BROKER = -2
    const val DRAWER_BUTTON_SETTINGS = -3
    const val DRAWER_BUTTON_HELP_AND_FEEDBACK = -4
    const val DRAWER_BUTTON_EDIT_DASHBOARDS = -5
    const val DRAWER_BUTTON_EDIT_BROKERS = -6
}

fun Flow<List<DrawerMenuItem>>.selectCurrentItem(
    currentIdFlow: Flow<Long?>,
    ifSelectionIsEmpty: suspend (Long) -> Unit
): Flow<List<DrawerMenuItem>> {
    return combine(currentIdFlow) { list, currentId ->
        var currentItemIsSelected = false

        val resultList = list.map {
            val selected = it.type.id == currentId
            currentItemIsSelected = currentItemIsSelected || selected
            it.copy(isSelected = selected)
        }

        if (!currentItemIsSelected && list.isNotEmpty()) {
            ifSelectionIsEmpty(list.first().type.id)
        }

        resultList
    }
}