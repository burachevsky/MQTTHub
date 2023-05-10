 package com.github.burachevsky.mqtthub.feature.home.item.tile

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.feature.home.R
import com.github.burachevsky.mqtthub.feature.home.databinding.ListItemButtonTileBinding
import com.github.burachevsky.mqtthub.feature.home.item.EditMode
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import com.github.burachevsky.mqtthub.feature.home.item.bindEditModeAndBackground

data class ButtonTileItem(
    override val tile: Tile,
    override val editMode: EditMode? = null
) : TileItem {

    override fun layout() = LAYOUT

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    override fun withEditMode(editMode: EditMode?): TileItem {
        return copy(editMode = editMode)
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_button_tile
    }
}

class ButtonTileItemViewHolder(
    itemView: View,
    listener: TileItem.Listener
) : ItemViewHolder(itemView) {

    private val binding = ListItemButtonTileBinding.bind(itemView)

    init {
        binding.tile.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.tile.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }

        binding.editModeOverlay.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        item as ButtonTileItem

        binding.tile.apply {
            layoutParams = StaggeredGridLayoutManager.LayoutParams(layoutParams).apply {
                isFullSpan = item.tile.design.isFullSpan
            }
        }

        binding.buttonTile.text = item.tile.name
        bindEditModeAndBackground(item)
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