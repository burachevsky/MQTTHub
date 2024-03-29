package com.github.burachevsky.mqtthub.feature.tiledetails.text

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.core.ui.container.DependentOnSystemBarsSize
import com.github.burachevsky.mqtthub.core.ui.container.ViewController
import com.github.burachevsky.mqtthub.feature.tiledetails.R
import com.github.burachevsky.mqtthub.core.ui.container.viewContainer
import com.github.burachevsky.mqtthub.core.ui.di.ViewModelFactory
import com.github.burachevsky.mqtthub.core.ui.ext.applicationAs
import com.github.burachevsky.mqtthub.core.ui.ext.collectOnStarted
import com.github.burachevsky.mqtthub.feature.tiledetails.databinding.FragmentTextTileDetailsBinding
import com.google.android.material.elevation.SurfaceColors
import javax.inject.Inject

class TextTileDetailsFragment : Fragment(R.layout.fragment_text_tile_details),
    ViewController<TextTileDetailsViewModel>,
    DependentOnSystemBarsSize {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<TextTileDetailsViewModel>

    override val binding  by viewBinding(FragmentTextTileDetailsBinding::bind)
    override val viewModel: TextTileDetailsViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        applicationAs<TextTileDetailsComponent.Provider>()
            .textTileDetailsComponent(TextTileDetailsModule(this))
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareTransitions()
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        collectOnStarted(viewModel.tileName) {
            binding.tileName.text = it
            startPostponedEnterTransition()
        }
        collectOnStarted(viewModel.tilePayload, binding.tilePayload::setText)

        binding.inputTextButton.setOnClickListener {
            viewModel.publishTextClicked()
        }

        collectOnStarted(viewModel.isSendEnabled) { isSendEnabled ->
            binding.inputTextButton.isVisible = isSendEnabled
        }

        binding.inputTextButton.setBackgroundColor(
            SurfaceColors.SURFACE_2.getColor(requireContext())
        )
    }

    override fun fitSystemBars(statusBarHeight: Int, navigationBarHeight: Int) {
        binding.tileDetails.updateLayoutParams<MarginLayoutParams> {
            topMargin = statusBarHeight
        }
        binding.inputTextButton.updateLayoutParams<MarginLayoutParams> {
            bottomMargin = navigationBarHeight
        }
    }

    private fun prepareTransitions() {
        val transition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
            .also { it.duration = 150 }

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }
}