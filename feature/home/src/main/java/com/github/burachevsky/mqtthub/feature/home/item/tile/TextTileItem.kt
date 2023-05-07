package com.github.burachevsky.mqtthub.feature.home.item.tile

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.core.model.TextTileSizeId
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.feature.home.databinding.ListItemTextTileBinding
import com.github.burachevsky.mqtthub.feature.home.item.APPEARANCE_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.EditMode
import com.github.burachevsky.mqtthub.feature.home.item.NAME_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.PAYLOAD_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import com.github.burachevsky.mqtthub.feature.home.item.bindEditModeAndBackground
import com.github.burachevsky.mqtthub.feature.home.R as featureR

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
            if (editMode != that.editMode || tile.design != that.tile.design)
                APPEARANCE_CHANGED else null,
        )
    }

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    override fun withEditMode(editMode: EditMode?): TileItem {
        return copy(editMode = editMode)
    }

    companion object {
        val LAYOUT get() = featureR.layout.list_item_text_tile

        const val TILE_TRANSITION_NAME = "text_tile_"
        const val TILE_NAME_TRANSITION_NAME = "text_tile_name_"
        const val TILE_PAYLOAD_TRANSITION_NAME = "text_tile_payload_"
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

    override fun bind(item: ListItem, payloads: List<Int>) {
        item as TextTileItem

        payloads.forEach {
            when (it) {
                NAME_CHANGED -> bindTileName(item)
                PAYLOAD_CHANGED -> bindTilePayload(item)
                APPEARANCE_CHANGED -> bindAppearance(item)
            }
        }
    }

    override fun bind(item: ListItem) {
        item as TextTileItem
        bindTransitionName(item)
        bindTileName(item)
        bindTilePayload(item)
        bindAppearance(item)
    }

    private fun bindTransitionName(item: TextTileItem) {
        val id = item.tile.id
        binding.tile.transitionName = TextTileItem.TILE_TRANSITION_NAME + id
        binding.tileName.transitionName = TextTileItem.TILE_NAME_TRANSITION_NAME + id
        binding.tilePayload.transitionName = TextTileItem.TILE_PAYLOAD_TRANSITION_NAME + id
    }

    private fun bindAppearance(item: TextTileItem) {
        val heightRes: Int
        val lines: Int

        when (item.tile.design.sizeId) {
            TextTileSizeId.MEDIUM -> {
                heightRes = R.dimen.tile_medium_height
                lines = 4
            }

            TextTileSizeId.LARGE -> {
                heightRes = R.dimen.tile_large_height
                lines = 7
            }

            else -> {
                heightRes = R.dimen.tile_small_height
                lines = 1
            }
        }

        binding.tile.apply {
            layoutParams = StaggeredGridLayoutManager.LayoutParams(layoutParams).apply {
                height = context.resources.getDimensionPixelSize(heightRes)
                isFullSpan = item.tile.design.isFullSpan
            }
        }

        binding.tilePayload.setLines(lines)
        bindEditModeAndBackground(item)
    }

    private fun bindTileName(item: TextTileItem) {
        binding.tileName.text = item.tile.name
    }

    private fun bindTilePayload(item: TextTileItem) {
        binding.tilePayload.text = item.tile.payload.stringValue
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