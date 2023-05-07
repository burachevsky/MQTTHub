package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context

class ListTxt(
    private val list: List<Txt>,
    private val separator: String = ", "
): Txt {
    override fun get(context: Context): CharSequence {
        return list.joinToString(separator) { it.get(context) }
    }
}