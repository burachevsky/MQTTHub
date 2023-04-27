package com.github.burachevsky.mqtthub.feature.home.item.tile

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.TextTileSizeId
import com.github.burachevsky.mqtthub.data.entity.TextTileStyleId
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.databinding.ListItemTextTileBinding
import com.github.burachevsky.mqtthub.feature.home.item.DESIGN_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.EDIT_MODE_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.EditMode
import com.github.burachevsky.mqtthub.feature.home.item.NAME_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.PAYLOAD_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import com.github.burachevsky.mqtthub.feature.home.item.bindEditMode

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
        bindTransitionName(item)
        bindDesign(item)
        bindTileName(item)
        bindTilePayload(item)
        bindEditMode(item.editMode)
    }

    private fun bindTransitionName(item: TextTileItem) {
        val id = item.tile.id
        binding.tile.transitionName = TextTileItem.TILE_TRANSITION_NAME + id
        binding.tileName.transitionName = TextTileItem.TILE_NAME_TRANSITION_NAME + id
        binding.tilePayload.transitionName = TextTileItem.TILE_PAYLOAD_TRANSITION_NAME + id
    }

    private fun bindDesign(item: TextTileItem) {
        val backgroundRes: Int = when (item.tile.design.styleId){
            TextTileStyleId.FILLED -> R.drawable.bg_tile_list_item_filled
            TextTileStyleId.OUTLINED -> R.drawable.bg_tile_list_item_outlined
            else -> R.drawable.bg_tile_list_item_empty
        }

        val tileIsFullSpan = item.tile.design.isFullSpan

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
                isFullSpan = tileIsFullSpan
            }
        }

        binding.tile.setBackgroundResource(backgroundRes)
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