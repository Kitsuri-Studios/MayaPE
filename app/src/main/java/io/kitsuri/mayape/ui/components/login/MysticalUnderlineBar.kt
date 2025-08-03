package io.kitsuri.mayape.ui.components.login

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
 fun MysticalUnderlineBar() {
    var barVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(800)
        barVisible = true
    }

    val lineWidthAnimatable = remember { Animatable(0f) }

    LaunchedEffect(barVisible) {
        if (barVisible) {
            lineWidthAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1200,
                    easing = EaseOutQuart
                )
            )
        }
    }

    // Mystical glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "mystical_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glow effect behind the bar
        Box(
            modifier = Modifier
                .width((90 * lineWidthAnimatable.value).dp)
                .height(6.dp)
                .alpha(glowAlpha * 0.4f)
                .blur(8.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFFF9CD1).copy(alpha = 0.8f),
                            Color(0xFFEC1885).copy(alpha = 0.9f),
                            Color(0xFFFF2797).copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main mystical bar with pointy faded ends
        Box(
            modifier = Modifier
                .offset(y = (-3).dp)
                .width((90 * lineWidthAnimatable.value).dp)
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFDB3762).copy(alpha = 0.3f),
                            Color(0xFFFF0090).copy(alpha = 0.9f),
                            Color(0xFFDB3762).copy(alpha = 0.9f),
                            Color(0xFFFB0081).copy(alpha = 0.9f),
                            Color(0xFFDB3762).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Additional mystical sparkle effect
        Box(
            modifier = Modifier
                .offset(y = (-6).dp)
                .width((60 * lineWidthAnimatable.value).dp)
                .height(1.dp)
                .alpha(glowAlpha * 0.6f)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}