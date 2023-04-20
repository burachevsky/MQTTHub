package com.github.burachevsky.mqtthub.feature.selector

import android.os.Parcelable
import com.github.burachevsky.mqtthub.common.text.ParcelableTxt
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectorConfig(
    val title: ParcelableTxt,
    val items: List<SelectorItem>
): Parcelable
