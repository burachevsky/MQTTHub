package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import androidx.core.view.isVisible
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.constant.SLIDER_MAX
import com.github.burachevsky.mqtthub.common.constant.SLIDER_MIN
import com.github.burachevsky.mqtthub.common.constant.SLIDER_STEP
import com.github.burachevsky.mqtthub.common.constant.SWITCH_ON
import com.github.burachevsky.mqtthub.common.ext.getPayload
import com.github.burachevsky.mqtthub.common.ext.isState
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.SwitchTileItem

const val NAME_CHANGED = 1
const val PAYLOAD_CHANGED = 2
const val EDIT_MODE_CHANGED = 3
const val SWITCH_STATE_CHANGED = 4
const val DESIGN_CHANGED = 5
const val PUBLISH_TOPIC_CHANGED = 6
const val STATE_LIST_CHANGED = 7

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

fun SwitchTileItem.isChecked(): Boolean {
    return tile.isState(SWITCH_ON)
}

fun SliderTileItem.sliderMin(): Float {
    return tile.stateList.getPayload(SLIDER_MIN)?.toFloatOrNull() ?: 0f
}

fun SliderTileItem.sliderMax(): Float {
    return tile.stateList.getPayload(SLIDER_MAX)?.toFloatOrNull() ?: 100f
}

fun SliderTileItem.sliderStep(): Float {
    return tile.stateList.getPayload(SLIDER_STEP)?.toFloatOrNull() ?: 0f
}