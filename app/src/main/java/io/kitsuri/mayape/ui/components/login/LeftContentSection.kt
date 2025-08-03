package io.kitsuri.mayape.ui.components.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
 fun LeftContentSection() {
    Column(
        modifier = Modifier
            .width(480.dp)
            .padding(start = 40.dp, end = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        // Smoother fade-in title
        var titleVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(400)
            titleVisible = true
        }

        AnimatedVisibility(
            visible = titleVisible,
            enter = fadeIn(animationSpec = tween(1000, easing = EaseOutQuart)) +
                    slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(1000, easing = EaseOutQuart)
                    )
        ) {
            Text(
                text = "Welcome To Maya",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Smoother fade-in description
        var descriptionVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(800)
            descriptionVisible = true
        }

        AnimatedVisibility(
            visible = descriptionVisible,
            enter = fadeIn(animationSpec = tween(800, easing = EaseOutQuart)) +
                    slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(800, easing = EaseOutQuart)
                    )
        ) {
            Text(
                text = "Best Minecraft Launcher & Mod Loader For Android.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Smoother arrows fade-in
        var arrowsVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(1200)
            arrowsVisible = true
        }

        AnimatedVisibility(
            visible = arrowsVisible,
            enter = fadeIn(animationSpec = tween(600, easing = EaseOutQuart)) +
                    slideInVertically(
                        initialOffsetY = { 15 },
                        animationSpec = tween(600, easing = EaseOutQuart)
                    )
        ) {
            NavigationArrows()
        }
    }
}