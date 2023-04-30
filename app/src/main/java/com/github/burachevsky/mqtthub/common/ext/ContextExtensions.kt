package com.github.burachevsky.mqtthub.common.ext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager

fun Context.getValueFromAttribute(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun Context.getStatusBarHeightFromSystemAttribute(): Int {
    val id: Int = resources
        .getIdentifier("status_bar_height", "dimen", "android")

    return if (id > 0) resources.getDimensionPixelSize(id) else 0
}


@SuppressLint("DiscouragedApi")
fun Context.getNavigationBarHeightFromSystemAttribute(): Int {
    val resName = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        "navigation_bar_height"
    } else {
        "navigation_bar_height_landscape"
    }

    val id: Int = resources.getIdentifier(resName, "dimen", "android")

    return if (id > 0) resources.getDimensionPixelSize(id) else 0
}