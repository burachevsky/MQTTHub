package com.github.burachevsky.mqtthub.common.recycler

import androidx.viewbinding.ViewBinding

data class BindingViewHolder<B : ViewBinding>(
    val binding: B
) : ItemViewHolder(binding.root)