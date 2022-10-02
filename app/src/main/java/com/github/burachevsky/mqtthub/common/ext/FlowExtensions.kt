package com.github.burachevsky.mqtthub.common.ext

import com.github.burachevsky.mqtthub.common.recycler.ListItem
import kotlinx.coroutines.flow.StateFlow

inline fun <reified R> StateFlow<List<ListItem>>.get(position: Int): R {
    return value[position] as R
}