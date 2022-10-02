package com.github.burachevsky.mqtthub.common.recycler

interface ListItem {

    fun layout(): Int

    fun areItemsTheSame(that: ListItem): Boolean {
        return this === that
    }

    fun areContentsTheSame(that: ListItem): Boolean {
        return this == that
    }

    fun getChangePayload(that: ListItem): List<Int>? {
        return null
    }
}