package com.github.burachevsky.mqtthub.common.container

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import com.github.burachevsky.mqtthub.common.container.VM as AppVM

class ViewContainerProperty(
    container: ViewContainer
) : ReadOnlyProperty<ViewController<*>, ViewContainer> {

    private var _container: ViewContainer? = container

    init {
        container.lifecycle.addObserver(ViewContainerLifecycleObserver())
    }

    override fun getValue(
        thisRef: ViewController<out VM<out Navigator>>,
        property: KProperty<*>
    ): ViewContainer {
        return checkNotNull(_container)
    }

    private inner class ViewContainerLifecycleObserver : DefaultLifecycleObserver {

        override fun onStart(owner: LifecycleOwner) {
            _container?.onStart()
        }

        override fun onStop(owner: LifecycleOwner) {
            _container?.onStop()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            _container?.onDestroy()
            _container = null
        }
    }
}


inline fun <reified N : Navigator, VM : AppVM<out N>> ViewController<VM>.viewContainer()
        : ViewContainerProperty {

    return ViewContainerProperty(
        ViewContainer(
            this,
            NavigatorFactory(N::class.constructors.first()::call)
        )
    )
}

fun <N : Navigator> VM<N>.viewModelContainer(): ViewModelContainer<N> {
    return ViewModelContainer((this as ViewModel).viewModelScope)
}