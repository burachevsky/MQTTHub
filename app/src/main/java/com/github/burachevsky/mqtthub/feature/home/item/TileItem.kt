package com.github.burachevsky.mqtthub.feature.home.item

import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Tile

interface TileItem : ListItem {
    val tile: Tile
    val editMode: EditMode?

    override fun areItemsTheSame(that: ListItem): Boolean {
        return that is TileItem && that.tile.id == tile.id
    }

    fun copyTile(tile: Tile): TileItem

    fun withEditMode(editMode: EditMode?): TileItem

    interface Listener {
        fun onClick(position: Int) {}
        fun onLongClick(position: Int): Boolean = false
    }
}

data class EditMode(
    val isSelected: Boolean = false
)