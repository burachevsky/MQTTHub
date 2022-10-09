package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.databinding.ListItemTextTileBinding

data class TextTileItem(
    override val tile: Tile,
    override val editMode: EditMode? = null
) : TileItem {

    override fun layout() = LAYOUT

    override fun getChangePayload(that: ListItem): List<Int> {
        that as TextTileItem

        return listOfNotNull(
            if (tile.name != that.tile.name) NAME_CHANGED else null,
            if (tile.payload != that.tile.payload) PAYLOAD_CHANGED else null,
            if (editMode != that.editMode) EDIT_MODE_CHANGED else null
        )
    }

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    override fun withEditMode(editMode: EditMode?): TileItem {
        return copy(editMode = editMode)
    }

    companion object {
        const val LAYOUT = R.layout.list_item_text_tile
    }
}

class TextTileItemViewHolder(
    itemView: View,
    listener: TileItem.Listener,
) : ItemViewHolder(itemView) {

    val binding = ListItemTextTileBinding.bind(itemView)

    init {
        binding.tile.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem, payloads: List<Int>) {
        item as TextTileItem

        payloads.forEach {
            when (it) {
                NAME_CHANGED -> bindTileName(item)
                PAYLOAD_CHANGED -> bindTilePayload(item)
                EDIT_MODE_CHANGED -> bindEditMode(item.editMode)
            }
        }
    }

    override fun bind(item: ListItem) {
        item as TextTileItem
        bindTileName(item)
        bindTilePayload(item)
        bindEditMode(item.editMode)
    }

    fun bindTileName(item: TextTileItem) {
        binding.tileName.text = item.tile.name
    }

    fun bindTilePayload(item: TextTileItem) {
        binding.tilePayload.text = item.tile.payload
    }
}

class TextTileItemAdapter(
    private val listener: TileItem.Listener
) : ItemAdapter {
    override fun viewType() = TextTileItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return TextTileItemViewHolder(inflateItemView(parent), listener)
    }
}