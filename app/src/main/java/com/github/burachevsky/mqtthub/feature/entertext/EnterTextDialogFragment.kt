package com.github.burachevsky.mqtthub.feature.entertext

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.databinding.FragmentEnterTextBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class EnterTextDialogFragment : BottomSheetDialogFragment(R.layout.fragment_enter_text),
    ViewController<EnterTextViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<EnterTextViewModel>

    override val binding by viewBinding(FragmentEnterTextBinding::bind)
    override val viewModel: EnterTextViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.enterTextComponent(EnterTextModule(this))
            .inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editText.requestFocus()
        dialog?.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)

        binding.textInputLayout.hint = viewModel.title.get(requireContext())

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