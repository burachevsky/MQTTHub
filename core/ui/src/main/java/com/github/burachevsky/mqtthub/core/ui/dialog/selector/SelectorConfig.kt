package com.github.burachevsky.mqtthub.core.ui.dialog.selector

import android.os.Parcelable
import com.github.burachevsky.mqtthub.core.ui.text.ParcelableTxt
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectorConfig(
    val title: ParcelableTxt? = null,
    val items: List<SelectorItem>
): Parcelable
