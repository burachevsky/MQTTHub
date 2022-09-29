package com.github.burachevsky.mqtthub.common.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

class TxtWithHints(
    private val text: Txt,
    private val hintCharacters: String,
    @ColorRes private val hintColorRes: Int
) : Txt {
    override fun get(context: Context): CharSequence {
        val chars = text.get(context)

        return SpannableString(chars).apply {
            for (i in chars.indices) {
                if (hintCharacters.contains(chars[i])) {
                    setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(context, hintColorRes)
                        ),
                        i,
                        i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }
}