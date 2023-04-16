package com.github.burachevsky.mqtthub.common.ext

import android.view.KeyEvent
import android.widget.EditText
import androidx.core.view.postDelayed

fun EditText.showKeyboard() {
    context.inputMethodManager.showSoftInput(this, 0)
}

fun EditText.hideKeyboard() {
    context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.clearFocusAndHideKeyboard() {
    clearFocus()
    hideKeyboard()
}

fun EditText.requestFocusAndShowKeyboard() {
    showKeyboard()
    requestFocus()
}

fun EditText.setFocus(isFocus: Boolean) {
    postDelayed(0){
        if (isFocus) {
            requestFocus()
            showKeyboard()
        }
    }
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