package com.github.burachevsky.mqtthub.feature.entertext

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.databinding.FragmentEnterTextBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.elevation.SurfaceColors
import javax.inject.Inject

class EnterTextDialogFragment : BottomSheetDialogFragment(),
    ViewController<EnterTextViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<EnterTextViewModel>

    private var _binding: FragmentEnterTextBinding? = null
    override val binding get() = _binding!!
    override val viewModel: EnterTextViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.enterTextComponent(EnterTextModule(this))
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterTextBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)

        binding.rootLayout.setBackgroundColor(
            SurfaceColors.SURFACE_2.getColor(requireActivity())
        )

        binding.editText.apply {
            setText(viewModel.initText?.get(requireContext()))
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendResult()
                    true
                } else false
            }
        }

        binding.textInputLayout.setEndIconOnClickListener { sendResult() }

        binding.label.text = viewModel.title.get(requireContext())
    }

    override fun onResume() {
        super.onResume()
        binding.editText.requestFocus()
    }

    private fun sendResult() {
        viewModel.sendResult(binding.editText.text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}