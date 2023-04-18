package com.github.burachevsky.mqtthub.feature.dashboards

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.burachevsky.mqtthub.common.container.ViewContainer
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentDashboardsBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.dashboards.item.DashboardItem
import com.github.burachevsky.mqtthub.feature.dashboards.item.DashboardItemAdapter
import javax.inject.Inject

class DashboardsFragment : Fragment(), ViewController<DashboardsViewModel> {

    private var _binding: FragmentDashboardsBinding? = null
    private val binding get() = _binding!!

    override val container = ViewContainer(this, ::DashboardsNavigator)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<DashboardsViewModel>

    override val viewModel: DashboardsViewModel by viewModels { viewModelFactory }

    private val listAdapter = CompositeAdapter(
        DashboardItemAdapter(
            object : DashboardItem.Listener {
                override fun onSubmitClicked(position: Int) {
                    viewModel.submit(position)
                }

                override fun onDeleteClicked(position: Int) {
                    viewModel.delete(position)
                }
            }
        )
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.dashboardsComponent(DashboardsModule(this))
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
        _binding = FragmentDashboardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            adapter = listAdapter
            collectOnStarted(viewModel.items, listAdapter::submitList)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}