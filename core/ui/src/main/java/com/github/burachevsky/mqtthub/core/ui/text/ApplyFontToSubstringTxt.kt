package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.core.content.res.ResourcesCompat
import com.github.burachevsky.mqtthub.core.ui.text.span.CustomTypefaceSpan

data class ApplyFontToSubstringTxt(
    private val text: Txt,
    private val substring: String,
    private val fontRes: Int,
) : Txt {

    override fun get(context: Context): CharSequence {
        val charSequence = text.get(context)
        val startIndex = charSequence.indexOf(substring)
        val endIndex = startIndex + substring.length

        if (startIndex < 0)
            return charSequence

        return ResourcesCompat.getFont(context, fontRes)?.let { typeface ->
            val typefaceSpan = CustomTypefaceSpan(typeface)

            SpannableStringBuilder(charSequence).apply {
                setSpan(typefaceSpan, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        } ?: charSequence
    }
}
