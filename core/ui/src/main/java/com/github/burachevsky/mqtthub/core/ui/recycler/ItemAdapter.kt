package com.github.burachevsky.mqtthub.core.ui.recycler

import android.view.ViewGroup
import com.github.burachevsky.mqtthub.core.ui.ext.inflate

interface ItemAdapter {

    fun viewType(): Int

    fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return ItemViewHolder(inflateItemView(parent))
    }

    fun inflateItemView(parent: ViewGroup) = parent.inflate(viewType())
}