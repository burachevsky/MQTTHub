package com.github.burachevsky.mqtthub.common.ext

import android.content.Context
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager

fun Context.getValueFromAttribute(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager