package com.github.burachevsky.mqtthub.common.widget

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.databinding.ListItemButtonBinding

class ButtonItem(val text: Txt) : ListItem {
    override fun layout() = LAYOUT

    companion object {
        val LAYOUT get() = R.layout.list_item_button
    }

    fun interface Listener {
        fun onClick(position: Int)
    }
}

class ButtonItemViewHolder(
    itemView: View,
    private val listener: ButtonItem.Listener,
) : ItemViewHolder(itemView) {

    private val binding = ListItemButtonBinding.bind(itemView)

    init {
        binding.button.setOnClickListener {
            listener.onClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        item as ButtonItem
        binding.button.text = item.text.get(context)
    }
}

class ButtonItemAdapter(
    private val listener: ButtonItem.Listener
) : ItemAdapter {

    override fun viewType() = ButtonItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return ButtonItemViewHolder(inflateItemView(parent), listener)
    }
}