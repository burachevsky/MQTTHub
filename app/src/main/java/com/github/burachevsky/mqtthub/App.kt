package com.github.burachevsky.mqtthub

import android.app.Application
import com.github.burachevsky.mqtthub.di.AppComponent
import com.github.burachevsky.mqtthub.di.DaggerAppComponent
import com.google.android.material.color.DynamicColors
import timber.log.Timber

class App : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Timber.plant(Timber.DebugTree())
    }

    fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}