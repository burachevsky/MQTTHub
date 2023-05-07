package com.github.burachevsky.mqtthub.core.ui.text.span

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class ClickableColorSpan(
    private val color: Int,
    private val drawUnderline: Boolean = false,
    private val handler: () -> Unit,
) : ClickableSpan() {
    override fun updateDrawState(ds: TextPaint) {
        ds.color = color

        if (drawUnderline) {
            super.updateDrawState(ds)
        } else {
            ds.isUnderlineText = false
        }
    }

    override fun onClick(p0: View) {
        handler()
    }
}
