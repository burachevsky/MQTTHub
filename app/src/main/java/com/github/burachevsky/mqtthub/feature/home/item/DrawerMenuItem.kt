package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.ext.getValueFromAttribute
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.databinding.ListItemDrawerMenuItemBinding

data class DrawerMenuItem(
    val text: Txt,
    val icon: Int,
    val isSelected: Boolean = false,
    val onClick: (() -> Unit)? = null,
) : ListItem {

    val textColorAttribute: Int
        get() = when {
            isSelected -> com.google.android.material.R.attr.colorOnSecondaryContainer
            else -> com.google.android.material.R.attr.colorOnSurfaceVariant
        }


    override fun layout() = LAYOUT

    companion object {
        val LAYOUT get() = R.layout.list_item_drawer_menu_item
    }
}

class DrawerMenuItemViewHolder(itemView: View) : ItemViewHolder(itemView) {

    private val binding = ListItemDrawerMenuItemBinding.bind(itemView)

    private var item: DrawerMenuItem? = null

    init {
        binding.item.setOnClickListener {
            item?.onClick?.invoke()
        }
    }

    override fun bind(item: ListItem) {
        item as DrawerMenuItem
        this.item = item

        binding.label.text = item.text.get(context)
        binding.icon.setImageResource(item.icon)
        binding.item.isSelected = item.isSelected
        binding.label.setTextColor(context.getValueFromAttribute(item.textColorAttribute))
    }
}

class DrawerMenuItemAdapter : ItemAdapter {
    override fun viewType() = DrawerMenuItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return DrawerMenuItemViewHolder(inflateItemView(parent))
    }
}