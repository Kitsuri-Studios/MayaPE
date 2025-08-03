package io.kitsuri.mayape.manager

data class ParticleState(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float
)

data class StarParticle(
    val angle: Float,
    val distance: Float,
    val size: Float,
    val speed: Float
)