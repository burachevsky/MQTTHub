package com.github.burachevsky.mqtthub.common.text

import android.content.Context
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResourceTxt(
    private val res: Int?
) : ParcelableTxt {
    override fun get(context: Context): CharSequence {
        return res?.let { context.resources.getText(res) } ?: ""
    }
}
