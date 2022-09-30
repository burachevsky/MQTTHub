package com.github.burachevsky.mqtthub.feature.brokers.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.recycler.SupportsSwipeToDelete
import com.github.burachevsky.mqtthub.databinding.ListItemBrokerBinding
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerInfo

data class BrokerItem(
    val info: BrokerInfo,
) : ListItem {

    override fun layout() = LAYOUT

    override fun areItemsTheSame(that: ListItem): Boolean {
        return that is BrokerItem && that.info.id == info.id
    }

    companion object {
        const val LAYOUT = R.layout.list_item_broker
    }

    interface Listener {
        fun onClick(position: Int)
        fun onEditClick(position: Int)
        fun onLongClick(position: Int)
    }
}

class BrokerItemViewHolder(
    itemView: View,
    private val listener: BrokerItem.Listener
) : ItemViewHolder(itemView), SupportsSwipeToDelete {

    private val binding = ListItemBrokerBinding.bind(itemView)

    init {
        binding.brokerPane.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.brokerPane.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
            true
        }

        binding.editButton.setOnClickListener {
            listener.onEditClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        item as BrokerItem
        binding.brokerName.text = item.info.name
    }
}

class BrokerItemAdapter(
    private val listener: BrokerItem.Listener
) : ItemAdapter {

    override fun viewType() = BrokerItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): BrokerItemViewHolder {
        return BrokerItemViewHolder(inflateItemView(parent), listener)
    }
}