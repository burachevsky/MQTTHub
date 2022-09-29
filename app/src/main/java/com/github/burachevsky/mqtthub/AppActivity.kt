package com.github.burachevsky.mqtthub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import javax.inject.Inject

class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AppViewModel>

    lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        (application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[AppViewModel::class.java]
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}