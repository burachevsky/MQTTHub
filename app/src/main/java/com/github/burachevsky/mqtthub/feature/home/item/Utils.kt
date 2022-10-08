package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.ext.showPopupMenu
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder

fun ItemViewHolder.showTilePopupMenu(view: View, listener: TileItem.Listener): Boolean {
    view.showPopupMenu(R.menu.tile_item_menu) {
        when (it) {
            R.id.delete -> {
                listener.onDeleteClick(adapterPosition)
                true
            }

            R.id.edit -> {
                listener.onEditClick(adapterPosition)
                true
            }

            else -> false
        }
    }

    return true
}