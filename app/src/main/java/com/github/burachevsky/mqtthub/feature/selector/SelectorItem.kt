package com.github.burachevsky.mqtthub.feature.selector

import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.ParcelableTxt
import com.github.burachevsky.mqtthub.databinding.ListItemSelectorItemBinding
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectorItem(
    val id: Int,
    val text: ParcelableTxt,
    val icon: Int? = null,
) : ListItem, Parcelable {

    override fun layout() = LAYOUT

    companion object {
        val LAYOUT get() = R.layout.list_item_selector_item
    }

    interface Listener {
        fun onClick(position: Int)
    }
}

class TileTypeItemViewHolder(
    itemView: View,
    listener: SelectorItem.Listener
) : ItemViewHolder(itemView) {

    private val binding = ListItemSelectorItemBinding.bind(itemView)

    init {
        binding.root.setOnClickListener {
            listener.onClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        item as SelectorItem

        binding.icon.apply {
            if (item.icon != null) {
                isVisible = true
                setImageResource(item.icon)
            } else {
                isVisible = false
            }
        }

        binding.label.text = item.text.get(itemView.context)
    }
}

class TileTypeItemAdapter(
    private val listener: SelectorItem.Listener
) : ItemAdapter {

    override fun viewType() = SelectorItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return TileTypeItemViewHolder(inflateItemView(parent), listener)
    }
}