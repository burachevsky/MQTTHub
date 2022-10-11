package com.github.burachevsky.mqtthub.common.widget

import android.text.InputType.*
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.View.FOCUS_FORWARD
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.addTextChangedListener
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.empty
import com.github.burachevsky.mqtthub.databinding.ListItemInputFieldBinding


data class InputFieldItem(
    val initText: Txt = Txt.empty,
    var text: String = "",
    val label: Txt? = null,
    val placeholder: Txt? = null,
    val type: FieldType = FieldType.DEFAULT,
) : ListItem {

    var initialized = false

    override fun layout() = LAYOUT

    companion object {
        const val LAYOUT = R.layout.list_item_input_field
    }
}

enum class FieldType {
    DEFAULT, NUMBER, URI,
}

class InputFieldItemViewHolder(itemView: View) : ItemViewHolder(itemView) {

    private val binding = ListItemInputFieldBinding.bind(itemView)

    private var field: InputFieldItem? = null

    init {
        binding.inputFieldEditText.addTextChangedListener {
            field?.text = it?.toString().orEmpty()
        }

        binding.inputFieldEditText.setOnEditorActionListener(
            OnEditorActionListener { textView, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    val view = textView.focusSearch(FOCUS_DOWN)

                    if (view != null) {
                        if (!view.requestFocus(FOCUS_FORWARD)) {
                            return@OnEditorActionListener true
                        }
                    }
                    return@OnEditorActionListener false
                }
                binding.inputFieldEditText.clearFocus()
                false
            }
        )
    }

    override fun bind(item: ListItem) {
        item as InputFieldItem
        field = item

        if (!item.initialized) {
            item.initialized = true
            binding.inputFieldEditText.setText(
                item.initText.get(itemView.context).ifEmpty { item.text }
            )
        } else {
            binding.inputFieldEditText.setText(item.text)
        }

        binding.inputFieldLayout.hint = item.label?.get(itemView.context)
        binding.inputFieldLayout.placeholderText = item.placeholder?.get(itemView.context)

        when (item.type) {
            FieldType.DEFAULT -> {
                binding.inputFieldEditText.inputType =
                    TYPE_TEXT_VARIATION_PERSON_NAME or TYPE_TEXT_FLAG_CAP_SENTENCES
            }

            FieldType.NUMBER -> {
                binding.inputFieldEditText.inputType = TYPE_CLASS_NUMBER
            }

            FieldType.URI -> {
                binding.inputFieldEditText.inputType = TYPE_TEXT_VARIATION_URI
            }
        }
    }
}

class InputFieldItemAdapter : ItemAdapter {

    override fun viewType() = InputFieldItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return InputFieldItemViewHolder(inflateItemView(parent))
    }
}