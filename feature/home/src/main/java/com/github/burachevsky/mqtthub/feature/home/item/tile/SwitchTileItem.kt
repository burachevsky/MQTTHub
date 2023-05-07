package com.github.burachevsky.mqtthub.feature.home.item.tile

import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.feature.home.R
import com.github.burachevsky.mqtthub.feature.home.databinding.ListItemSwitchTileBinding
import com.github.burachevsky.mqtthub.feature.home.item.APPEARANCE_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.EditMode
import com.github.burachevsky.mqtthub.feature.home.item.NAME_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.PUBLISH_TOPIC_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.SWITCH_STATE_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import com.github.burachevsky.mqtthub.feature.home.item.bindEditModeAndBackground
import com.github.burachevsky.mqtthub.feature.home.item.isChecked

data class SwitchTileItem(
    override val tile: Tile,
    override val editMode: EditMode? = null
) : TileItem {

    override fun layout() = LAYOUT

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    override fun withEditMode(editMode: EditMode?): TileItem {
        return copy(editMode = editMode)
    }

    override fun getChangePayload(that: ListItem): List<Int> {
        that as SwitchTileItem

        return listOfNotNull(
            if (tile.name != that.tile.name) NAME_CHANGED else null,
            if (tile.payload != that.tile.payload) SWITCH_STATE_CHANGED else null,
            if (tile.publishTopic != that.tile.publishTopic) PUBLISH_TOPIC_CHANGED else null,
            if (editMode != that.editMode || tile.design != that.tile.design)
                APPEARANCE_CHANGED else null,
        )
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_switch_tile
    }
}

class SwitchTileItemViewHolder(
    itemView: View,
    private val listener: TileItem.Listener
) : ItemViewHolder(itemView), OnCheckedChangeListener, OnClickListener {

    private val binding = ListItemSwitchTileBinding.bind(itemView)

    private var item: SwitchTileItem? = null

    init {
        binding.tileSwitch.setOnCheckedChangeListener(this)

        binding.tile.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }

        binding.tileSwitch.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }

        binding.editModeOverlay.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (!item?.tile?.publishTopic.isNullOrEmpty()) {
            listener.onClick(adapterPosition)
        } else {
            item?.let(::bindSwitchState)
        }
    }

    override fun onClick(v: View?) {
        listener.onClick(adapterPosition)
    }

    override fun bind(item: ListItem) {
        item as SwitchTileItem
        this.item = item

        bindName(item)
        bindSwitchState(item)
        bindPublishTopic(item)
        bindAppearance(item)
    }

    override fun bind(item: ListItem, payloads: List<Int>) {
        item as SwitchTileItem

        payloads.forEach {
            when (it) {
                NAME_CHANGED -> bindName(item)
                SWITCH_STATE_CHANGED -> bindSwitchState(item)
                APPEARANCE_CHANGED -> bindAppearance(item)
                PUBLISH_TOPIC_CHANGED -> bindPublishTopic(item)
            }
        }
    }

    private fun changeCheckedWithoutTriggering(value: Boolean) {
        binding.tileSwitch.apply {
            setOnCheckedChangeListener(null)
            isChecked = value
            setOnCheckedChangeListener(this@SwitchTileItemViewHolder)
        }
    }

    private fun bindName(item: SwitchTileItem) {
        binding.tileSwitch.text = item.tile.name
    }

    private fun bindSwitchState(item: SwitchTileItem) {
        val newState = item.isChecked()

        binding.tileSwitch.apply {
            if (isChecked != newState) {
                changeCheckedWithoutTriggering(newState)
            }
        }
    }

    private fun bindPublishTopic(item: SwitchTileItem) {
        val hasTopic = item.tile.publishTopic.isNotEmpty()

        binding.tileSwitch.apply {
            setOnClickListener(if (hasTopic) this@SwitchTileItemViewHolder else null)
            isClickable = hasTopic
        }
    }

    private fun bindAppearance(item: SwitchTileItem) {
        val tileIsFullSpan = item.tile.design.isFullSpan

        binding.tile.apply {
            layoutParams = StaggeredGridLayoutManager.LayoutParams(layoutParams).apply {
                isFullSpan = tileIsFullSpan
                binding.tileSwitch.gravity = when {
                    tileIsFullSpan -> Gravity.START or Gravity.CENTER_VERTICAL
                    else -> Gravity.CENTER
                }
            }
        }

        bindEditModeAndBackground(item)
    }
}

class SwitchTileItemAdapter(
    private val listener: TileItem.Listener
) : ItemAdapter {

    override fun viewType() = SwitchTileItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return SwitchTileItemViewHolder(inflateItemView(parent), listener)
    }
}