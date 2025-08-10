package io.kitsuri.mayape.manager

import androidx.compose.ui.graphics.Color

data class ParticleState(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float
)

data class StarParticle(
    val angle: Float,
    val distance: Float,
    val size: Float,
    val speed: Float
)

data class SettingItem(
    val key: String,
    val title: String,
    val type: SettingType,
    val defaultValue: Any = "",
    val description: String? = null,
    val minValue: Float = 0f,
    val maxValue: Float = 100f,
    val suffix: String = "",
    val onClick: (() -> Unit)? = null,
    val buttonColor: Color = Color(0xFF4CAF50),
    val isReadOnly: Boolean = false
)

enum class SettingType {
    SWITCH, CHECKBOX, SLIDER, TEXT_FIELD, BUTTON, INFO
}