package com.github.burachevsky.mqtthub.core.ui.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

inline fun <reified T> Fragment.applicationAs(): T {
    return requireContext().applicationContext as T
}

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