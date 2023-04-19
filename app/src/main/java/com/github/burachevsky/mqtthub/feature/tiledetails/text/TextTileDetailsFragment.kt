package com.github.burachevsky.mqtthub.feature.tiledetails.text

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.burachevsky.mqtthub.common.container.ViewContainer
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.setFocus
import com.github.burachevsky.mqtthub.common.ext.setOnEnterListener
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.databinding.FragmentTextTileDetailsBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import javax.inject.Inject

class TextTileDetailsFragment : Fragment(), ViewController<TextTileDetailsViewModel> {

    private var _binding: FragmentTextTileDetailsBinding? = null
    private val binding get() = _binding!!

    override val container = ViewContainer(this, ::Navigator)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<TextTileDetailsViewModel>

    override val viewModel: TextTileDetailsViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.textTileDetailsComponent(TextTileDetailsModule(this))
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container.onCreate()
        /*val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.slide_left)

        postponeEnterTransition()
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTextTileDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.editText.setOnEnterListener(::publishText)
        binding.textInputLayout.setEndIconOnClickListener { publishText() }

        binding.tileName.setOnClickListener {
            binding.editText.setFocus(false)
        }

        binding.tilePayload.setOnClickListener {
            binding.editText.setFocus(false)
        }

        collectOnStarted(viewModel.tileName) {
            binding.tileName.text = it
            //startPostponedEnterTransition()
        }
        collectOnStarted(viewModel.tilePayload, binding.tilePayload::setText)

        collectOnStarted(viewModel.isSendEnabled) { isSendEnabled ->
            binding.textInputLayout.isVisible = isSendEnabled
            binding.editText.setFocus(isSendEnabled)
        }
    }

    private fun publishText() {
        viewModel.enterPublishText(binding.editText.text.toString())
    }
}