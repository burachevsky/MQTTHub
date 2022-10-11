package com.github.burachevsky.mqtthub.common.ext

import android.view.KeyEvent
import android.widget.EditText

fun EditText.showKeyboard() {
    context.inputMethodManager.showSoftInput(this, 0)
}

fun EditText.hideKeyboard() {
    context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.setOnEnterListener(onEnter: () -> Unit) {
    setOnKeyListener { v, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            onEnter()
            return@setOnKeyListener true
        }
        false
    }
}