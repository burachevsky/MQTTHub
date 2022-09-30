package com.github.burachevsky.mqtthub.common.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter as RecyclerViewListAdapter

class CompositeAdapter(
    vararg itemAdapters: ItemAdapter
) : RecyclerViewListAdapter<ListItem, ItemViewHolder>(DiffCallback) {

    private val adapters: Map<Int, ItemAdapter> =
        itemAdapters.associateBy { it.viewType() }

    private val defaultAdapter = object : ItemAdapter {
        override fun viewType(): Int = 0
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).layout()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return adapters[viewType]?.onCreateViewHolder(parent)
            ?: defaultAdapter.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<ListItem>() {

        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.areItemsTheSame(newItem)
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.areContentsTheSame(newItem)
        }
    }
}