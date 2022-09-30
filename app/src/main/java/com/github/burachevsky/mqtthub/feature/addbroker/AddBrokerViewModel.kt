package com.github.burachevsky.mqtthub.feature.addbroker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.text.withArgs
import com.github.burachevsky.mqtthub.common.widget.ButtonItem
import com.github.burachevsky.mqtthub.common.widget.FieldType
import com.github.burachevsky.mqtthub.common.widget.InputFieldItem
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.domain.usecase.broker.AddBroker
import com.github.burachevsky.mqtthub.domain.usecase.broker.UpdateBroker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.random.Random

class AddBrokerViewModel @Inject constructor(
    private val eventBus: EventBus,
    private val addBroker: AddBroker,
    private val updateBroker: UpdateBroker,
    args: AddBrokerFragmentArgs,
) : ViewModel() {

    private val initialBrokerInfo: BrokerInfo? = args.brokerInfo

    val title: Txt = initialBrokerInfo
        ?.let { Txt.of(R.string.edit_broker) }
        ?: Txt.of(R.string.add_broker)

    val container = ViewModelContainer<Navigator>(viewModelScope)

    private val brokerName = InputFieldItem(
        initText = Txt.of(initialBrokerInfo?.name.orEmpty()),
        label = Txt.of(R.string.broker_name),
    )

    private val address = InputFieldItem(
        initText = Txt.of(initialBrokerInfo?.address.orEmpty()),
        label = Txt.of(R.string.broker_address),
        placeholder = Txt.of(R.string.broker_address_hint),
        type = FieldType.URI,
    )

    private val port = InputFieldItem(
        initText = initialBrokerInfo?.port?.let(Txt::of)
            ?: Txt.of(R.string.broker_default_port),
        label = Txt.of(R.string.broker_port),
        placeholder = Txt.of(R.string.broker_default_port),
        type = FieldType.NUMBER,
    )

    private val clientId = InputFieldItem(
        initText = initialBrokerInfo?.clientId?.let(Txt::of)
            ?: Txt.of(R.string.broker_default_client_id)
                .withArgs("${Random.nextInt(1000000, 10000000)}"),
        label = Txt.of(R.string.broker_client_id),
    )

    private val _items = MutableStateFlow(initialList())
    val items: StateFlow<List<ListItem>> = _items

    fun saveResult() {
        container.launch(Dispatchers.Main) {
            var broker = Broker(
                id = initialBrokerInfo?.id ?: 0,
                name = brokerName.text,
                address = address.text,
                port = port.text,
                clientId = clientId.text,
            )

            if (initialBrokerInfo == null) {
                broker = addBroker(broker)
                eventBus.send(BrokerAdded(BrokerInfo.map(broker)))
            } else {
                updateBroker(broker)
                eventBus.send(BrokerUpdated(BrokerInfo.map(broker)))
            }

            container.navigator {
                back()
            }
        }
    }

    private fun initialList(): List<ListItem> {
        return listOf(
            brokerName,
            address,
            port,
            clientId,
            ButtonItem(Txt.of(R.string.save)),
        )
    }
}