package com.github.burachevsky.mqtthub.common.widget

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.databinding.ListItemSubtitleBinding

data class SubtitleItem(
    val text: Txt
) : ListItem {

    override fun layout() = LAYOUT

    override fun areItemsTheSame(that: ListItem): Boolean {
        return this == that
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_subtitle
    }
}

class SubtitleItemViewHolder(itemView: View): ItemViewHolder(itemView) {

    private val binding = ListItemSubtitleBinding.bind(itemView)

    override fun bind(item: ListItem) {
        item as SubtitleItem

        binding.subtitle.text = item.text.get(context)
    }
}

class SubtitleItemAdapter : ItemAdapter {

    override fun viewType() = SubtitleItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return SubtitleItemViewHolder(inflateItemView(parent))
    }
}