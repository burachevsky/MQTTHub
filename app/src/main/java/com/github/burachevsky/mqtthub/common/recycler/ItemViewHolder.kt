package com.github.burachevsky.mqtthub.common.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    open fun bind(item: ListItem) {}
}