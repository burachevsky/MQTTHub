package com.github.burachevsky.mqtthub.feature.brokers.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.core.database.entity.broker.Broker
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.ext.showPopupMenu
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.feature.brokers.databinding.ListItemBrokerBinding
import com.github.burachevsky.mqtthub.feature.brokers.R as featureR

data class BrokerItem(
    val broker: Broker,
) : ListItem {

    override fun layout() = LAYOUT

    override fun areItemsTheSame(that: ListItem): Boolean {
        return that is BrokerItem && that.broker.id == broker.id
    }

    companion object {
        val LAYOUT get() = featureR.layout.list_item_broker
    }

    interface Listener {
        fun onClick(position: Int)
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }
}

class BrokerItemViewHolder(
    itemView: View,
    private val listener: BrokerItem.Listener
) : ItemViewHolder(itemView) {

    private val binding = ListItemBrokerBinding.bind(itemView)

    init {
        binding.brokerPane.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.brokerPane.setOnLongClickListener { view ->
            view.showPopupMenu(R.menu.broker_item_menu) {
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

        binding.editButton.setOnClickListener {
            listener.onEditClick(adapterPosition)
        }

        binding.deleteButton.setOnClickListener {
            listener.onDeleteClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        item as BrokerItem
        binding.brokerName.text = item.broker.name
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