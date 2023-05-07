package com.github.burachevsky.mqtthub.core.ui.widget

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CompoundButton
import androidx.core.view.updateLayoutParams
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.databinding.ListItemSwitchBinding
import com.github.burachevsky.mqtthub.core.ui.text.Txt

data class SwitchItem(
    val text: Txt,
    var isChecked: Boolean = false,
    val marginTopRes: Int = R.dimen.switch_item_margin_top_default,
    val onCheckChanged: ((Boolean) -> Unit)? = null
) : ListItem {
    override fun layout() = LAYOUT

    override fun areItemsTheSame(that: ListItem): Boolean {
        return this == that
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_switch
    }
}

class SwitchItemViewHolder(
    itemView: View
) : ItemViewHolder(itemView), CompoundButton.OnCheckedChangeListener {

    private val binding = ListItemSwitchBinding.bind(itemView)

    private var item: SwitchItem? = null

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        item?.isChecked = isChecked
        item?.onCheckChanged?.invoke(isChecked)
    }

    override fun bind(item: ListItem) {
        binding.switchItem.setOnCheckedChangeListener(null)

        item as SwitchItem
        this.item = item
        binding.switchItem.text = item.text.get(context)
        binding.switchItem.isChecked = item.isChecked
        binding.switchItem.updateLayoutParams<MarginLayoutParams> {
            topMargin = context.resources.getDimensionPixelSize(item.marginTopRes)
        }

        binding.switchItem.setOnCheckedChangeListener(this)
    }
}

class SwitchItemAdapter() : ItemAdapter {

    override fun viewType() = SwitchItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return SwitchItemViewHolder(inflateItemView(parent))
    }
}
