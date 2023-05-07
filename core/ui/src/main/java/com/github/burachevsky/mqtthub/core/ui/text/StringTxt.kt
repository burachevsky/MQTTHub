package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context
import kotlinx.parcelize.Parcelize

@Parcelize
data class StringTxt(
    private val str: String?
) : ParcelableTxt {
    override fun get(context: Context): CharSequence {
        return str ?: ""
    }
}
