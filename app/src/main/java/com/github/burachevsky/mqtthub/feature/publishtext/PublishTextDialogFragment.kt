package com.github.burachevsky.mqtthub.feature.publishtext

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import com.github.burachevsky.mqtthub.common.container.ViewContainer
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.databinding.DialogPublishTextBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject


class PublishTextDialogFragment : BottomSheetDialogFragment(), ViewController<PublishTextViewModel> {

    private var _binding: DialogPublishTextBinding? = null
    private val binding get() = _binding!!

    override val container = ViewContainer(this, ::Navigator)

    override val viewModel: PublishTextViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<PublishTextViewModel>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.publishTextComponent(PublishTextModule(this))
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPublishTextBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}