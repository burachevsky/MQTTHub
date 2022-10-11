package com.github.burachevsky.mqtthub.common.container

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.effect.*
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewContainer(
    private val viewController: ViewController<*>,
    private val navigatorFactory: NavigatorFactory,
) {
    private var vmContainer: ViewModelContainer<*>? = null
        private set

    var activity: Activity? = if (viewController is Activity) viewController else null
        private set

    private var viewEffectHandler: EffectHandler? = null

    private var navigator: Navigator? = null

    private var effectCollection: Job? = null

    private fun initComponents() {
        this.vmContainer = viewController.viewModel.container

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

        if (viewController is EffectHandler) {
            viewEffectHandler = viewController
        }
    }

    fun onCreate() {
        viewController.lifecycleScope.launch {
            viewController.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                effectCollection = launch {
                    vmContainer?.effect?.collect(::handleEffect)
                }
            }
        }

        viewController.lifecycleScope.launch {
            viewController.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cancelEffectCollection()
                initComponents()
            }
        }

        viewController.lifecycleScope.launch {
            viewController.repeatOnLifecycle(Lifecycle.State.DESTROYED) {
                clear()
            }
        }
    }

    private fun handleEffect(effect: UIEffect) {
        if (viewEffectHandler?.handleEffect(effect) == true)
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

    private fun cancelEffectCollection() {
        effectCollection?.cancel()
        effectCollection = null
    }

    private fun clear() {
        cancelEffectCollection()
        activity = null
        viewEffectHandler = null
        vmContainer = null
    }
}