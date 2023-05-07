package com.github.burachevsky.mqtthub.feature.home.drawer.item

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.core.database.entity.broker.Broker
import com.github.burachevsky.mqtthub.core.database.entity.dashboard.Dashboard
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.ext.getValueFromAttribute
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.feature.home.databinding.ListItemDrawerMenuItemBinding
import com.github.burachevsky.mqtthub.core.database.entity.broker.Broker as BrokerEntity
import com.github.burachevsky.mqtthub.core.database.entity.dashboard.Dashboard as DashboardEntity
import com.github.burachevsky.mqtthub.feature.home.R as featureR

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
        val LAYOUT get() = featureR.layout.list_item_drawer_menu_item

        fun map(broker: Broker): DrawerMenuItem {
            return DrawerMenuItem(
                Txt.of(broker.name),
                R.drawable.ic_broker,
                type = Type.Broker(broker),
            )
        }

        fun map(dashboard: Dashboard): DrawerMenuItem {
            return DrawerMenuItem(
                Txt.of(dashboard.name),
                R.drawable.ic_dashboard,
                type = Type.Dashboard(dashboard),
            )
        }
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