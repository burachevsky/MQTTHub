package com.github.burachevsky.mqtthub.common.widget

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.databinding.ListItemQosSelectorBinding

data class QosSelectorItem(
    var selectedValue: Int = 0
) : ListItem {

    override fun layout() = LAYOUT

    companion object {
        const val LAYOUT = R.layout.list_item_qos_selector
    }
}

class QosSelectorItemViewHolder(itemView: View) : ItemViewHolder(itemView) {

    private val binding = ListItemQosSelectorBinding.bind(itemView)

    private var item: QosSelectorItem? = null

    init {
        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                item?.selectedValue = when (checkedId) {
                    R.id.qos1 -> 1
                    R.id.qos2 -> 2
                    else -> 0
                }
            }
        }
    }

    override fun bind(item: ListItem) {
        item as QosSelectorItem
        this.item = item

        binding.toggleGroup.check(
            when (item.selectedValue) {
                1 -> R.id.qos1
                2 -> R.id.qos2
                else -> R.id.qos0
            }
        )
    }
}

class QosSelectorItemAdapter() : ItemAdapter {

    override fun viewType() = QosSelectorItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return QosSelectorItemViewHolder(inflateItemView(parent))
    }
}