package com.github.burachevsky.mqtthub.core.ui.container

import android.app.Activity
import android.content.Intent
import android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
import android.provider.Settings.EXTRA_APP_PACKAGE
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.burachevsky.mqtthub.core.eventbus.AppEvent
import com.github.burachevsky.mqtthub.core.eventbus.AppEventHandler
import com.github.burachevsky.mqtthub.core.ui.event.AlertDialog
import com.github.burachevsky.mqtthub.core.ui.event.GoToNotificationSettings
import com.github.burachevsky.mqtthub.core.ui.event.Navigate
import com.github.burachevsky.mqtthub.core.ui.event.ToastMessage
import com.github.burachevsky.mqtthub.core.ui.navigation.NavControllerProvider
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewContainer(
    private val viewController: ViewController<*>,
    private val navigatorFactory: NavigatorFactory,
) : LifecycleOwner by viewController, DependentOnSystemBarsSize {

    private var vmContainer: ViewModelContainer<*>? = null
    private var activity: Activity? = null
    private var appEventHandler: AppEventHandler? = null
    private var navigator: Navigator? = null
    private var effectCollectionJob: Job? = null

    fun onStart() {
        cancelEffectCollection()
        initComponents()
        startEffectCollection()
        fitSystemBars()
    }

    fun onStop() {
        cancelEffectCollection()
    }

    fun onDestroy() {
        cancelEffectCollection()
        activity = null
        appEventHandler = null
        vmContainer = null
    }

    fun navigator(): Navigator = navigator!!

    override fun fitSystemBars(statusBarHeight: Int, navigationBarHeight: Int) {
        if (viewController is DependentOnSystemBarsSize) {
            viewController.fitSystemBars(statusBarHeight, navigationBarHeight)
        } else if (viewController is Fragment) {
            if (viewController !is BottomSheetDialogFragment) {
                viewController.binding.root.updateLayoutParams<MarginLayoutParams> {
                    topMargin = statusBarHeight
                }
            }
            viewController.binding.root.updatePadding(bottom = navigationBarHeight)
        }
    }

    private fun getNavDestinationMapper(): NavDestinationMapper {
        return (activity as NavDestinationMapper.Provider).provideNavDestinationMapper()
    }

    private fun fitSystemBars() {
        (activity as? SystemBarsSizeProvider)?.apply {
            fitSystemBars(statusBarHeight, navigationBarHeight)
        }
    }

    private fun initComponents() {
        vmContainer = viewController.viewModel.container

        when (viewController) {
            is Activity -> {
                activity = viewController

                navigator = navigatorFactory.createNavigator(
                    (activity as NavControllerProvider).provideNavController(),
                    getNavDestinationMapper()
                )
            }
            is Fragment -> {
                activity = viewController.requireActivity()

                navigator = navigatorFactory.createNavigator(
                    viewController.findNavController(),
                   getNavDestinationMapper()
                )
            }
        }

        if (viewController is AppEventHandler) {
            appEventHandler = viewController
        }

        requireNotNull(activity)
    }

    private fun handleEffect(effect: AppEvent) {
        if (appEventHandler?.handleEvent(effect) == true)
            return

        when (effect) {
            is ToastMessage -> {
                Toast.makeText(activity, effect.text.get(activity!!), Toast.LENGTH_SHORT)
                    .show()
            }

            is Navigate -> {
                navigator?.let(effect.navigateAction::invoke)
            }

            is AlertDialog -> with(effect) {
                MaterialAlertDialogBuilder(activity!!)
                    .setCancelable(cancelable)
                    .setTitle(title?.get(activity!!))
                    .setMessage(message?.get(activity!!))
                    .also { builder ->
                        yes?.let { button ->
                            builder.setPositiveButton(button.text.get(activity!!)) { _, _ ->
                                button.action?.invoke()
                            }
                        }

                        no?.let { button ->
                            builder.setNegativeButton(button.text.get(activity!!)) { _, _ ->
                                button.action?.invoke()
                            }
                        }

                        cancel?.let { button ->
                            builder.setNeutralButton(button.text.get(activity!!)) { _, _ ->
                                button.action?.invoke()
                            }
                        }

                        builder.setOnCancelListener {
                            cancel?.action?.invoke()
                        }
                    }
                    .create()
                    .show()
            }

            GoToNotificationSettings -> withActivity {
                startActivity(
                    Intent(ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(EXTRA_APP_PACKAGE, packageName)
                    }
                )
            }
        }
    }

    private fun startEffectCollection() {
        effectCollectionJob = lifecycleScope.launch {
            vmContainer?.effect?.collect(::handleEffect)
        }
    }

    private fun cancelEffectCollection() {
        effectCollectionJob?.cancel()
        effectCollectionJob = null
    }

    private inline fun withActivity(block: Activity.() -> Unit) {
        activity?.block()
    }
}