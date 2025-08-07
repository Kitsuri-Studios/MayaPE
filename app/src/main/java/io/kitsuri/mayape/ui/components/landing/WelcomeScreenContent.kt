package io.kitsuri.mayape.ui.components.landing

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kitsuri.mayape.R
import io.kitsuri.mayape.application.HomeActivity
import io.kitsuri.mayape.ui.components.login.AnimatedLogo
import io.kitsuri.mayape.ui.components.misc.FlickeringStartButton
import kotlinx.coroutines.delay
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession
import java.time.LocalTime

@Composable
fun WelcomeScreenContent(
    session: StepFullBedrockSession.FullBedrockSession?,
    requiresAuth: Boolean,
    isProcessing: Boolean,
    showProgressPanel: Boolean,
    progressValue: Float,
    statusMessage: String,
    onIntroAnimationDone: () -> Unit,
    onAuthSuccess: (StepFullBedrockSession.FullBedrockSession) -> Unit
) {
    var animationPhase by remember { mutableStateOf(AnimationPhase.INITIAL) }
    var showLogoCard by remember { mutableStateOf(false) }
    var showStartButton by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    val font = FontFamily(Font(R.font.light, FontWeight.Normal))

    val mayaPeOffset by animateFloatAsState(
        targetValue = when (animationPhase) {
            AnimationPhase.INITIAL -> 0f
            AnimationPhase.TITLE_COMBINED -> 0f
            AnimationPhase.TITLE_SPLIT -> -40f
        },
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "mayaPeOffset"
    )

    val kitsuriOffset by animateFloatAsState(
        targetValue = when (animationPhase) {
            AnimationPhase.INITIAL -> 0f
            AnimationPhase.TITLE_COMBINED -> 0f
            AnimationPhase.TITLE_SPLIT -> 70f
        },
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "kitsuriOffset"
    )

    LaunchedEffect(Unit) {
        delay(200) // Initial delay for greeting animation
        animationPhase = AnimationPhase.TITLE_COMBINED
        delay(2000)
        animationPhase = AnimationPhase.TITLE_SPLIT
        delay(800)
        showLogoCard = true
        delay(1200)
        showStartButton = true
        onIntroAnimationDone()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background content with conditional blur and dim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .graphicsLayer {
                    if (showLoginDialog) {
                        renderEffect = BlurEffect(
                            radiusX = 10f,
                            radiusY = 10f,
                            edgeTreatment = TileMode.Clamp
                        )
                    }
                }
                .alpha(if (showLoginDialog) 0.5f else 1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg3),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.6f
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.6f)
                            ),
                            radius = 1200f
                        )
                    )
            )

            AnimatedVisibility(
                visible = animationPhase == AnimationPhase.INITIAL,
                enter = fadeIn(animationSpec = tween(200)),
                exit = slideOutVertically(
                    targetOffsetY = { -it / 2 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(600)),
                modifier = Modifier.align(Alignment.Center)
            ) {
                val currentHour = LocalTime.now().hour
                val greeting = when {
                    currentHour in 0..11 -> "Good Morning"
                    currentHour in 12..16 -> "Good Afternoon"
                    currentHour in 17..20 -> "Good Evening"
                    else -> "Good Night"
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    greeting.forEachIndexed { index, char ->
                        AnimatedVisibility(
                            visible = animationPhase == AnimationPhase.INITIAL,
                            enter = slideInHorizontally(
                                initialOffsetX = { it * (if (index % 2 == 0) 1 else -1) },
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = 200 + index * 50,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = 200 + index * 50
                                )
                            ),
                            exit = fadeOut(animationSpec = tween(600))
                        ) {
                            Text(
                                text = char.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Light,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontFamily = font
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = animationPhase == AnimationPhase.TITLE_COMBINED || animationPhase == AnimationPhase.TITLE_SPLIT,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(800)),
                exit = fadeOut(animationSpec = tween(400)),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MayaPE By",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        modifier = Modifier.offset(x = mayaPeOffset.dp),
                        fontFamily = font
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Kitsuri Studios",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        modifier = Modifier.offset(x = kitsuriOffset.dp),
                        fontFamily = font
                    )
                }
            }

            AnimatedVisibility(
                visible = showLogoCard,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(1200, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(1200)),
                modifier = Modifier.align(Alignment.Center)
            ) {
                AnimatedLogo()
            }

            AnimatedVisibility(
                visible = !isProcessing && showStartButton,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(800)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(400)),
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                FlickeringStartButton(
                    modifier = Modifier.padding(32.dp),
                    text = if (!isProcessing && session != null) "Logged In" else "Log In",
                    onClick = { if (requiresAuth) showLoginDialog = true }
                )
            }

            AnimatedVisibility(
                visible = showProgressPanel,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(600)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(400)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                ProgressPanel(
                    progress = progressValue,
                    statusMessage = statusMessage
                )
            }
        }

        // LoginDialog outside the blurred background
        if (showLoginDialog) {
            LoginDialog(
                onDismiss = { showLoginDialog = false },
                onAuthSuccess = { session ->
                    showLoginDialog = false
                    onAuthSuccess(session)
                }
            )
        }

        val context = LocalContext.current

        LaunchedEffect(session, animationPhase) {
            if (session != null && animationPhase == AnimationPhase.TITLE_SPLIT) {
                delay(500)
                context.startActivity(Intent(context, HomeActivity::class.java))
                if (context is Activity) context.finish()
            }
        }

    }
}