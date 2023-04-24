package com.github.burachevsky.mqtthub.feature.publishtext

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.databinding.DialogPublishTextBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class PublishTextDialogFragment : BottomSheetDialogFragment(R.layout.dialog_publish_text),
    ViewController<PublishTextViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<PublishTextViewModel>

    override val binding by viewBinding(DialogPublishTextBinding::bind)
    override val viewModel: PublishTextViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.publishTextComponent(PublishTextModule(this))
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editText.requestFocus()
        dialog?.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)

        binding.textInputLayout.hint = viewModel.tileName

        binding.textInputLayout.setEndIconOnClickListener { sendResult() }
        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendResult()
                true
            } else false
        }
    }

    private fun sendResult() {
        viewModel.sendResult(binding.editText.text.toString())
    }
}