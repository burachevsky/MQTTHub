package com.github.burachevsky.mqtthub.core.ui.text

import android.content.Context

/**
 * Represents text that appears in ui.
 * @see [Txt.Companion.of]
 */
interface Txt {

    /**
     * @return [CharSequence] value of [Txt]
     */
    fun get(context: Context): CharSequence

    companion object
}