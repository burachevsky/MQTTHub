package com.github.burachevsky.mqtthub.feature.addbroker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.github.burachevsky.mqtthub.core.domain.usecase.broker.AddBroker
import com.github.burachevsky.mqtthub.core.domain.usecase.broker.ObserveBroker
import com.github.burachevsky.mqtthub.core.domain.usecase.broker.UpdateBroker
import com.github.burachevsky.mqtthub.core.model.Broker
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class AddBrokerViewModel @Inject constructor(
    private val addBroker: AddBroker,
    private val updateBroker: UpdateBroker,
    private val brokerId: Long,
    private val itemStore: AddBrokerItemStore,
    observeBroker: ObserveBroker,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    private val isEditMode = brokerId > 0

    val title: Int = if (isEditMode) R.string.edit_broker else R.string.add_broker

    private val broker: LiveData<Broker> = when {
        isEditMode -> observeBroker(brokerId).asLiveData()
        else -> MutableLiveData(null)
    }

    val items: LiveData<List<ListItem>> = broker.map(::makeItemsList)

    fun saveResult() {
        container.launch(Dispatchers.Main) {
            val broker = (broker.value ?: Broker()).copy(
                id = brokerId,
                name = itemStore.name.text,
                address = itemStore.address.text,
                port = itemStore.port.text,
                clientId = itemStore.clientId.text,
            )

            when {
                isEditMode -> updateBroker(broker)
                else -> addBroker(broker)
            }

            container.navigator { back() }
        }
    }

    private fun makeItemsList(broker: Broker? = null): List<ListItem> {
        return itemStore.run {
            itemStore.name.text = broker?.name.orEmpty()
            itemStore.address.text = broker?.address.orEmpty()
            itemStore.port.text = broker?.port.orEmpty()
            itemStore.clientId.text = broker?.clientId.orEmpty()

            listOf(name, address, port, clientId, save,)
        }
    }
}