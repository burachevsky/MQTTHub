package com.github.burachevsky.mqtthub.feature.home.typeselector

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.databinding.ListItemTileTypeBinding

data class TileTypeItem(
    val text: Txt,
    val type: Tile.Type
) : ListItem {

    override fun layout() = LAYOUT

    companion object {
        val LAYOUT get() = R.layout.list_item_tile_type
    }

    interface Listener {
        fun onClick(position: Int)
    }
}

class TileTypeItemViewHolder(
    itemView: View,
    listener: TileTypeItem.Listener
) : ItemViewHolder(itemView) {

    private val binding = ListItemTileTypeBinding.bind(itemView)

    init {
        binding.root.setOnClickListener {
            listener.onClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        item as TileTypeItem
        binding.tileTypeName.text = item.text.get(itemView.context)
    }
}

class TileTypeItemAdapter(
    private val listener: TileTypeItem.Listener
) : ItemAdapter {

    override fun viewType() = TileTypeItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return TileTypeItemViewHolder(inflateItemView(parent), listener)
    }
}