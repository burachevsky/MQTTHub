package com.github.burachevsky.mqtthub.feature.dashboards.item

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.github.burachevsky.mqtthub.core.database.entity.dashboard.Dashboard
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.ext.clearFocusAndHideKeyboard
import com.github.burachevsky.mqtthub.core.ui.ext.requestFocusAndShowKeyboard
import com.github.burachevsky.mqtthub.core.ui.ext.setFocus
import com.github.burachevsky.mqtthub.core.ui.ext.setOnEnterListener
import com.github.burachevsky.mqtthub.core.ui.ext.showKeyboard
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.cached
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.feature.dashboards.databinding.ListItemDashboardBinding
import com.github.burachevsky.mqtthub.feature.dashboards.R as featureR

data class DashboardItem(
    val config: ItemConfig,
    val initText: String = "",
    val initFocus: Boolean = false,
    var text: String = "",
    val dashboard: Dashboard? = null
) : ListItem {

    var initialized = false

    override fun layout() = LAYOUT

    override fun areItemsTheSame(that: ListItem): Boolean {
        return that is DashboardItem && this.dashboard?.id == that.dashboard?.id
    }

    companion object {
        val LAYOUT get() = featureR.layout.list_item_dashboard
    }

    interface Listener {

        fun onSubmitClicked(position: Int)

        fun onDeleteClicked(position: Int)
    }
}

sealed class ItemConfig(
    val placeholder: Txt,
    val showPlaceholderWhenUnfocused: Boolean,
    val icon: Int,
    val endIcon: Int?,
    val focusedIcon: Int,
    val focusedEndIcon: Int,
) {
    object CreateNew : ItemConfig(
        placeholder = Txt.of(R.string.home_create_new_dashboard).cached(),
        showPlaceholderWhenUnfocused = true,
        icon = R.drawable.ic_add,
        endIcon = null,
        focusedIcon = R.drawable.ic_close,
        focusedEndIcon = R.drawable.ic_done,
    )

    object Default : ItemConfig(
        placeholder = Txt.of(R.string.home_enter_a_dashboard_name).cached(),
        showPlaceholderWhenUnfocused = false,
        icon = R.drawable.ic_dashboard,
        endIcon = R.drawable.ic_edit,
        focusedIcon = R.drawable.ic_delete,
        focusedEndIcon = R.drawable.ic_done_primary,
    )
}

class DashboardItemViewHolder(
    itemView: View,
    private val listener: DashboardItem.Listener,
) : ItemViewHolder(itemView) {

    private val binding = ListItemDashboardBinding.bind(itemView)

    private var field: DashboardItem? = null

    init {
        binding.editText.addTextChangedListener {
            field?.text = it?.toString().orEmpty()
        }

        binding.editText.setOnEnterListener {
            submitClicked()
        }

        binding.editText.setOnFocusChangeListener { _, hasFocus ->
            field?.let(::bind)
            if (hasFocus) {
                binding.editText.apply {
                    showKeyboard()
                    setSelection(length())
                }
            }
        }

        binding.iconStart.setOnClickListener {
            val isCreateNew = field!!.config is ItemConfig.CreateNew

            if (binding.editText.isFocused) {
                binding.editText.clearFocusAndHideKeyboard()

                if (isCreateNew) {
                    binding.editText.setText("")
                } else {
                    listener.onDeleteClicked(adapterPosition)
                }
            } else if (isCreateNew) {
                binding.editText.requestFocusAndShowKeyboard()
            }
        }

        binding.iconEnd.setOnClickListener {
            submitClicked()
        }
    }

    private fun submitClicked() {
        if (binding.editText.isFocused) {
            binding.editText.clearFocusAndHideKeyboard()
            listener.onSubmitClicked(adapterPosition)
            if (field!!.config is ItemConfig.CreateNew) {
                binding.editText.setText("")
            }
        } else {
            binding.editText.requestFocusAndShowKeyboard()
        }
    }

    override fun bind(item: ListItem) {
        item as DashboardItem
        this.field = item

        if (!item.initialized) {
            item.initialized = true

            binding.editText.setText(
                item.initText.ifEmpty { item.text }
            )
            if (item.initFocus) {
                binding.editText.setFocus(true)
            }
        } else {
            binding.editText.setText(item.text)
        }

        val isFocused = binding.editText.isFocused

        binding.dividerTop.isVisible = isFocused
        binding.dividerBottom.isVisible = isFocused

        binding.editText.hint = when {
            isFocused || item.config.showPlaceholderWhenUnfocused -> {
                item.config.placeholder.get(context)
            }

            else -> null
        }

        binding.iconStart.setImageResource(
            when {
                isFocused -> item.config.focusedIcon
                else -> item.config.icon
            }
        )

        binding.iconEnd.setImageResource(
            when {
                isFocused -> item.config.focusedEndIcon
                else -> item.config.endIcon ?: 0
            }
        )
    }
}

class DashboardItemAdapter(
    private val listener: DashboardItem.Listener,
) : ItemAdapter {

    override fun viewType() = DashboardItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return DashboardItemViewHolder(inflateItemView(parent), listener)
    }
}
