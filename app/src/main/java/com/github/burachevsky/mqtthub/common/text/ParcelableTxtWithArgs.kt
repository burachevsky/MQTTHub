package com.github.burachevsky.mqtthub.common.text

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.github.burachevsky.mqtthub.common.ext.mapToArray

@Parcelize
class ParcelableTxtWithArgs(
    val text: ParcelableTxt,
    val args: Array<out Arg>
) : ParcelableTxt {

    override fun get(context: Context): CharSequence {
        return String.format(
            text.get(context).toString(),
            *args.mapToArray { get() }
        )
    }
}

@Parcelize
class ParcelableTxtWithUIArgs(
    val text: ParcelableTxt,
    val args: Array<out TxtArg>
) : ParcelableTxt {

    override fun get(context: Context): CharSequence {
        return String.format(
            text.get(context).toString(),
            *args.mapToArray { get().get(context) }
        )
    }
}

interface Arg : Parcelable {
    fun get(): Any
}

@Parcelize
data class NumberArg(val arg: Number) : Arg {
    override fun get() = arg
}

@Parcelize
data class StringArg(val arg: String) : Arg {
    override fun get() = arg
}

@Parcelize
data class TxtArg(val arg: ParcelableTxt): Arg {
    override fun get() = arg
}
