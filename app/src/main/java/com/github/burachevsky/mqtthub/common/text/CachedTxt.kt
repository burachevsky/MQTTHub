package com.github.burachevsky.mqtthub.common.text

import android.content.Context

data class CachedTxt(
    private val text: Txt
) : Txt {
    private var cachedValue: CharSequence? = null

    override fun get(context: Context): CharSequence {
        return cachedValue ?: text.get(context).also { cachedValue = it }
    }
}
