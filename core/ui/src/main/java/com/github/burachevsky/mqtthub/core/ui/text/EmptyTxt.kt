package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context
import kotlinx.parcelize.Parcelize

@Parcelize
object EmptyTxt : ParcelableTxt {
    override fun get(context: Context): CharSequence {
        return ""
    }
}