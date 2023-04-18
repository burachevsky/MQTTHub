package com.github.burachevsky.mqtthub

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.github.burachevsky.mqtthub.common.container.ViewContainer
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import javax.inject.Inject

class AppActivity : AppCompatActivity(), ViewController<AppViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AppViewModel>

    override val viewModel: AppViewModel by viewModels { viewModelFactory }

    override val container = ViewContainer(this, ::Navigator)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        (application as App).appComponent.inject(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        container.onCreate()
        findNavController().setGraph(R.navigation.app_graph)
    }

    fun findNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.appContainer) as NavHostFragment)
            .navController
    }
}