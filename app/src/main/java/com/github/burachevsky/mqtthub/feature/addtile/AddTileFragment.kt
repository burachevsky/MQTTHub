package com.github.burachevsky.mqtthub.feature.addtile

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.common.event.RequestNotificationsPermissionIfNeeded
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.notification.isNotificationsPermissionEnabled
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.widget.*
import com.github.burachevsky.mqtthub.databinding.FragmentAddTileBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent
import com.github.burachevsky.mqtthub.domain.eventbus.AppEventHandler

abstract class AddTileFragment<VM : AddTileViewModel> : Fragment(R.layout.fragment_add_tile),
    ViewController<VM>, AppEventHandler {

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

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onNotificationPermissionResult(isGranted)
    }

    abstract fun inject()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            adapter = listAdapter
        }

        binding.toolbar.setTitle(viewModel.title)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        collectOnStarted(viewModel.itemChanged, listAdapter::notifyItemChanged)
    }

    override fun onResume() {
        super.onResume()

        viewModel.notificationPermissionWhenResumed(
            requireContext().isNotificationsPermissionEnabled()
        )
    }

    override fun handleEffect(effect: AppEvent): Boolean {
        when (effect) {
            is RequestNotificationsPermissionIfNeeded -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
                    val status = ActivityCompat.checkSelfPermission(
                        requireContext(),
                        POST_NOTIFICATIONS
                    )

                    if (status != PERMISSION_GRANTED) {
                        requestPermission.launch(POST_NOTIFICATIONS)
                    }
                }
            }

            CheckForNotificationsPermission -> {
                viewModel.onNotificationPermissionResult(
                    requireContext().isNotificationsPermissionEnabled()
                )
            }

            else -> return false
        }

        return true
    }
}