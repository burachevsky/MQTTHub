package com.github.burachevsky.mqtthub.common.text

import android.content.Context
import android.text.Spannable
import android.text.TextPaint
import android.text.style.URLSpan

data class TxtWithoutUnderlines(
    private val text: Txt
) : Txt {

    override fun get(context: Context): CharSequence {
        return transform(text.get(context))
    }

    companion object {
        fun transform(charSequence: CharSequence): CharSequence {
            return when (charSequence) {
                is Spannable -> charSequence.apply {
                    val spans = getSpans(0, length, URLSpan::class.java)

                    for (span in spans) {
                        val start = getSpanStart(span)
                        val end = getSpanEnd(span)
                        removeSpan(span)
                        setSpan(
                            object : URLSpan(span.url) {
                                override fun updateDrawState(ds: TextPaint) {
                                    super.updateDrawState(ds)
                                    ds.isUnderlineText = false
                                }
                            },
                            start, end, 0
                        )
                    }
                }

                else -> charSequence
            }
        }
    }
}
