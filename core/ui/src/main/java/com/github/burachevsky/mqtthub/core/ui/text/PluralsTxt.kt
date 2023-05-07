package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context
import androidx.annotation.PluralsRes
import kotlinx.parcelize.Parcelize

@Parcelize
class PluralsTxt(
    private val quantity: Int,
    @PluralsRes private val pluralsRes: Int
) : ParcelableTxt {

    override fun get(context: Context): CharSequence {
        return context.resources.getQuantityString(pluralsRes, quantity)
    }
}