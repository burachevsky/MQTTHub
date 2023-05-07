package com.github.burachevsky.mqtthub.core.ui.text

import androidx.annotation.ColorRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

fun Txt.Companion.of(@StringRes res: Int?): ParcelableTxt = ResourceTxt(res)

fun Txt.Companion.of(str: String?): ParcelableTxt = StringTxt(str)

fun Txt.Companion.of(
    @PluralsRes pluralsRes: Int,
    quantity: Int
) = PluralsTxt(
    pluralsRes = pluralsRes,
    quantity = quantity
)

fun Txt.Companion.fromListTxt(
    list: List<Txt>,
    separator: String = ", "
): Txt = ListTxt(list, separator)

val Txt.Companion.empty: ParcelableTxt get() = EmptyTxt

fun Txt.parseHtml(): Txt = HtmlTxt(this)

fun Txt.withArgs(vararg args: Any): Txt = TxtWithArgs(this, args)

fun Txt.withArgs(vararg args: Txt): Txt = TxtWithTxtArgs(this, args)

fun ParcelableTxt.withParcelableArgs(vararg args: Arg) = ParcelableTxtWithArgs(this, args)

fun ParcelableTxt.withParcelableTxtArgs(vararg args: TxtArg) = ParcelableTxtWithUIArgs(this, args)

fun String?.asTxt() = StringTxt(this)

fun Int?.asTxt() = ResourceTxt(this)

fun Txt.withoutUnderlines(): Txt = TxtWithoutUnderlines(this)

fun Txt.cached(): Txt = CachedTxt(this)

fun Txt.withHints(
    hintCharacters: String,
    @ColorRes hintColorRes: Int
): Txt = TxtWithHints(this, hintCharacters, hintColorRes)

fun Txt.append(that: Txt): Txt = CombinedTxt(this, that)

fun ParcelableTxt.appendParcelable(that: ParcelableTxt) = ParcelableCombinedTxt(this, that)

fun Txt.applyFont(
    substring: String,
    fontRes: Int
): Txt =
    com.github.burachevsky.mqtthub.core.ui.text.ApplyFontToSubstringTxt(this, substring, fontRes)

fun Txt.paintSubstring(
    substring: String,
    colorRes: Int,
    lastSubstring: Boolean = false,
): Txt = PaintSubstringTxt(this, substring.asTxt(), colorRes, lastSubstring)

fun Txt.paintSubstring(
    substring: Txt,
    colorRes: Int,
    lastSubstring: Boolean = false,
): Txt = PaintSubstringTxt(this, substring, colorRes, lastSubstring)

fun Txt.paint(
    colorRes: Int
): Txt = PaintSubstringTxt(text = this, substring = null, colorRes)