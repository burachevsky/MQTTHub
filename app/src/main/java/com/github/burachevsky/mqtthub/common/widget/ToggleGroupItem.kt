package com.github.burachevsky.mqtthub.common.widget

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.databinding.ListItemToggleGroupBinding
import com.github.burachevsky.mqtthub.databinding.ToggleGroupButtonOutlinedBinding
import com.google.android.material.button.MaterialButtonToggleGroup

data class ToggleGroupItem(
    val id: Int = 0,
    val title: Txt,
    val options: List<ToggleOption>,
    var selectedValue: Int,
    val marginTopRes: Int = R.dimen.switch_item_margin_top_default,
    val isVertical: Boolean = true,
) : ListItem {

    override fun layout() = LAYOUT

    companion object {
        val LAYOUT get() = R.layout.list_item_toggle_group
    }

    interface Listener {
        fun onSelectionChanged(position: Int)
    }
}

data class ToggleOption(
    val id: Int,
    val text: Txt,
)

class ToggleGroupItemItemViewHolder(
    itemView: View,
    private val listener: ToggleGroupItem.Listener?,
) : ItemViewHolder(itemView), MaterialButtonToggleGroup.OnButtonCheckedListener {

    private val binding = ListItemToggleGroupBinding.bind(itemView)

    private var item: ToggleGroupItem? = null

    init {
        binding.toggleGroup.addOnButtonCheckedListener(this)
    }

    override fun onButtonChecked(
        group: MaterialButtonToggleGroup?,
        checkedId: Int,
        isChecked: Boolean
    ) {
        if (isChecked) {
            item?.selectedValue = checkedId
            listener?.onSelectionChanged(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        binding.toggleGroup.removeOnButtonCheckedListener(this)

        item as ToggleGroupItem
        this.item = item

        binding.container.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = context.resources.getDimensionPixelSize(item.marginTopRes)
        }

        binding.toggleGroup.updateLayoutParams<FrameLayout.LayoutParams> {
            gravity = if (item.isVertical) Gravity.START else  Gravity.END
        }

        binding.toggleGroupContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = when {
                item.isVertical ->
                    context.resources.getDimensionPixelSize(R.dimen.toggle_group_spacing)

                else -> 0
            }
        }

        binding.title.updateLayoutParams<LinearLayout.LayoutParams> {
            gravity = if (item.isVertical) Gravity.START else Gravity.CENTER_VERTICAL
        }

        binding.container.orientation = when {
            item.isVertical -> LinearLayout.VERTICAL
            else -> LinearLayout.HORIZONTAL
        }

        binding.title.text = item.title.get(context)

        val inflater = LayoutInflater.from(context)

        val toggleViews = item.options.map { option ->
            val buttonBinding = ToggleGroupButtonOutlinedBinding
                .inflate(inflater, binding.toggleGroup, false)

            buttonBinding.toggleButton.apply {
                text = option.text.get(context)
                id = option.id
            }
        }

        binding.toggleGroup.apply {
            removeAllViews()
            toggleViews.forEachIndexed { i, it -> addView(it, i)}
            check(item.selectedValue)
        }

        binding.toggleGroup.addOnButtonCheckedListener(this)
    }
}

class ToggleGroupItemItemAdapter(
    private val listener: ToggleGroupItem.Listener? = null
) : ItemAdapter {

    override fun viewType() = ToggleGroupItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return ToggleGroupItemItemViewHolder(inflateItemView(parent), listener)
    }
}