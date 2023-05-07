package com.github.burachevsky.mqtthub.feature.home.item.tile

import android.view.View
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.feature.home.R
import com.github.burachevsky.mqtthub.feature.home.databinding.ListItemSliderTileBinding
import com.github.burachevsky.mqtthub.feature.home.item.APPEARANCE_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.EditMode
import com.github.burachevsky.mqtthub.feature.home.item.NAME_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.PAYLOAD_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.PUBLISH_TOPIC_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.STATE_LIST_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import com.github.burachevsky.mqtthub.feature.home.item.bindEditModeAndBackground
import com.github.burachevsky.mqtthub.feature.home.item.sliderMax
import com.github.burachevsky.mqtthub.feature.home.item.sliderMin
import com.github.burachevsky.mqtthub.feature.home.item.sliderStep
import com.google.android.material.slider.LabelFormatter
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
            if (editMode != that.editMode || tile.design != that.tile.design)
                APPEARANCE_CHANGED else null,
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
    private var isShowingLabelForDisabledSlider = false

    init {
        binding.tile.setOnClickListener {
            if (!sliderIsEnabled) {
                binding.slider.run {
                    if (isShowingLabelForDisabledSlider) {
                        labelBehavior = LabelFormatter.LABEL_GONE
                        isShowingLabelForDisabledSlider = false
                    } else {
                        labelBehavior = LabelFormatter.LABEL_VISIBLE
                        isShowingLabelForDisabledSlider = true

                        postDelayed(2000) {
                            if (isShowingLabelForDisabledSlider) {
                                labelBehavior = LabelFormatter.LABEL_GONE
                                isShowingLabelForDisabledSlider = false
                            }
                        }
                    }

                }
            }
        }

        binding.tile.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }

        binding.editModeOverlay.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }

        binding.slider.addOnSliderTouchListener(this)
    }

    override fun onStartTrackingTouch(slider: Slider) {}

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
                APPEARANCE_CHANGED -> bindAppearance(item)
            }
        }
    }

    override fun bind(item: ListItem) {
        item as SliderTileItem

        bindTileName(item)
        bindSliderParams(item)
        bindPublishTopic(item)
        bindPayload(item)
        bindAppearance(item)
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
        binding.slider.isEnabled = sliderIsEnabled
        if (sliderIsEnabled) {
            binding.slider.labelBehavior = LabelFormatter.LABEL_FLOATING
        }
    }

    private fun bindPayload(item: SliderTileItem) {
        val payload = item.tile.payload.stringValue.toFloatOrNull() ?: return

        binding.slider.apply {
            if (payload in valueFrom..valueTo) {
                value = payload
                currentPayload = payload
            }
        }
    }

    private fun bindAppearance(item: SliderTileItem) {
        binding.tile.apply {
            layoutParams = StaggeredGridLayoutManager.LayoutParams(layoutParams).apply {
                isFullSpan = item.tile.design.isFullSpan
            }
        }

        bindEditModeAndBackground(item)
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