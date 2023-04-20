package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.TextTileStyleId
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
            if (editMode != that.editMode) EDIT_MODE_CHANGED else null,
            if (tile.design != that.tile.design) DESIGN_CHANGED else null,
        )
    }

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    override fun withEditMode(editMode: EditMode?): TileItem {
        return copy(editMode = editMode)
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_text_tile
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
                DESIGN_CHANGED -> bindDesign(item)
            }
        }
    }

    override fun bind(item: ListItem) {
        item as TextTileItem
        bindDesign(item)
        bindTileName(item)
        bindTilePayload(item)
        bindEditMode(item.editMode)
    }

    private fun bindDesign(item: TextTileItem) {
        val heightRes: Int
        val lines: Int

        when (item.tile.design.styleId) {
            TextTileStyleId.MEDIUM -> {
                heightRes = R.dimen.tile_medium_height
                lines = 4
            }

            TextTileStyleId.LARGE -> {
                heightRes = R.dimen.tile_large_height
                lines = 7
            }

            else -> {
                heightRes = R.dimen.tile_small_height
                lines = 1
            }
        }

        binding.tile.updateLayoutParams {
            height = context.resources.getDimensionPixelSize(heightRes)
        }
        binding.tilePayload.setLines(lines)
    }

    private fun bindTileName(item: TextTileItem) {
        binding.tileName.text = item.tile.name
    }

    private fun bindTilePayload(item: TextTileItem) {
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