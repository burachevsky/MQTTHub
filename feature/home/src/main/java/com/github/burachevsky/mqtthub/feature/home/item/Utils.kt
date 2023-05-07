package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import androidx.core.view.isVisible
import com.github.burachevsky.mqtthub.core.database.entity.tile.SLIDER_MAX
import com.github.burachevsky.mqtthub.core.database.entity.tile.SLIDER_MIN
import com.github.burachevsky.mqtthub.core.database.entity.tile.SLIDER_STEP
import com.github.burachevsky.mqtthub.core.database.entity.tile.SWITCH_ON
import com.github.burachevsky.mqtthub.core.database.entity.tile.TileStyleId
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.ext.getPayload
import com.github.burachevsky.mqtthub.core.ui.ext.isState
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.SwitchTileItem
import com.github.burachevsky.mqtthub.feature.home.R as featureR

const val NAME_CHANGED = 1
const val PAYLOAD_CHANGED = 2
const val APPEARANCE_CHANGED = 3
const val SWITCH_STATE_CHANGED = 4
const val PUBLISH_TOPIC_CHANGED = 5
const val STATE_LIST_CHANGED = 6

fun ItemViewHolder.bindEditModeAndBackground(item: TileItem) {
    val editMode = item.editMode
    val tileView = itemView.findViewById<View>(featureR.id.tile)
    val overlayView = itemView.findViewById<View>(featureR.id.editModeOverlay)

    tileView.setBackgroundForStyleId(item.tile.design.styleId)

    if (editMode != null) {
        overlayView.isVisible = true
        overlayView.setBackgroundResource(
            when {
                editMode.isSelected -> R.drawable.bg_tile_edit_mode_selected
                else -> R.drawable.bg_tile_edit_mode
            }
        )
    } else {
        overlayView.isVisible = false
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

private fun View.setBackgroundForStyleId(styleId: Int) {
    setBackgroundResource(
        when (styleId){
            TileStyleId.FILLED -> R.drawable.bg_tile_list_item_filled
            TileStyleId.OUTLINED -> R.drawable.bg_tile_list_item_outlined
            else -> R.drawable.bg_tile_list_item_empty
        }
    )
}