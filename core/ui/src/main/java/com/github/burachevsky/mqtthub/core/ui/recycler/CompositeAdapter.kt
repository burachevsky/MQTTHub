package com.github.burachevsky.mqtthub.core.ui.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class CompositeAdapter(
    vararg itemAdapters: ItemAdapter
) : ListAdapter<ListItem, ItemViewHolder>(DiffCallback) {

    private val adapters: Map<Int, ItemAdapter> =
        itemAdapters.associateBy { it.viewType() }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).layout()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = adapters[viewType]
            ?: defaultAdapterFor(viewType)

        return adapter.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            holder.bind(getItem(position))
        } else {
            @Suppress("UNCHECKED_CAST")
            holder.bind(getItem(position), payloads.first() as List<Int>)
        }
    }

    private fun defaultAdapterFor(viewType: Int): ItemAdapter {
        return object : ItemAdapter {
            override fun viewType(): Int = viewType
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ListItem>() {

        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.areItemsTheSame(newItem)
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.areContentsTheSame(newItem)
        }

        override fun getChangePayload(oldItem: ListItem, newItem: ListItem): Any? {
            return oldItem.getChangePayload(newItem)
        }
    }
}