package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.constant.SWITCH_ON
import com.github.burachevsky.mqtthub.common.ext.isState
import com.github.burachevsky.mqtthub.common.ext.showPopupMenu
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.databinding.ListItemSwitchTileBinding
import com.github.burachevsky.mqtthub.feature.home.item.SwitchTileItem.Companion.NAME_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.SwitchTileItem.Companion.SWITCH_STATE_CHANGED

data class SwitchTileItem(
    override val tile: Tile
) : TileItem {

    override fun layout() = LAYOUT

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    override fun getChangePayload(that: ListItem): List<Int> {
        that as SwitchTileItem

        return listOfNotNull(
            if (tile.name != that.tile.name) NAME_CHANGED else null,
            if (tile.payload != that.tile.payload) SWITCH_STATE_CHANGED else null,
        )
    }

    companion object {
        const val LAYOUT = R.layout.list_item_switch_tile

        const val NAME_CHANGED = 1
        const val SWITCH_STATE_CHANGED = 2
    }
}

class SwitchTileItemViewHolder(
    itemView: View,
    listener: TileItem.Listener
) : ItemViewHolder(itemView) {

    val binding = ListItemSwitchTileBinding.bind(itemView)

    init {
        binding.tile.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.tile.setOnLongClickListener{ view ->
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

        binding.tileSwitch.isClickable = false
    }

    override fun bind(item: ListItem, payloads: List<Int>) {
        item as SwitchTileItem

        payloads.forEach {
            when (it) {
                NAME_CHANGED -> bindName(item)
                SWITCH_STATE_CHANGED -> bindSwitchState(item)
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