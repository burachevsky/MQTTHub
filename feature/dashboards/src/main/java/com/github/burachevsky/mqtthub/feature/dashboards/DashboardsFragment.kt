package com.github.burachevsky.mqtthub.feature.dashboards

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.core.ui.container.ViewController
import com.github.burachevsky.mqtthub.core.ui.container.viewContainer
import com.github.burachevsky.mqtthub.core.ui.di.ViewModelFactory
import com.github.burachevsky.mqtthub.core.ui.ext.applicationAs
import com.github.burachevsky.mqtthub.core.ui.ext.collectOnStarted
import com.github.burachevsky.mqtthub.core.ui.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.core.ui.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.feature.dashboards.databinding.FragmentDashboardsBinding
import com.github.burachevsky.mqtthub.feature.dashboards.item.DashboardItem
import com.github.burachevsky.mqtthub.feature.dashboards.item.DashboardItemAdapter
import javax.inject.Inject

class DashboardsFragment : Fragment(R.layout.fragment_dashboards),
    ViewController<DashboardsViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<DashboardsViewModel>

    override val binding by viewBinding(FragmentDashboardsBinding::bind)
    override val viewModel: DashboardsViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

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
        applicationAs<DashboardsComponent.Provider>()
            .dashboardsComponent(DashboardsModule(this))
            .inject(this)
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
}