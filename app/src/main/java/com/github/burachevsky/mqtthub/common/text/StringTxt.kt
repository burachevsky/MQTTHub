package com.github.burachevsky.mqtthub.common.text

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
