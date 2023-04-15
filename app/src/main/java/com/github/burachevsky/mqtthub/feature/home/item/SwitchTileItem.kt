package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.constant.SWITCH_ON
import com.github.burachevsky.mqtthub.common.ext.isState
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.databinding.ListItemSwitchTileBinding

data class SwitchTileItem(
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

    override fun getChangePayload(that: ListItem): List<Int> {
        that as SwitchTileItem

        return listOfNotNull(
            if (tile.name != that.tile.name) NAME_CHANGED else null,
            if (tile.payload != that.tile.payload) SWITCH_STATE_CHANGED else null,
            if (editMode != that.editMode) EDIT_MODE_CHANGED else null
        )
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_switch_tile
    }
}

class SwitchTileItemViewHolder(
    itemView: View,
    listener: TileItem.Listener
) : ItemViewHolder(itemView) {

    private val binding = ListItemSwitchTileBinding.bind(itemView)

    init {
        binding.tileSwitch.setOnClickListener {
            binding.tileSwitch.isChecked = !binding.tileSwitch.isChecked
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        item as SwitchTileItem

        bindName(item)
        bindSwitchState(item)
    }

    override fun bind(item: ListItem, payloads: List<Int>) {
        item as SwitchTileItem

        payloads.forEach {
            when (it) {
                NAME_CHANGED -> bindName(item)
                SWITCH_STATE_CHANGED -> bindSwitchState(item)
                EDIT_MODE_CHANGED -> bindEditMode(item.editMode)
            }
        }
    }

    private fun bindName(item: SwitchTileItem) {
        binding.tileSwitch.text = item.tile.name
    }

    private fun bindSwitchState(item: SwitchTileItem) {
        binding.tileSwitch.isChecked = item.tile.isState(SWITCH_ON)
    }
}

class SwitchTileItemAdapter(
    private val listener: TileItem.Listener
) : ItemAdapter {

    override fun viewType() = SwitchTileItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return SwitchTileItemViewHolder(inflateItemView(parent), listener)
    }
}