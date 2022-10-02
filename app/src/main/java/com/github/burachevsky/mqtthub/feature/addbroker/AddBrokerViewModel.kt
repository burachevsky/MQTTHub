package com.github.burachevsky.mqtthub.feature.addbroker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.EmptyTxt
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.text.withArgs
import com.github.burachevsky.mqtthub.common.widget.ButtonItem
import com.github.burachevsky.mqtthub.common.widget.FieldType
import com.github.burachevsky.mqtthub.common.widget.InputFieldItem
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.domain.usecase.broker.AddBroker
import com.github.burachevsky.mqtthub.domain.usecase.broker.GetBroker
import com.github.burachevsky.mqtthub.domain.usecase.broker.UpdateBroker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.random.Random

class AddBrokerViewModel @Inject constructor(
    private val eventBus: EventBus,
    private val addBroker: AddBroker,
    private val updateBroker: UpdateBroker,
    private val getBroker: GetBroker,
    args: AddBrokerFragmentArgs,
) : ViewModel() {

    private val brokerId = args.brokerId

    val title: Int = if (isEditMode()) R.string.edit_broker else R.string.add_broker

    val container = ViewModelContainer<Navigator>(viewModelScope)

    private val name = InputFieldItem(
        label = Txt.of(R.string.broker_name),
    )

    private val address = InputFieldItem(
        label = Txt.of(R.string.broker_address),
        placeholder = Txt.of(R.string.broker_address_hint),
        type = FieldType.URI,
    )

    private val port = InputFieldItem(
        initText = run {
            if (isEditMode()) EmptyTxt else Txt.of(R.string.broker_default_port)
        },
        label = Txt.of(R.string.broker_port),
        placeholder = Txt.of(R.string.broker_default_port),
        type = FieldType.NUMBER,
    )

    private val clientId = InputFieldItem(
        initText = run {
            if (isEditMode()) EmptyTxt
            else Txt.of(R.string.broker_default_client_id)
                .withArgs(generateClientId())
        },
        label = Txt.of(R.string.broker_client_id),
    )

    private val _items = MutableStateFlow(initialList())
    val items: StateFlow<List<ListItem>> = _items

    private val _itemsChanged = MutableSharedFlow<Unit>()
    val itemsChanged: SharedFlow<Unit> = _itemsChanged

    private var oldBroker: Broker? = null

    init {
        if (isEditMode()) {
            container.launch(Dispatchers.Main) {
                val broker = getBroker(brokerId)
                oldBroker = broker
                name.text = broker.name
                address.text = broker.address
                port.text = broker.port
                clientId.text = broker.clientId
                _itemsChanged.emit(Unit)
            }
        }
    }

    fun saveResult() {
        container.launch(Dispatchers.Main) {
            val broker = oldBroker?.copy(
                name = name.text,
                address = address.text,
                port = port.text,
                clientId = clientId.text,
            ) ?: Broker(
                id = brokerId,
                name = name.text,
                address = address.text,
                port = port.text,
                clientId = clientId.text,
            )

            if (isEditMode()) {
                updateBroker(broker)
                eventBus.send(BrokerEdited(broker))
            } else {
                eventBus.send(BrokerAdded(addBroker(broker)))
            }

            container.navigator {
                back()
            }
        }
    }

    private fun isEditMode() = brokerId > 0

    private fun generateClientId(): String {
        return Random(System.currentTimeMillis())
            .nextInt(1000000, 10000000)
            .toString()
    }

    private fun initialList(): List<ListItem> {
        return listOf(
            name,
            address,
            port,
            clientId,
            ButtonItem(Txt.of(R.string.save)),
        )
    }
}