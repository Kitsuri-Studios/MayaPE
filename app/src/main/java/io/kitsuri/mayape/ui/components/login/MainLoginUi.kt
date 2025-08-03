package io.kitsuri.mayape.ui.components.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.kitsuri.mayape.R

@Composable
fun MainLoginUi() {
    // Animation states
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg3),
                contentScale = ContentScale.Crop
            )
    ) {
        // Animated background overlay
        AnimatedBackgroundOverlay()

        // Floating particles background
        FloatingParticles()

        // Main content container
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side content
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(1200, delayMillis = 200, easing = EaseOutQuart)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 200))
            ) {
                LeftContentSection()
            }

            // Right side login card
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(
                    initialScale = 0.7f,
                    animationSpec = tween(1000, delayMillis = 500, easing = EaseOutBack)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 500))
            ) {
                LoginCard(
                    onLoginClick = {  }
                )
            }
        }

        // Top bar with slide-in animation
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(1000, easing = EaseOutQuart)
            ) + fadeIn(animationSpec = tween(1000)),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            TopBarSection(
                onCloseClick = {  }
            )
        }

        // Social bar at bottom
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(1000, delayMillis = 800, easing = EaseOutQuart)
            ) + fadeIn(animationSpec = tween(800, delayMillis = 800)),
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            SocialBarSection()
        }
    }
}