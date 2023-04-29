package com.github.burachevsky.mqtthub.feature.home.item.drawer

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.ext.getValueFromAttribute
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.data.entity.Dashboard as DashboardEntity
import com.github.burachevsky.mqtthub.data.entity.Broker as BrokerEntity
import com.github.burachevsky.mqtthub.databinding.ListItemDrawerMenuItemBinding

data class DrawerMenuItem(
    val text: Txt,
    val icon: Int,
    val type: Type,
    val isSelected: Boolean = false,
) : ListItem {

    val textColorAttribute: Int
        get() = when {
            isSelected -> com.google.android.material.R.attr.colorOnSecondaryContainer
            else -> com.google.android.material.R.attr.colorOnSurface
        }


    override fun layout() = LAYOUT

    override fun areItemsTheSame(that: ListItem): Boolean {
        return that is DrawerMenuItem && this.type.id == that.type.id
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_drawer_menu_item
    }

    sealed class Type(val id: Long) {

        data class Button(val buttonId: Int) : Type(buttonId.toLong())

        class Dashboard(val dashboard: DashboardEntity) : Type(dashboard.id)

        data class Broker(val broker: BrokerEntity) : Type(broker.id)
    }

    interface Listener {

        fun onClick(position: Int)
    }
}

class DrawerMenuItemViewHolder(
    itemView: View,
    private val listener: DrawerMenuItem.Listener,
) : ItemViewHolder(itemView) {

    private val binding = ListItemDrawerMenuItemBinding.bind(itemView)

    init {
        binding.item.setOnClickListener {
            listener.onClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem) {
        item as DrawerMenuItem

        val textColor = context.getValueFromAttribute(item.textColorAttribute)

        binding.label.setTextColor(textColor)
        binding.label.text = item.text.get(context)
        binding.icon.imageTintList = ColorStateList.valueOf(textColor)
        binding.icon.setImageResource(item.icon)
        binding.item.isSelected = item.isSelected
        binding.label.setTextColor(textColor)
    }
}

class DrawerMenuItemAdapter(
    private val listener: DrawerMenuItem.Listener
) : ItemAdapter {
    override fun viewType() = DrawerMenuItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return DrawerMenuItemViewHolder(inflateItemView(parent), listener)
    }
}