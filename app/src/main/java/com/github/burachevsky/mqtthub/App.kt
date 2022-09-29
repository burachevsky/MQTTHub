package com.github.burachevsky.mqtthub

import android.app.Application
import com.github.burachevsky.mqtthub.di.AppComponent
import com.github.burachevsky.mqtthub.di.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}