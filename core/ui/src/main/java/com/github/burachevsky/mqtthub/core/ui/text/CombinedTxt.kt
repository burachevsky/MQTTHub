package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context

data class CombinedTxt(
    val left: Txt,
    val right: Txt
) : Txt {
    override fun get(context: Context): CharSequence {
        return left.get(context).toString() + right.get(context).toString()
    }
}
