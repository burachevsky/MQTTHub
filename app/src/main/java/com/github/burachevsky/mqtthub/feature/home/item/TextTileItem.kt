package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.databinding.ListItemTextTileBinding
import com.github.burachevsky.mqtthub.feature.home.UITile

interface TileItem {
    val tile: UITile
    val payload: String

    fun copyTile(payload: String): TileItem
}

data class TextTileItem(
    override val tile: UITile,
    override val payload: String,
) : ListItem, TileItem {

    override fun layout() = LAYOUT

    override fun areItemsTheSame(that: ListItem): Boolean {
        return that is TextTileItem && that.tile.id == tile.id
    }

    override fun copyTile(payload: String): TextTileItem {
        return copy(payload = payload)
    }

    companion object {
        const val LAYOUT = R.layout.list_item_text_tile
    }

    interface Listener {
        fun onClick(position: Int)
        fun onLongClick(position: Int)
    }
}

class TextTileItemViewHolder(
    itemView: View,
    listener: TextTileItem.Listener,
) : ItemViewHolder(itemView) {

    val binding = ListItemTextTileBinding.bind(itemView)

    init {
        binding.tile.setOnClickListener {
            listener.onClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        item as TextTileItem
        binding.tileName.text = item.tile.name
        binding.tilePayload.text = item.payload
    }
}

class TextTileItemAdapter(
    private val listener: TextTileItem.Listener
) : ItemAdapter {
    override fun viewType() = TextTileItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return TextTileItemViewHolder(inflateItemView(parent), listener)
    }
}