package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.ext.showPopupMenu
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.databinding.ListItemButtonTileBinding

data class ButtonTileItem(
    override val tile: Tile,
) : TileItem {

    override fun layout() = LAYOUT

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    companion object {
        const val LAYOUT = R.layout.list_item_button_tile
    }
}

class ButtonTileItemViewHolder(
    itemView: View,
    listener: TileItem.Listener
) : ItemViewHolder(itemView) {

    private val binding = ListItemButtonTileBinding.bind(itemView)

    init {
        binding.buttonTile.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.buttonTile.setOnLongClickListener { view ->
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
            true
        }
    }

    override fun bind(item: ListItem) {
        item as ButtonTileItem
        binding.buttonTile.text = item.tile.name
    }
}

class ButtonTileItemAdapter(
    private val listener: TileItem.Listener
) : ItemAdapter {

    override fun viewType() = ButtonTileItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return ButtonTileItemViewHolder(inflateItemView(parent), listener)
    }
}