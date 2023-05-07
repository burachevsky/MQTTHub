package com.github.burachevsky.mqtthub.core.ui.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layout: Int): View {
    return LayoutInflater.from(context)
        .inflate(layout, this, false)
}
