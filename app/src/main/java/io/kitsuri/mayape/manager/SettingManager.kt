package io.kitsuri.mayape.manager

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

class SettingsManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
    private val _settingsList = mutableStateListOf<SettingItem>()
    val settingsList: List<SettingItem> = _settingsList
    private val listeners = mutableListOf<SettingsChangeListener>()

    interface SettingsChangeListener {
        fun onSettingChanged(key: String, value: Any)
    }

    fun addSetting(setting: SettingItem) {
        _settingsList.add(setting)
    }

    fun addSwitch(
        key: String,
        title: String,
        defaultValue: Boolean = false,
        description: String? = null
    ) {
        addSetting(
            SettingItem(
                key = key,
                title = title,
                type = SettingType.SWITCH,
                defaultValue = defaultValue,
                description = description
            )
        )
    }

    fun addCheckbox(
        key: String,
        title: String,
        defaultValue: Boolean = false,
        description: String? = null
    ) {
        addSetting(
            SettingItem(
                key = key,
                title = title,
                type = SettingType.CHECKBOX,
                defaultValue = defaultValue,
                description = description
            )
        )
    }

    fun addSlider(
        key: String,
        title: String,
        defaultValue: Float,
        minValue: Float = 0f,
        maxValue: Float = 100f,
        suffix: String = "",
        description: String? = null
    ) {
        addSetting(
            SettingItem(
                key = key,
                title = title,
                type = SettingType.SLIDER,
                defaultValue = defaultValue,
                minValue = minValue,
                maxValue = maxValue,
                suffix = suffix,
                description = description
            )
        )
    }

    fun addTextField(
        key: String,
        title: String,
        defaultValue: String = "",
        isReadOnly: Boolean = false,
        description: String? = null
    ) {
        addSetting(
            SettingItem(
                key = key,
                title = title,
                type = SettingType.TEXT_FIELD,
                defaultValue = defaultValue,
                isReadOnly = isReadOnly,
                description = description
            )
        )
    }

    fun addButton(
        key: String,
        title: String,
        buttonColor: Color = Color(0xFF4CAF50),
        onClick: () -> Unit
    ) {
        addSetting(
            SettingItem(
                key = key,
                title = title,
                type = SettingType.BUTTON,
                buttonColor = buttonColor,
                onClick = onClick
            )
        )
    }

    fun addInfo(
        key: String,
        title: String
    ) {
        addSetting(
            SettingItem(
                key = key,
                title = title,
                type = SettingType.INFO
            )
        )
    }

    fun addDivider() {
        // Add an invisible info item as divider
        addSetting(
            SettingItem(
                key = "divider_${System.currentTimeMillis()}",
                title = "",
                type = SettingType.INFO
            )
        )
    }

    fun clearSettings() {
        _settingsList.clear()
    }

    fun removeSetting(key: String) {
        _settingsList.removeAll { it.key == key }
    }

    // Value getters
    fun getBooleanValue(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun getStringValue(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    fun getFloatValue(key: String, defaultValue: Float = 0f): Float {
        return try {
            val stringValue = prefs.getString(key, null)
            if (stringValue != null) {
                stringValue.toFloatOrNull() ?: prefs.getFloat(key, defaultValue)
            } else {
                prefs.getFloat(key, defaultValue)
            }
        } catch (e: ClassCastException) {
            prefs.getFloat(key, defaultValue)
        }
    }

    // Value setters
    fun setBooleanValue(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
        notifyListeners(key, value)
    }

    fun setStringValue(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
        notifyListeners(key, value)
    }

    fun setFloatValue(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
        notifyListeners(key, value)
    }

    // Listener management
    fun addSettingsChangeListener(listener: SettingsChangeListener) {
        listeners.add(listener)
    }

    fun removeSettingsChangeListener(listener: SettingsChangeListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners(key: String, value: Any) {
        listeners.forEach { it.onSettingChanged(key, value) }
    }
}