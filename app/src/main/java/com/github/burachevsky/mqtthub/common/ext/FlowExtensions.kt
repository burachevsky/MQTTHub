package com.github.burachevsky.mqtthub.common.ext

import com.github.burachevsky.mqtthub.common.recycler.ListItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow

inline fun <reified R> StateFlow<List<ListItem>>.get(position: Int): R {
    return value[position] as R
}

fun <T> Flow<T>.throttle(periodMillis: Long): Flow<T> {
    if (periodMillis < 0) return this
    return flow {
        conflate().collect { value ->
            emit(value)
            delay(periodMillis)
        }
    }
}