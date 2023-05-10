package com.github.burachevsky.mqtthub

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.core.domain.usecase.dashboard.ImportDashboardFromFile
import com.github.burachevsky.mqtthub.core.domain.usecase.settings.GetSettings
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.model.Settings
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.event.StartNewMqttConnection
import com.github.burachevsky.mqtthub.core.ui.event.SwitchTheme
import com.github.burachevsky.mqtthub.core.ui.event.ToastMessage
import com.github.burachevsky.mqtthub.core.ui.ext.toast
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

class AppViewModel @Inject constructor(
    private val importDashboardFromFile: ImportDashboardFromFile,
    eventBus: EventBus,
    getSettings: GetSettings,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    private val settings: Settings = getSettings()

    var themeIsInitialized = false

    init {
        eventBus.apply {
            subscribe<ToastMessage>(viewModelScope, container::raiseEffect)
            subscribe<SwitchTheme>(viewModelScope, container::raiseEffect)
            subscribe<StartNewMqttConnection>(viewModelScope, container::raiseEffect)
        }
    }

    fun dynamicColorsEnabled(): Boolean {
        return settings.dynamicColorsEnabled
    }

    fun getTheme(): Int {
        return settings.theme
    }

    fun importDashboard(uri: Uri) {
        container.launch(Dispatchers.IO) {
            try {
                importDashboardFromFile(uri)
                toast(R.string.dashboard_imported_successfully)
            } catch (e: Exception) {
                Timber.e(e)
                toast(R.string.failed_to_import_dashboard)
            }
        }
    }
}