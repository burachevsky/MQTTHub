package com.github.burachevsky.mqtthub

import android.app.UiModeManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.common.event.SwitchTheme
import com.github.burachevsky.mqtthub.data.settings.Theme
import com.github.burachevsky.mqtthub.databinding.ActivityAppBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent
import com.github.burachevsky.mqtthub.domain.eventbus.AppEventHandler
import com.google.android.material.color.DynamicColors
import timber.log.Timber
import javax.inject.Inject

class AppActivity : AppCompatActivity(), ViewController<AppViewModel>, AppEventHandler {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AppViewModel>

    override val binding: ViewBinding by viewBinding(ActivityAppBinding::bind, R.id.appContainer)
    override val viewModel: AppViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("AppActivity-SwitchTheme: onCreate, ${AppCompatDelegate.getDefaultNightMode()}")
        (application as App).appComponent.inject(this)
        setupActivityAppearance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        findNavController().setGraph(R.navigation.app_graph)
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



    private fun findNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.appContainer) as NavHostFragment)
            .navController
    }

    override fun handleEffect(effect: AppEvent): Boolean {
        when (effect) {
            is SwitchTheme -> {
                viewModel.themeIsInitialized = false
                recreate()
                return true
            }
        }

        return false
    }

    companion object {
        var statusBarHeight: Int = 0
        var navigationBarHeight: Int = 0
    }
}