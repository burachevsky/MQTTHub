package com.github.burachevsky.mqtthub.di

import android.content.Context
import com.github.burachevsky.mqtthub.AppActivity
import com.github.burachevsky.mqtthub.data.di.LocalDataModule
import com.github.burachevsky.mqtthub.data.di.RemoteDataModule
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerComponent
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerModule
import com.github.burachevsky.mqtthub.feature.brokers.BrokersFragment
import com.github.burachevsky.mqtthub.feature.home.HomeComponent
import com.github.burachevsky.mqtthub.feature.home.HomeModule
import com.github.burachevsky.mqtthub.feature.home.addtile.text.AddTextTileComponent
import com.github.burachevsky.mqtthub.feature.home.addtile.text.AddTextTileModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        LocalDataModule::class,
        RemoteDataModule::class,
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: AppActivity)
    fun inject(fragment: BrokersFragment)

    fun addBrokerComponent(module: AddBrokerModule): AddBrokerComponent
    fun homeComponent(module: HomeModule): HomeComponent
    fun addTextTileComponent(module: AddTextTileModule): AddTextTileComponent
}