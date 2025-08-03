package io.kitsuri.mayape.ui.components.login

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.kitsuri.mayape.manager.ParticleState
import kotlin.random.Random

@Composable
 fun FloatingParticles() {
    val particles = remember {
        List(35) {
            ParticleState(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2.5f + 1f,
                speed = Random.nextFloat() * 0.4f + 0.2f
            )
        }
    }

    particles.forEach { particle ->
        FloatingParticle(particle)
    }
}

@Composable
 fun FloatingParticle(particle: ParticleState) {
    val infiniteTransition = rememberInfiniteTransition(label = "particle")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween((8000 / particle.speed).toInt(), easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_x"
    )
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween((6000 / particle.speed).toInt(), easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_y"
    )

    Box(
        modifier = Modifier
            .absoluteOffset(
                x = (particle.x * 1200).dp + offsetX.dp,
                y = (particle.y * 700).dp + offsetY.dp
            )
            .size(particle.size.dp)
            .background(
                Color.White.copy(alpha = 0.3f),
                CircleShape
            )
    )
}