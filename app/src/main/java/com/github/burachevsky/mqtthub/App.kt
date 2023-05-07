package com.github.burachevsky.mqtthub

import android.app.Application
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.EnterTextComponent
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.EnterTextModule
import com.github.burachevsky.mqtthub.core.ui.dialog.selector.SelectorComponent
import com.github.burachevsky.mqtthub.core.ui.dialog.selector.SelectorModule
import com.github.burachevsky.mqtthub.core.ui.notification.createNotificationChannels
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerComponent
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerModule
import com.github.burachevsky.mqtthub.feature.addtile.AddTileComponent
import com.github.burachevsky.mqtthub.feature.addtile.AddTileModule
import com.github.burachevsky.mqtthub.feature.brokers.BrokersComponent
import com.github.burachevsky.mqtthub.feature.connection.BrokerConnectionServiceComponent
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardsComponent
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardsModule
import com.github.burachevsky.mqtthub.feature.home.HomeComponent
import com.github.burachevsky.mqtthub.feature.home.HomeModule
import com.github.burachevsky.mqtthub.feature.settings.SettingsComponent
import com.github.burachevsky.mqtthub.feature.tiledetails.text.TextTileDetailsComponent
import com.github.burachevsky.mqtthub.feature.tiledetails.text.TextTileDetailsModule
import com.google.android.material.color.DynamicColors
import timber.log.Timber

class App : Application(),
    HomeComponent.Provider,
    SelectorComponent.Provider,
    EnterTextComponent.Provider,
    SettingsComponent.Provider,
    TextTileDetailsComponent.Provider,
    AddTileComponent.Provider,
    BrokerConnectionServiceComponent.Provider,
    DashboardsComponent.Provider,
    BrokersComponent.Provider,
    AddBrokerComponent.Provider {

    val appComponent: AppComponent by lazy(::initializeComponent)

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Timber.plant(Timber.DebugTree())
        createNotificationChannels()
    }

    override fun homeComponent(module: HomeModule): HomeComponent {
        return appComponent.homeComponent(module)
    }

    override fun selectorComponent(module: SelectorModule): SelectorComponent {
        return appComponent.selectorComponent(module)
    }

    override fun enterTextComponent(module: EnterTextModule): EnterTextComponent {
        return appComponent.enterTextComponent(module)
    }

    override fun settingsComponent(): SettingsComponent {
        return appComponent.settingsComponent()
    }

    override fun textTileDetailsComponent(module: TextTileDetailsModule): TextTileDetailsComponent {
        return appComponent.textTileDetailsComponent(module)
    }

    override fun addTileComponent(module: AddTileModule): AddTileComponent {
        return appComponent.addTileComponent(module)
    }

    override fun brokerConnectionServiceComponent(): BrokerConnectionServiceComponent {
        return appComponent.brokerConnectionServiceComponent()
    }

    override fun dashboardsComponent(module: DashboardsModule): DashboardsComponent {
        return appComponent.dashboardsComponent(module)
    }

    override fun brokersComponent(): BrokersComponent {
        return appComponent.brokersComponent()
    }

    override fun addBrokerComponent(module: AddBrokerModule): AddBrokerComponent {
        return appComponent.addBrokerComponent(module)
    }

    private fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}