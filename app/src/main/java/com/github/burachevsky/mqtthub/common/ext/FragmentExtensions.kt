package com.github.burachevsky.mqtthub.common.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.burachevsky.mqtthub.App
import com.github.burachevsky.mqtthub.di.AppComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

val Fragment.appComponent: AppComponent
    get() = (requireActivity().application as App).appComponent

fun <T> Fragment.collectOnStarted(flow: Flow<T>, collector: FlowCollector<T>) {
    viewLifecycleOwner.run {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(collector)
            }
        }
    }
}

fun Fragment.verticalLinearLayoutManager() =
    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)