package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context
import androidx.core.text.HtmlCompat

data class HtmlTxt(
    private val text: Txt
) : Txt {
    override fun get(context: Context): CharSequence {
        return HtmlCompat.fromHtml(
            text.get(context).toString(),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }
}
