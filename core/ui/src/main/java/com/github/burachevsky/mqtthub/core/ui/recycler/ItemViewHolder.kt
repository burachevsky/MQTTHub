package com.github.burachevsky.mqtthub.core.ui.recycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val context: Context
        get() = itemView.context

    open fun bind(item: ListItem, payloads: List<Int>) {}

    open fun bind(item: ListItem) {}
}