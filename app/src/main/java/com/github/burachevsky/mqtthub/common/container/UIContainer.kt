package com.github.burachevsky.mqtthub.common.container

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.burachevsky.mqtthub.common.effect.*
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UIContainer(
    val lifecycleOwner: LifecycleOwner,
    val navigatorFactory: NavigatorFactory?
) {
    var vmContainer: ViewModelContainer<*>? = null
        private set

    var fragment: Fragment? = null
        private set

    var context: Context? = null
        private set

    private var viewEffectHandler: EffectHandler? = null

    private var navigator: Navigator? = null

    private var effectCollection: Job? = null

    fun onViewCreated(
        vmContainer: ViewModelContainer<*>,
        fragment: Fragment? = null
    ) {
        this.vmContainer = vmContainer

        this.fragment = fragment

        if (fragment != null) {
            context = fragment.requireContext()

            if (fragment is EffectHandler) {
                viewEffectHandler = fragment
            }

            navigator = navigatorFactory?.createNavigator(fragment.findNavController())
        }
    }

    fun onCreate() {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                effectCollection = launch {
                    vmContainer?.effect?.collect(::handleEffect)
                }
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cancelEffectCollection()
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.DESTROYED) {
                clear()
            }
        }
    }

    private fun handleEffect(effect: UIEffect) {
        if (viewEffectHandler?.handleEffect(effect) == true)
            return

        when (effect) {
            is ToastMessage -> {
                Toast.makeText(context, effect.text.get(context!!), Toast.LENGTH_SHORT)
                    .show()
            }

            is Navigate -> {
                navigator?.let(effect.navigateAction::invoke)
            }

            is AlertDialog -> with(effect) {
                MaterialAlertDialogBuilder(context!!)
                    .setCancelable(cancelable)
                    .setTitle(title?.get(context!!))
                    .setMessage(message?.get(context!!))
                    .also { builder ->
                        yes?.let { button ->
                            builder.setPositiveButton(button.text.get(context!!)) { _, _ ->
                                button.action?.invoke()
                            }
                        }

                        no?.let { button ->
                            builder.setNegativeButton(button.text.get(context!!)) { _, _ ->
                                button.action?.invoke()
                            }
                        }

                        cancel?.let { button ->
                            builder.setNeutralButton(button.text.get(context!!)) { _, _ ->
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
        context = null
        fragment = null
        viewEffectHandler = null
        vmContainer = null
    }
}