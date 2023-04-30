package com.github.burachevsky.mqtthub.feature.settings

import androidx.lifecycle.ViewModel
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.viewModelContainer
import com.github.burachevsky.mqtthub.common.event.SwitchTheme
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.widget.SubtitleItem
import com.github.burachevsky.mqtthub.common.widget.SwitchItem
import com.github.burachevsky.mqtthub.common.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.common.widget.ToggleOption
import com.github.burachevsky.mqtthub.data.settings.Settings
import com.github.burachevsky.mqtthub.data.settings.Theme
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settings: Settings,
    private val eventBus: EventBus,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    private val _items: MutableStateFlow<List<ListItem>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    fun toggleGroupSelectionChanged(position: Int) {
        val item = items.get<ToggleGroupItem>(position)

        when (item.id) {
            THEME_SELECTOR_ID -> {
                settings.theme = item.selectedValue
                switchTheme()
            }
        }
    }

    fun recreate() {
        _items.update { list() }
    }

    private fun list(): List<ListItem> {
        return listOf(
            SubtitleItem(Txt.of(R.string.settings_appearance)),
            SwitchItem(
                text = Txt.of(R.string.settings_dynamic_colors),
                marginTopRes = R.dimen.switch_item_margin_top_small,
                isChecked = settings.dynamicColorsEnabled,
                onCheckChanged = {
                    settings.dynamicColorsEnabled = it
                    switchTheme()
                }
            ),
            ToggleGroupItem(
                title = Txt.of(R.string.settings_theme),
                options = listOf(
                    ToggleOption(
                        id = Theme.SYSTEM,
                        text = Txt.of(R.string.settings_theme_system)
                    ),
                    ToggleOption(
                        id = Theme.LIGHT,
                        text = Txt.of(R.string.settings_theme_light)
                    ),
                    ToggleOption(
                        id = Theme.DARK,
                        text = Txt.of(R.string.settings_theme_dark)
                    )
                ),
                selectedValue = settings.theme,
                marginTopRes = R.dimen.switch_item_margin_top_small,
                isVertical = false,
            )
        )
    }

    private fun switchTheme() {
        container.launch(Dispatchers.Main) {
            eventBus.send(SwitchTheme)
        }
    }

    companion object {
        private const val THEME_SELECTOR_ID = 0
    }
}