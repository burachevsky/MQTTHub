package com.github.burachevsky.mqtthub.di

import android.content.Context
import com.github.burachevsky.mqtthub.AppActivity
import com.github.burachevsky.mqtthub.data.di.LocalDataModule
import com.github.burachevsky.mqtthub.data.di.RemoteDataModule
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerComponent
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerModule
import com.github.burachevsky.mqtthub.feature.brokers.BrokersFragment
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

    fun addBrokerComponent(fragment: AddBrokerModule): AddBrokerComponent
}