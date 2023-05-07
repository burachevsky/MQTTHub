package com.github.burachevsky.mqtthub.feature.home.drawer.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.feature.home.R
import com.github.burachevsky.mqtthub.feature.home.databinding.ListItemDrawerLabelBinding

data class DrawerLabelItem(
    val id: Int,
    val text: Txt,
    val buttonText: Txt? = null,
) : ListItem {
    override fun layout() = LAYOUT

    override fun areItemsTheSame(that: ListItem): Boolean {
        return that is DrawerLabelItem && this.id == that.id
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_drawer_label
    }

    interface Listener {

        fun onClick(position: Int)
    }
}

class DrawerLabelItemViewHolder(
    itemView: View,
    private val listener: DrawerLabelItem.Listener,
) : ItemViewHolder(itemView) {
    
    private val binding = ListItemDrawerLabelBinding.bind(itemView)

    init {
        binding.labelButton.setOnClickListener {
            listener.onClick(adapterPosition)
        }
    }
    
    override fun bind(item: ListItem) {
        item as DrawerLabelItem
        
        binding.label.text = item.text.get(context)
        binding.labelButton.text = item.buttonText?.get(context)
    }
}

class DrawerLabelItemAdapter(
    private val listener: DrawerLabelItem.Listener
) : ItemAdapter {
    override fun viewType() = DrawerLabelItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return DrawerLabelItemViewHolder(inflateItemView(parent), listener)
    }
}