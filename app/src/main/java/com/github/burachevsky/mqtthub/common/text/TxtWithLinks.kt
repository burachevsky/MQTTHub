package com.github.burachevsky.mqtthub.common.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import androidx.core.content.ContextCompat
import com.github.burachevsky.mqtthub.common.text.span.ClickableColorSpan


data class UILink(
    val text: String,
    val onClick: () -> Unit,
    val parentText: String,
    val startPosOfParentText: Int,
    val endPosOfParentText: Int,
)

class TxtWithLinks(
    val text: String,
    val colorRes: Int,
    val links: List<UILink>
) : Txt {
    override fun get(context: Context): CharSequence {
        val spanableString = SpannableString(text)

        links.forEach {
            spanableString.setSpan(
                ClickableColorSpan(
                    color = ContextCompat.getColor(context, colorRes),
                    handler = it.onClick
                ),
                it.startPosOfParentText,
                it.endPosOfParentText,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spanableString
    }
}
