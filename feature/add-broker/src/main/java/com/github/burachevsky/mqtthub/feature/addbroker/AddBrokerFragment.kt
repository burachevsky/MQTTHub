package com.github.burachevsky.mqtthub.feature.addbroker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.core.ui.container.viewContainer
import com.github.burachevsky.mqtthub.core.ui.di.ViewModelFactory
import com.github.burachevsky.mqtthub.core.ui.ext.applicationAs
import com.github.burachevsky.mqtthub.core.ui.ext.collectOnStarted
import com.github.burachevsky.mqtthub.core.ui.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.feature.addbroker.databinding.FragmentAddBrokerBinding
import javax.inject.Inject

class AddBrokerFragment : Fragment(R.layout.fragment_add_broker),
    com.github.burachevsky.mqtthub.core.ui.container.ViewController<AddBrokerViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AddBrokerViewModel>

    override val binding by viewBinding(FragmentAddBrokerBinding::bind)
    override val viewModel: AddBrokerViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    private val listAdapter = com.github.burachevsky.mqtthub.core.ui.recycler.CompositeAdapter(
        com.github.burachevsky.mqtthub.core.ui.widget.InputFieldItemAdapter(),
        com.github.burachevsky.mqtthub.core.ui.widget.ButtonItemAdapter(
            listener = {
                viewModel.saveResult()
            }
        )
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        applicationAs<AddBrokerComponent.Provider>()
            .addBrokerComponent(AddBrokerModule(this))
            .inject(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setTitle(viewModel.title)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            adapter = listAdapter
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        collectOnStarted(viewModel.itemsChanged) {
            listAdapter.notifyDataSetChanged()
        }
    }
}
