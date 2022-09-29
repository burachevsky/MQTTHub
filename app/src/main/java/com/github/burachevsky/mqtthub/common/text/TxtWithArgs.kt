package com.github.burachevsky.mqtthub.common.text

import android.content.Context
import com.github.burachevsky.mqtthub.common.ext.mapToArray

open class TxtWithArgs constructor(
    private val text: Txt,
    private val args: Array<out Any>
): Txt {

    override fun get(context: Context): CharSequence {
        return String.format(
            text.get(context).toString(),
            *args.mapToArray {
                when (this) {
                    is Txt -> get(context)
                    else -> this
                }
            }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TxtWithArgs) return false

        if (text != other.text) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}

class TxtWithTxtArgs(
    private val text: Txt,
    private val args: Array<out Txt>
): TxtWithArgs(text, args) {

    override fun get(context: Context): CharSequence {
        val argsFromTxt = args.map { it.get(context) }.toTypedArray()
        return String.format(text.get(context).toString(), *argsFromTxt)
    }
}