package io.kitsuri.mayape.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import io.kitsuri.mayape.R

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
val montserratFontFamily = FontFamily(
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)