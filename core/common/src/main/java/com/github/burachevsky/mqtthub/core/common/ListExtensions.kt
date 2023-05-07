package com.github.burachevsky.mqtthub.core.common

inline fun <T, reified R> List<T>.mapToArray(
    crossinline mapper: T.() -> R
) = Array(size) { i -> get(i).mapper() }