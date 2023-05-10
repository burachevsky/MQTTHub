package com.github.burachevsky.mqtthub.feature.helpandfeedback

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.core.ui.container.ViewController
import com.github.burachevsky.mqtthub.core.ui.container.viewContainer
import com.github.burachevsky.mqtthub.core.ui.di.ViewModelFactory
import com.github.burachevsky.mqtthub.core.ui.ext.applicationAs
import com.github.burachevsky.mqtthub.feature.helpandfeedback.databinding.FragmentHelpAndFeedbackBinding
import javax.inject.Inject

class HelpAndFeedbackFragment : Fragment(R.layout.fragment_help_and_feedback),
    ViewController<HelpAndFeedbackViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HelpAndFeedbackViewModel>

    override val binding by viewBinding(FragmentHelpAndFeedbackBinding::bind)
    override val viewModel: HelpAndFeedbackViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        applicationAs<HelpAndFeedbackComponent.Provider>()
            .helpAndSupportComponent()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            container.navigator().back()
        }
    }
}