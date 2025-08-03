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
import io.kitsuri.mayape.manager.StarParticle
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
 fun LogoParticles() {
    val particles = remember {
        List(8) { index ->
            StarParticle(
                angle = Random.nextFloat() * 360f,
                distance = Random.nextFloat() * 45f + 35f,
                size = Random.nextFloat() * 2.5f + 1.5f,
                speed = Random.nextFloat() * 0.6f + 0.4f
            )
        }
    }

    particles.forEach { particle ->
        AnimatedStarParticle(particle)
    }
}

@Composable
 fun AnimatedStarParticle(particle: StarParticle) {
    val infiniteTransition = rememberInfiniteTransition(label = "star_particle")

    val distance by infiniteTransition.animateFloat(
        initialValue = 25f,
        targetValue = particle.distance,
        animationSpec = infiniteRepeatable(
            animation = tween((4000 / particle.speed).toInt(), easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_distance"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween((3000 / particle.speed).toInt(), easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_alpha"
    )

    // Centered around logo position (logo is at center of 74dp box, which is at center of card)
    val logoCenter = 110f // Center X of card
    val logoCenterY = 180f // Moved up to center around logo area

    val x = logoCenter + distance * cos(particle.angle * Math.PI / 180).toFloat()
    val y = logoCenterY + distance * sin(particle.angle * Math.PI / 180).toFloat()

    Box(
        modifier = Modifier
            .absoluteOffset(x = x.dp, y = y.dp)
            .size(particle.size.dp)
            .background(
                Color(0xFFF6C13B).copy(alpha = alpha),
                CircleShape
            )
    )
}