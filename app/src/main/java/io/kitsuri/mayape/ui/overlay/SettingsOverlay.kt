package io.kitsuri.mayape.ui.overlay

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kitsuri.mayape.manager.NativeBlurManager
import io.kitsuri.mayape.manager.SettingItem
import io.kitsuri.mayape.manager.SettingType
import io.kitsuri.mayape.manager.SettingsManager



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsOverlay(
    isVisible: Boolean,
    onClose: () -> Unit,
    settingsManager: SettingsManager,
    useNativeBlur: Boolean = NativeBlurManager.isNativeBlurSupported()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    LaunchedEffect(isVisible) {
        if (useNativeBlur && activity != null) {
            NativeBlurManager.applyBlur(activity, isVisible)


        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (useNativeBlur && activity != null) {
                NativeBlurManager.applyBlur(activity, false)
            }
        }
    }

    val settingStates = remember { mutableMapOf<String, MutableState<Any>>() }

    LaunchedEffect(settingsManager.settingsList) {
        settingsManager.settingsList.forEach { setting ->
            if (!settingStates.containsKey(setting.key)) {
                val savedValue = when (setting.type) {
                    SettingType.SWITCH, SettingType.CHECKBOX -> {
                        settingsManager.getBooleanValue(setting.key, setting.defaultValue as Boolean)
                    }
                    SettingType.SLIDER -> {
                        settingsManager.getFloatValue(setting.key, setting.defaultValue as Float)
                    }
                    SettingType.TEXT_FIELD -> {
                        settingsManager.getStringValue(setting.key, setting.defaultValue as String)
                    }
                    else -> setting.defaultValue
                }
                settingStates[setting.key] = mutableStateOf(savedValue)
            }
        }
    }


    // Save preference function
    fun savePreference(key: String, value: Any) {
        when (value) {
            is Boolean -> settingsManager.setBooleanValue(key, value)
            is String -> settingsManager.setStringValue(key, value)
            is Float -> settingsManager.setFloatValue(key, value)
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { it },
        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(animationSpec = tween(300)) { it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = if (useNativeBlur) {
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onClose() }
                } else {
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .blur(radius = 8.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onClose() }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.85f)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close Settings",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (settingsManager.settingsList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No settings configured",
                            color = Color(0xFF757575),
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        settingsManager.settingsList.forEach { setting ->
                            val state = settingStates[setting.key]
                            if (state != null) {
                                SettingItemView(
                                    setting = setting,
                                    currentValue = state.value,
                                    onValueChange = { newValue ->
                                        state.value = newValue
                                        if (setting.type != SettingType.INFO && setting.type != SettingType.BUTTON) {
                                            savePreference(setting.key, newValue)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingItemView(
    setting: SettingItem,
    currentValue: Any,
    onValueChange: (Any) -> Unit
) {
    when (setting.type) {
        SettingType.TEXT_FIELD -> {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = setting.title,
                    color = Color(0xFFB0B0B0),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentValue as String,
                        onValueChange = { if (!setting.isReadOnly) onValueChange(it) },
                        modifier = Modifier.weight(1f),
                        readOnly = setting.isReadOnly,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color(0xFF2D2D2D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF2196F3)
                        ),
                        singleLine = true
                    )
                    if (setting.key.contains("Location") || setting.key.contains("location")) {
                        IconButton(
                            onClick = { /* Handle folder selection */ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FolderOpen,
                                contentDescription = "Browse",
                                tint = Color(0xFFB0B0B0),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        SettingType.SWITCH -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = setting.title,
                    color = Color(0xFFB0B0B0),
                    fontSize = 12.sp
                )
                Switch(
                    checked = currentValue as Boolean,
                    onCheckedChange = { onValueChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        uncheckedThumbColor = Color(0xFF4A4A4A),
                        checkedTrackColor = Color(0xFF2196F3),
                        uncheckedTrackColor = Color(0xFF2D2D2D)
                    )
                )
            }
        }

        SettingType.CHECKBOX -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = setting.title,
                    color = Color(0xFFB0B0B0),
                    fontSize = 12.sp
                )
                Checkbox(
                    checked = currentValue as Boolean,
                    onCheckedChange = { onValueChange(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF2196F3),
                        uncheckedColor = Color(0xFF4A4A4A),
                        checkmarkColor = Color.White
                    )
                )
            }
        }

        SettingType.SLIDER -> {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = setting.title,
                        color = Color(0xFFB0B0B0),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${(currentValue as Float).toInt()}${setting.suffix}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Slider(
                    value = currentValue as Float,
                    onValueChange = { onValueChange(it) },
                    valueRange = setting.minValue..setting.maxValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .height(20.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color(0xFF2196F3),
                        inactiveTrackColor = Color(0xFF2D2D2D)
                    ),
                    track = { sliderState ->
                        SliderDefaults.Track(
                            sliderState = sliderState,
                            modifier = Modifier.height(2.dp),
                            colors = SliderDefaults.colors(
                                activeTrackColor = Color(0xFF2196F3),
                                inactiveTrackColor = Color(0xFF2D2D2D)
                            )
                        )
                    },
                    thumb = {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            modifier = Modifier.size(16.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White
                            )
                        )
                    }
                )
            }
        }

        SettingType.BUTTON -> {
            Button(
                onClick = { setting.onClick?.invoke() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = setting.buttonColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = setting.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        SettingType.INFO -> {
            if (setting.title.isNotEmpty()) {
                Text(
                    text = setting.title,
                    color = Color(0xFF757575),
                    fontSize = 10.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}