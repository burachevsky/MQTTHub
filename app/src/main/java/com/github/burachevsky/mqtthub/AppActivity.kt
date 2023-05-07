package com.github.burachevsky.mqtthub

import android.app.UiModeManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.core.data.settings.Theme
import com.github.burachevsky.mqtthub.core.eventbus.AppEvent
import com.github.burachevsky.mqtthub.core.eventbus.AppEventHandler
import com.github.burachevsky.mqtthub.core.ui.container.NavDestinationMapper
import com.github.burachevsky.mqtthub.core.ui.container.SystemBarsSizeProvider
import com.github.burachevsky.mqtthub.core.ui.container.ViewController
import com.github.burachevsky.mqtthub.core.ui.container.viewContainer
import com.github.burachevsky.mqtthub.core.ui.di.ViewModelFactory
import com.github.burachevsky.mqtthub.core.ui.event.StartNewBrokerConnection
import com.github.burachevsky.mqtthub.core.ui.event.SwitchTheme
import com.github.burachevsky.mqtthub.core.ui.ext.getNavigationBarHeightFromSystemAttribute
import com.github.burachevsky.mqtthub.core.ui.ext.getStatusBarHeightFromSystemAttribute
import com.github.burachevsky.mqtthub.core.ui.navigation.NavControllerProvider
import com.github.burachevsky.mqtthub.databinding.ActivityAppBinding
import com.github.burachevsky.mqtthub.feature.connection.BrokerConnectionService
import com.google.android.material.color.DynamicColors
import javax.inject.Inject

class AppActivity : AppCompatActivity(),
    ViewController<AppViewModel>, AppEventHandler,
    SystemBarsSizeProvider, NavControllerProvider, NavDestinationMapper.Provider {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AppViewModel>

    override val binding by viewBinding(ActivityAppBinding::bind, R.id.appContainer)
    override val viewModel: AppViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    override var statusBarHeight = 0
    override var navigationBarHeight = 0

    private var contentIsSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)
        setupActivityAppearance()
        super.onCreate(savedInstanceState)
        startBrokerConnectionService()
        setContentView(R.layout.activity_app)

        binding.root.rootView.setOnApplyWindowInsetsListener { _, insets ->
            if (contentIsSet)
                return@setOnApplyWindowInsetsListener insets

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                statusBarHeight = statusBarInsets.top
                navigationBarHeight = navigationBarInsets.bottom
            } else {
                statusBarHeight = getStatusBarHeightFromSystemAttribute()
                navigationBarHeight = getNavigationBarHeightFromSystemAttribute()
            }

            setContent()

            insets
        }
    }

    private fun setContent() {
        contentIsSet = true
        provideNavController().setGraph(R.navigation.app_graph)
    }

    override fun handleEvent(effect: AppEvent): Boolean {
        when (effect) {
            is SwitchTheme -> {
                viewModel.themeIsInitialized = false
                recreate()
                return true
            }

            is StartNewBrokerConnection -> {
                startBrokerConnectionService()
            }
        }

        return false
    }

    override fun provideNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.appContainer) as NavHostFragment)
            .navController
    }

    override fun provideNavDestinationMapper(): NavDestinationMapper {
        return AppNavDestinationMapper
    }

    private fun setupActivityAppearance() {
        window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)

        val themeId = viewModel.getTheme()
        val dynamicColorsEnabled = viewModel.dynamicColorsEnabled()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(
                when (themeId) {
                    Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
        } else {
            getSystemService<UiModeManager>()?.nightMode = when (themeId) {
                Theme.LIGHT -> UiModeManager.MODE_NIGHT_NO
                Theme.DARK -> UiModeManager.MODE_NIGHT_YES
                else -> UiModeManager.MODE_NIGHT_AUTO
            }
        }

        if (DynamicColors.isDynamicColorAvailable()) {
            setTheme(
                when {
                    dynamicColorsEnabled -> R.style.Theme_MQTTHub
                    else -> R.style.Theme_MQTTHub_NoDynamicColors
                }
            )

            if (!viewModel.themeIsInitialized) {
                viewModel.themeIsInitialized = true

                if (themeId != Theme.SYSTEM && dynamicColorsEnabled) {
                    recreate()
                }
            }
        }
    }

    private fun startBrokerConnectionService() {
        startService(Intent(this, BrokerConnectionService::class.java))
    }
}