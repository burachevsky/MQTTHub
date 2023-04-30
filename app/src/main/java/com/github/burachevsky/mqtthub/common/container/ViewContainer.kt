package com.github.burachevsky.mqtthub.common.container

import android.app.Activity
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.github.burachevsky.mqtthub.AppActivity
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.event.*
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.domain.eventbus.AppEventHandler
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewContainer(
    private val viewController: ViewController<*>,
    private val navigatorFactory: NavigatorFactory,
) : LifecycleOwner by viewController, DependableOnStatusBarHeight {

    private var vmContainer: ViewModelContainer<*>? = null
    private var activity: Activity? = null
    private var appEventHandler: AppEventHandler? = null
    private var navigator: Navigator? = null
    private var effectCollectionJob: Job? = null

    fun onStart() {
        cancelEffectCollection()
        initComponents()
        startEffectCollection()
        fitStatusBarHeight()
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

    override fun fitStatusBarHeight(statusBarHeight: Int) {
        if (viewController is DependableOnStatusBarHeight) {
            viewController.fitStatusBarHeight(statusBarHeight)
        } else if (viewController is Fragment && viewController !is DialogFragment) {
            viewController.binding.root.updateLayoutParams<MarginLayoutParams> {
                topMargin = statusBarHeight
            }
        }
    }

    private fun fitStatusBarHeight() {
        AppActivity.statusBarHeight.let(::fitStatusBarHeight)
    }

    private fun initComponents() {
        vmContainer = viewController.viewModel.container

        when (viewController) {
            is Activity -> {
                activity = viewController

                navigator = navigatorFactory.createNavigator(
                    viewController.findNavController(R.id.appContainer)
                )
            }
            is Fragment -> {
                activity = viewController.requireActivity()

                navigator = navigatorFactory.createNavigator(viewController.findNavController())
            }
        }

        if (viewController is AppEventHandler) {
            appEventHandler = viewController
        }

        requireNotNull(activity)
    }

    private fun handleEffect(effect: AppEvent) {
        if (appEventHandler?.handleEffect(effect) == true)
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
}