package com.github.burachevsky.mqtthub.common.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.databinding.ListItemToggleGroupBinding
import com.github.burachevsky.mqtthub.databinding.ToggleGroupButtonOutlinedBinding

data class ToggleGroupItem(
    val title: Txt,
    val options: List<ToggleOption>,
    var selectedValue: Int
) : ListItem {

    override fun layout() = LAYOUT

    companion object {
        val LAYOUT get() = R.layout.list_item_toggle_group
    }
}

data class ToggleOption(
    val id: Int,
    val text: Txt,
)

class ToggleGroupItemItemViewHolder(itemView: View) : ItemViewHolder(itemView) {

    private val binding = ListItemToggleGroupBinding.bind(itemView)

    private var item: ToggleGroupItem? = null

    init {
        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                item?.selectedValue = checkedId
            }
        }
    }

    override fun bind(item: ListItem) {
        item as ToggleGroupItem
        this.item = item

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
    }
}

class ToggleGroupItemItemAdapter() : ItemAdapter {

    override fun viewType() = ToggleGroupItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return ToggleGroupItemItemViewHolder(inflateItemView(parent))
    }
}