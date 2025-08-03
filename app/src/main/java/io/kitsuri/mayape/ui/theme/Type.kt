package io.kitsuri.mayape.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color

// Set of Material typography styles to start with
fun typography(): Typography {
    return Typography().run {
        copy(
            displayLarge = displayLarge.copy(color = Color.White),
            displayMedium = displayMedium.copy(color = Color.White),
            displaySmall = displaySmall.copy(color = Color.White),
            headlineLarge = headlineLarge.copy(color = Color.White),
            headlineMedium = headlineMedium.copy(color = Color.White),
            headlineSmall = headlineSmall.copy(color = Color.White),
            titleLarge = titleLarge.copy(color = Color.White),
            titleMedium = titleMedium.copy(color = Color.White),
            titleSmall = titleSmall.copy(color = Color.White),
            bodyLarge = bodyLarge.copy(color = Color.White),
            bodyMedium = bodyMedium.copy(color = Color.White),
            bodySmall = bodySmall.copy(color = Color.White),
            labelLarge = labelLarge.copy(color = Color.White),
            labelMedium = labelMedium.copy(color = Color.White),
            labelSmall = labelSmall.copy(color = Color.White),
        )
    }
}