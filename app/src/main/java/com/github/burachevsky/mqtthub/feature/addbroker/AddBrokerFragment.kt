package com.github.burachevsky.mqtthub.feature.addbroker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.widget.ButtonItemAdapter
import com.github.burachevsky.mqtthub.common.widget.InputFieldItemAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentAddBrokerBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import javax.inject.Inject

class AddBrokerFragment : Fragment(R.layout.fragment_add_broker),
    ViewController<AddBrokerViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AddBrokerViewModel>

    override val binding by viewBinding(FragmentAddBrokerBinding::bind)
    override val viewModel: AddBrokerViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    private val listAdapter = CompositeAdapter(
        InputFieldItemAdapter(),
        ButtonItemAdapter(
            listener = {
                viewModel.saveResult()
            }
        )
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.addBrokerComponent(AddBrokerModule(this))
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
