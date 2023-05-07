package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableCombinedTxt(
    val left: ParcelableTxt,
    val right: ParcelableTxt
) : ParcelableTxt {
    override fun get(context: Context): CharSequence {
        val left = left.get(context).toString()
        val right = right.get(context).toString()
        return left + right
    }
}

