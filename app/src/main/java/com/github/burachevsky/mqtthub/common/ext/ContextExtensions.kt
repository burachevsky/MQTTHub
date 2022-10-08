package com.github.burachevsky.mqtthub.common.ext

import android.content.Context
import android.util.TypedValue

fun Context.getValueFromAttribute(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}