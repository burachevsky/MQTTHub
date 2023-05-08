package com.github.burachevsky.mqtthub

import android.content.Context
import com.github.burachevsky.mqtthub.core.data.DataModule
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.EnterTextComponent
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.EnterTextModule
import com.github.burachevsky.mqtthub.core.ui.dialog.selector.SelectorComponent
import com.github.burachevsky.mqtthub.core.ui.dialog.selector.SelectorModule
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerComponent
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerModule
import com.github.burachevsky.mqtthub.feature.addtile.AddTileComponent
import com.github.burachevsky.mqtthub.feature.addtile.AddTileModule
import com.github.burachevsky.mqtthub.feature.brokers.BrokersComponent
import com.github.burachevsky.mqtthub.feature.brokers.BrokersFragment
import com.github.burachevsky.mqtthub.feature.mqttservice.MqttService
import com.github.burachevsky.mqtthub.feature.mqttservice.MqttServiceComponent
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardsComponent
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardsModule
import com.github.burachevsky.mqtthub.feature.home.HomeComponent
import com.github.burachevsky.mqtthub.feature.home.HomeModule
import com.github.burachevsky.mqtthub.feature.settings.SettingsComponent
import com.github.burachevsky.mqtthub.feature.settings.SettingsFragment
import com.github.burachevsky.mqtthub.feature.tiledetails.text.TextTileDetailsComponent
import com.github.burachevsky.mqtthub.feature.tiledetails.text.TextTileDetailsModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DataModule::class,
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: AppActivity)
    fun inject(fragment: BrokersFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(service: MqttService)

    fun homeComponent(module: HomeModule): HomeComponent
    fun addBrokerComponent(module: AddBrokerModule): AddBrokerComponent
    fun addTileComponent(module: AddTileModule): AddTileComponent
    fun enterTextComponent(module: EnterTextModule): EnterTextComponent
    fun settingsComponent(): SettingsComponent
    fun dashboardsComponent(module: DashboardsModule): DashboardsComponent
    fun brokersComponent(): BrokersComponent
    fun textTileDetailsComponent(module: TextTileDetailsModule): TextTileDetailsComponent
    fun selectorComponent(module: SelectorModule): SelectorComponent
    fun mqttServiceComponent(): MqttServiceComponent
}