package io.kitsuri.mayape.ui.components.misc

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FlickeringStartButton(modifier: Modifier = Modifier, onClick: () -> Unit, text: String) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )

    // Animate underline width
    val underlineWidth = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        underlineWidth.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800, // Match LandingPage slide-in duration
                easing = FastOutSlowInEasing
            )
        )
    }

    // Press animation for underline (same as before)
    val pressLineWidthAnimatable = remember { Animatable(0f) }
    LaunchedEffect(isPressed) {
        pressLineWidthAnimatable.animateTo(
            targetValue = if (isPressed) 1f else 0f,
            animationSpec = tween(
                durationMillis = if (isPressed) 350 else 250,
                easing = if (isPressed) FastOutSlowInEasing else LinearEasing
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            color = Color.White,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                            onClick()
                        }
                    )
                }
        )


        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .width((40 * pressLineWidthAnimatable.value).dp) // Press animation underline
                .height(2.dp)
                .background(Color(0xFFF3CF75).copy(alpha = 0.4f))
                .clickable(onClick = onClick)
        )
    }
}