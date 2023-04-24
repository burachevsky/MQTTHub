package com.github.burachevsky.mqtthub.feature.addtile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.widget.*
import com.github.burachevsky.mqtthub.databinding.FragmentAddTileBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory

abstract class AddTileFragment<VM : AddTileViewModel> : Fragment(R.layout.fragment_add_tile),
    ViewController<VM> {

    abstract var viewModelFactory: ViewModelFactory<VM>

    override val binding by viewBinding(FragmentAddTileBinding::bind)
    override val container by viewContainer()

    open val listAdapter = CompositeAdapter(
        InputFieldItemAdapter(),
        ButtonItemAdapter(
            listener = {
                viewModel.saveResult()
            }
        ),
        SwitchItemAdapter(),
        ToggleGroupItemItemAdapter(),
    )

    abstract fun inject()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            adapter = listAdapter
        }

        binding.toolbar.setTitle(viewModel.title)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        collectOnStarted(viewModel.itemsChanged) {
            listAdapter.notifyDataSetChanged()
        }
    }
}