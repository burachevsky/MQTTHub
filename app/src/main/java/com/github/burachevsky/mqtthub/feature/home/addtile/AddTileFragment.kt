package com.github.burachevsky.mqtthub.feature.home.addtile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.burachevsky.mqtthub.common.container.ViewContainer
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.widget.*
import com.github.burachevsky.mqtthub.databinding.FragmentAddTileBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import kotlin.reflect.KClass

abstract class AddTileFragment<VM : AddTileViewModel>(
    private val viewModelClass: KClass<VM>,
) : Fragment(), ViewController<VM> {

    abstract var viewModelFactory: ViewModelFactory<VM>

    override val container = ViewContainer(this, ::Navigator)

    private var _binding: FragmentAddTileBinding? = null
    private val binding get() = _binding!!

    open val listAdapter = CompositeAdapter(
        InputFieldItemAdapter(),
        ButtonItemAdapter(
            listener = {
                viewModel.saveResult()
            }
        ),
        SwitchItemAdapter(),
        QosSelectorItemAdapter(),
    )

    abstract fun inject()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
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
        _binding = FragmentAddTileBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}