package com.github.burachevsky.mqtthub.feature.home.item.tile

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.databinding.ListItemSliderTileBinding
import com.github.burachevsky.mqtthub.feature.home.item.DESIGN_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.EDIT_MODE_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.EditMode
import com.github.burachevsky.mqtthub.feature.home.item.NAME_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.PAYLOAD_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.PUBLISH_TOPIC_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.STATE_LIST_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import com.github.burachevsky.mqtthub.feature.home.item.bindEditMode
import com.github.burachevsky.mqtthub.feature.home.item.setBackgroundForStyleId
import com.github.burachevsky.mqtthub.feature.home.item.sliderMax
import com.github.burachevsky.mqtthub.feature.home.item.sliderMin
import com.github.burachevsky.mqtthub.feature.home.item.sliderStep
import com.google.android.material.slider.Slider

data class SliderTileItem(
    override val tile: Tile,
    override val editMode: EditMode? = null
) : TileItem {

    override fun layout() = LAYOUT

    override fun getChangePayload(that: ListItem): List<Int> {
        that as SliderTileItem

        return listOfNotNull(
            if (tile.name != that.tile.name) NAME_CHANGED else null,
            if (tile.stateList != that.tile.stateList) STATE_LIST_CHANGED else null,
            if (tile.publishTopic != that.tile.publishTopic) PUBLISH_TOPIC_CHANGED else null,
            if (tile.payload != that.tile.payload) PAYLOAD_CHANGED else null,
            if (editMode != that.editMode) EDIT_MODE_CHANGED else null,
            if (tile.design != that.tile.design) DESIGN_CHANGED else null,
        )
    }

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    override fun withEditMode(editMode: EditMode?): TileItem {
        return copy(editMode = editMode)
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_slider_tile
    }

    interface Listener : TileItem.Listener {

        fun sliderValueChanged(position: Int, value: Float)
    }
}

class SliderTileItemViewHolder(
    itemView: View,
    private val listener: SliderTileItem.Listener,
) : ItemViewHolder(itemView), Slider.OnSliderTouchListener {

    private val binding = ListItemSliderTileBinding.bind(itemView)

    private var sliderIsEnabled = true
    private var currentPayload = 0f

    init {
        binding.editModeOverlay.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }

        binding.slider.addOnSliderTouchListener(this)
    }

    override fun onStartTrackingTouch(slider: Slider) {
        if (!sliderIsEnabled) {
            slider.value = currentPayload
        }
    }

    override fun onStopTrackingTouch(slider: Slider) {
        if (sliderIsEnabled) {
            listener.sliderValueChanged(adapterPosition, slider.value)
        }
    }

    override fun bind(item: ListItem, payloads: List<Int>) {
        item as SliderTileItem

        payloads.forEach {
            when (it) {
                NAME_CHANGED -> bindTileName(item)
                STATE_LIST_CHANGED -> bindSliderParams(item)
                PUBLISH_TOPIC_CHANGED -> bindPublishTopic(item)
                PAYLOAD_CHANGED -> bindPayload(item)
                EDIT_MODE_CHANGED -> bindEditMode(item.editMode)
                DESIGN_CHANGED -> bindDesign(item)
            }
        }
    }

    override fun bind(item: ListItem) {
        item as SliderTileItem

        bindTileName(item)
        bindSliderParams(item)
        bindPublishTopic(item)
        bindPayload(item)
        bindEditMode(item.editMode)
        bindDesign(item)
    }

    private fun bindTileName(item: SliderTileItem) {
        binding.tileName.text = item.tile.name
    }

    private fun bindSliderParams(item: SliderTileItem) {
        binding.slider.apply {
            valueFrom = item.sliderMin()
            valueTo = item.sliderMax()
            stepSize = item.sliderStep()
        }
    }

    private fun bindPublishTopic(item: SliderTileItem) {
        sliderIsEnabled = item.tile.publishTopic.isNotEmpty()
    }

    private fun bindPayload(item: SliderTileItem) {
        val payload = item.tile.payload.toFloatOrNull() ?: return

        binding.slider.apply {
            if (payload in valueFrom..valueTo) {
                value = payload
                currentPayload = payload
            }
        }
    }

    private fun bindDesign(item: SliderTileItem) {
        binding.tile.apply {
            setBackgroundForStyleId(item.tile.design.styleId)

            layoutParams = StaggeredGridLayoutManager.LayoutParams(layoutParams).apply {
                isFullSpan = item.tile.design.isFullSpan
            }
        }
    }
}

class SliderTileItemAdapter(
    private val listener: SliderTileItem.Listener
) : ItemAdapter {

    override fun viewType() = SliderTileItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return SliderTileItemViewHolder(inflateItemView(parent), listener)
    }
}