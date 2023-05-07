package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat

data class PaintSubstringTxt(
    val text: Txt,
    val substring: Txt?,
    val colorRes: Int,
    val lastSubstring: Boolean = false
) : Txt {

    override fun get(context: Context): CharSequence {
        val charSequence = text.get(context)

        val substr = substring?.get(context)?.toString()

        val startIndex = substr?.let {
            if (lastSubstring) charSequence.lastIndexOf(substr, ignoreCase = true)
            else charSequence.indexOf(substr, ignoreCase = true)
        } ?: 0

        val endIndex = substr?.let {
            startIndex + substr.length
        } ?: charSequence.lastIndex

        if (startIndex < 0)
            return charSequence

        return SpannableString(charSequence).apply {
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(context, colorRes),
                ),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}
