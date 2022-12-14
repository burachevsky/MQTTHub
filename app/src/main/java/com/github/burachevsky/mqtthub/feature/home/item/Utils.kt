package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import androidx.core.view.isVisible
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.ext.showPopupMenu
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder

const val NAME_CHANGED = 1
const val PAYLOAD_CHANGED = 2
const val EDIT_MODE_CHANGED = 3
const val SWITCH_STATE_CHANGED = 4

fun ItemViewHolder.bindEditMode(editMode: EditMode?) {
    itemView.findViewById<View>(R.id.editModeOverlay)?.apply {
        if (editMode != null) {
            isVisible = true
            setBackgroundResource(
                when {
                    editMode.isSelected -> R.drawable.bg_tile_edit_mode_selected
                    else -> R.drawable.bg_tile_edit_mode
                }
            )
        } else {
            isVisible = false
        }
    }
}