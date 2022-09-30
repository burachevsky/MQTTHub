package com.github.burachevsky.mqtthub.common.container

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import com.github.burachevsky.mqtthub.common.effect.Navigate
import com.github.burachevsky.mqtthub.common.effect.ToastMessage
import com.github.burachevsky.mqtthub.common.effect.UIEffect
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import kotlinx.coroutines.async as libAsync

class ViewModelContainer<N : Navigator>(
    val scope: CoroutineScope
) {
    private val _effect = MutableSharedFlow<UIEffect>()
    val effect: SharedFlow<UIEffect> = _effect

    fun raiseEffect(effect: UIEffect) {
        launch(Dispatchers.Main) {
            _effect.emit(effect)
        }
    }

    inline fun raiseEffect(effect: () -> UIEffect) {
        raiseEffect(effect())
    }

    @Suppress("UNCHECKED_CAST")
    inline fun navigator(crossinline navigatorAction: N.() -> Unit) {
        raiseEffect {
            Navigate {
                (it as N).navigatorAction()
            }
        }
    }

    inline fun <T> runSafely(action: () -> T): T? {
        return try {
            action()
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    inline fun launch(
        dispatcher: CoroutineDispatcher,
        crossinline action: suspend CoroutineScope.() -> Unit
    ) = scope.launch(dispatcher) {
        runSafely { action() }
    }

    suspend inline fun <T> withContext(
        dispatcher: CoroutineDispatcher,
        crossinline action: suspend CoroutineScope.() -> T
    ): T? = kotlinx.coroutines.withContext(dispatcher) {
        runSafely { action() }
    }

    inline fun <T> async(
        dispatcher: CoroutineDispatcher,
        crossinline action: suspend CoroutineScope.() -> T
    ): Deferred<T?> = scope.libAsync(dispatcher) {
        runSafely { action() }
    }

    inline fun <T> liveData(crossinline block: suspend LiveDataScope<T>.() -> Unit): LiveData<T> {
        return androidx.lifecycle.liveData {
            runSafely {
                block()
            }
        }
    }
}