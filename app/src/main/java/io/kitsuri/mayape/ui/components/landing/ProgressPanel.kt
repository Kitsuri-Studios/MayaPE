package io.kitsuri.mayape.ui.components.landing

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProgressPanel(
    progress: Float,
    statusMessage: String,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "progress"
    )
    val progressBarHeight by animateDpAsState(
        targetValue = if (progress > 0f) 2.dp else 2.dp,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "progressHeight"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (progress > 0f) 0.8f else 0.3f,
        animationSpec = tween(600, easing = LinearEasing),
        label = "glowAlpha"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .graphicsLayer {
                    renderEffect = BlurEffect(
                        radiusX = 10f,
                        radiusY = 10f,
                        edgeTreatment = TileMode.Clamp
                    )
                },
            color = Color(0x28FFFFFF),
            shape = RectangleShape
        ) {}

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(progressBarHeight)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(progressBarHeight)
                    .background(Color.White.copy(alpha = 0.15f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(progressBarHeight)
                    .background(Color.White)
                    .shadow(
                        elevation = if (progress > 0f) 12.dp else 0.dp,
                        ambientColor = Color(0xFFFFFFFF).copy(alpha = glowAlpha),
                        spotColor = Color(0xFFFFFFFF).copy(alpha = glowAlpha)
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .zIndex(2f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.White,
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AnimatedContent(
                        targetState = statusMessage,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with
                                    fadeOut(animationSpec = tween(200))
                        }
                    ) { text ->
                        Text(
                            text = text,
                            color = Color(0xFFF1F1F1),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}