package com.github.burachevsky.mqtthub.core.ui.dialog.entertext

import android.os.Parcelable
import com.github.burachevsky.mqtthub.core.ui.text.ParcelableTxt
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnterTextConfig(
    val actionId: Int,
    val title: ParcelableTxt,
    val initText: ParcelableTxt?,
): Parcelable
