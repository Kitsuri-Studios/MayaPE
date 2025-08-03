package io.kitsuri.mayape.ui.components.login

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.kitsuri.mayape.R

@Composable
 fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_animation")

    //  floating animation
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_float"
    )

    // Subtle glow pulsing
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_glow"
    )

    // Gentle scale breathing
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    Box(
        modifier = Modifier.size(74.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect behind logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .alpha(glowAlpha * 0.4f)
                .blur(12.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFEC1885).copy(alpha = 0.6f),
                            Color.Transparent
                        ),
                        radius = 40f
                    ),
                    CircleShape
                )
        )

        // Main logo with animations
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Maya Logo",
            modifier = Modifier
                .size(64.dp)
                .scale(scale)
                .offset(y = offsetY.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )

        // Subtle inner glow
        Box(
            modifier = Modifier
                .size(64.dp)
                .scale(scale)
                .offset(y = offsetY.dp)
                .alpha(glowAlpha * 0.2f)
                .blur(4.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        radius = 32f
                    ),
                    CircleShape
                )
        )
    }
}