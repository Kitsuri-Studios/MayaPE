package io.kitsuri.mayape.application

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.kitsuri.mayape.R
import io.kitsuri.mayape.ui.components.HomePageContent
import io.kitsuri.mayape.ui.components.login.AnimatedLogo
import io.kitsuri.mayape.ui.components.login.LoginCard
import io.kitsuri.mayape.ui.components.login.LogoParticles
import io.kitsuri.mayape.ui.components.misc.FlickeringStartButton
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide status bar and make fullscreen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        enableEdgeToEdge()
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            MayaPETheme {
//                LandingPage()
                HomePageContent()
            }
        }
    }
}

@Composable
fun LandingPage() {
    var animationState by remember { mutableStateOf(AnimationState.GOOD_MORNING) }
    var showCard by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    val Font = FontFamily(
        Font(R.font.light, FontWeight.Normal)
    )
    val mayaPeOffsetX by animateFloatAsState(
        targetValue = when (animationState) {
            AnimationState.GOOD_MORNING -> 0f
            AnimationState.MAYA_PE_TOGETHER -> 0f
            AnimationState.MAYA_PE_SPLIT -> -40f
        },
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "mayaPeOffset"
    )

    val kitsuriOffsetX by animateFloatAsState(
        targetValue = when (animationState) {
            AnimationState.GOOD_MORNING -> 0f
            AnimationState.MAYA_PE_TOGETHER -> 0f
            AnimationState.MAYA_PE_SPLIT -> 70f
        },
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "kitsuriOffset"
    )

    LaunchedEffect(Unit) {
        delay(2500) // Show "Good Morning" for 2.5 seconds
        animationState = AnimationState.MAYA_PE_TOGETHER
        delay(2500) // Show "MayaPE By Kitsuri Studios" together for 2.5 seconds
        animationState = AnimationState.MAYA_PE_SPLIT
        delay(800) // Wait for split animation
        showCard = true
        delay(1200) // Wait for AnimatedLogo slide-in
        showButton = true // Show button after all animations
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        // Elegant gradient overlay for depth
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

        // Good Morning Text
        AnimatedVisibility(
            visible = animationState == AnimationState.GOOD_MORNING,
            enter = fadeIn(animationSpec = tween(800)),
            exit = fadeOut(animationSpec = tween(600)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Good Morning",
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontFamily = Font
            )
        }

        // MayaPE By Kitsuri Studios Text
        AnimatedVisibility(
            visible = animationState == AnimationState.MAYA_PE_TOGETHER || animationState == AnimationState.MAYA_PE_SPLIT,
            enter = fadeIn(animationSpec = tween(800)),
            exit = fadeOut(animationSpec = tween(400)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // MayaPE By
                Text(
                    text = "MayaPE By",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    modifier = Modifier.offset(x = mayaPeOffsetX.dp),
                    fontFamily = Font
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Kitsuri Studios
                Text(
                    text = "Kitsuri Studios",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    modifier = Modifier.offset(x = kitsuriOffsetX.dp),
                    fontFamily = Font
                )
            }
        }

        // Login Card
        AnimatedVisibility(
            visible = showCard,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 1200,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(animationSpec = tween(1200)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            AnimatedLogo()
        }

        // Flickering Start Button at Bottom Center
        AnimatedVisibility(
            visible = showButton,
            enter = slideInVertically(
                initialOffsetY = { it }, // Slide in from bottom
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(animationSpec = tween(800)),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            FlickeringStartButton(
                modifier = Modifier
                    .padding(32.dp) // Add some padding from the bottom edge
            ) { }
        }
    }
}

enum class AnimationState {
    GOOD_MORNING,
    MAYA_PE_TOGETHER,
    MAYA_PE_SPLIT
}

@Composable
fun MayaPETheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF0078D4),
            secondary = Color(0xFF1BA1E2),
            background = Color.Black,
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White
        ),
        typography = Typography(
            bodyLarge = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                color = Color.White
            )
        ),
        content = content
    )
}