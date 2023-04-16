package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import android.view.ViewGroup
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.databinding.ListItemDrawerLabelBinding

data class DrawerLabelItem(
    val text: Txt,
    val buttonText: Txt? = null,
    val onClick: (() -> Unit)? = null,
) : ListItem {
    override fun layout() = LAYOUT
    
    companion object {
        val LAYOUT get() = R.layout.list_item_drawer_label
    }
}

class DrawerLabelItemViewHolder(itemView: View) : ItemViewHolder(itemView) {
    
    private val binding = ListItemDrawerLabelBinding.bind(itemView)

    private var item: DrawerLabelItem? = null

    init {
        binding.labelButton.setOnClickListener {
            item?.onClick?.invoke()
        }
    }
    
    override fun bind(item: ListItem) {
        item as DrawerLabelItem
        this.item = item
        
        binding.label.text = item.text.get(context)
        binding.labelButton.text = item.buttonText?.get(context)
    }
}

class DrawerLabelItemAdapter : ItemAdapter {
    override fun viewType() = DrawerLabelItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return DrawerLabelItemViewHolder(inflateItemView(parent))
    }
}