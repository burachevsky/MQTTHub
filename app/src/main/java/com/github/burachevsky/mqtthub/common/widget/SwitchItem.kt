package com.github.burachevsky.mqtthub.common.widget

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.databinding.ListItemSwitchBinding

data class SwitchItem(
    val text: Txt,
    var isChecked: Boolean = false,
    val onCheckChanged: ((Boolean) -> Unit)? = null
) : ListItem {
    override fun layout() = LAYOUT

    override fun areItemsTheSame(that: ListItem): Boolean {
        return this == that
    }

    companion object {
        const val LAYOUT = R.layout.list_item_switch
    }
}

class SwitchItemViewHolder(
    itemView: View
) : ItemViewHolder(itemView) {

    private val binding = ListItemSwitchBinding.bind(itemView)

    private var item: SwitchItem? = null

    init {
        binding.switchItem.setOnCheckedChangeListener { _, isChecked ->
            item?.isChecked = isChecked
            item?.onCheckChanged?.invoke(isChecked)
        }
    }

    override fun bind(item: ListItem) {
        item as SwitchItem
        this.item = item
        binding.switchItem.text = item.text.get(itemView.context)
        binding.switchItem.isChecked = item.isChecked
    }
}

class SwitchItemAdapter() : ItemAdapter {

    override fun viewType() = SwitchItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return SwitchItemViewHolder(inflateItemView(parent))
    }
}
