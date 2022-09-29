package com.github.burachevsky.mqtthub.common.text

import android.content.Context
import kotlinx.parcelize.Parcelize

@Parcelize
object EmptyTxt : ParcelableTxt {
    override fun get(context: Context): CharSequence {
        return ""
    }
}