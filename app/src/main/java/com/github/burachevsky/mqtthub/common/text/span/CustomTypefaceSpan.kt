package com.github.burachevsky.mqtthub.common.text.span

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

class CustomTypefaceSpan(
    private val customTypeface: Typeface
) : TypefaceSpan(null) {

    override fun updateDrawState(ds: TextPaint) {
        ds.typeface = customTypeface
    }

    override fun updateMeasureState(paint: TextPaint) {
        paint.typeface = customTypeface
    }
}