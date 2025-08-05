package io.kitsuri.mayape.ui.components.landing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthenticationDialog(
    showWebView: Boolean,
    authState: LoginState,
    userCode: String?,
    verificationUri: String?,
    onDismiss: () -> Unit,
    onLogin: () -> Unit,
    onCopyCode: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(660.dp)
            .fillMaxHeight(0.85f)
            .shadow(32.dp, RoundedCornerShape(20.dp))
    ) {
        // Blurred background layer
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    renderEffect = BlurEffect(
                        radiusX = 10f,
                        radiusY = 10f,
                        edgeTreatment = TileMode.Clamp
                    )
                },
            color = Color(0x28FFFFFF),
            shape = RoundedCornerShape(20.dp)
        ) {}

        // Content layer without blur
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {},
            color = Color.Transparent,
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val cardWidth by animateDpAsState(
                    targetValue = if (showWebView || authState is LoginState.Error || authState == LoginState.Success) 260.dp else 660.dp,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
                    label = "cardWidth"
                )
                val cardOffsetX by animateDpAsState(
                    targetValue = if (showWebView || authState is LoginState.Error || authState == LoginState.Success) 0.dp else 0.dp,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
                    label = "cardOffsetX"
                )

                Surface(
                    modifier = Modifier
                        .width(cardWidth)
                        .fillMaxHeight()
                        .offset(x = cardOffsetX)
                        .clip(
                            if (showWebView || authState is LoginState.Error || authState == LoginState.Success)
                                RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                            else
                                RoundedCornerShape(20.dp)
                        ),
                    color = Color(0x28FFFFFF),
                    shape = if (showWebView || authState is LoginState.Error || authState == LoginState.Success)
                        RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    else
                        RoundedCornerShape(20.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.02f),
                                            Color.Transparent,
                                            Color.Gray.copy(alpha = 0.05f)
                                        ),
                                        radius = 300f
                                    )
                                )
                        )
                        ControlPanel(
                            authState = authState,
                            userCode = userCode,
                            onDismiss = onDismiss,
                            onLogin = onLogin,
                            onCopyCode = onCopyCode,
                            isCompact = showWebView || authState is LoginState.Error || authState == LoginState.Success
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showWebView && authState != LoginState.Success && authState !is LoginState.Error,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(1000, 200, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(800, 400)),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(800, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(600))
                ) {
                    Surface(
                        modifier = Modifier
                            .width(400.dp)
                            .fillMaxHeight()
                            .offset(x = 260.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                    ) {
                        if (verificationUri != null && userCode != null) {
                            AuthenticationWebView(
                                url = verificationUri,
                                code = userCode
                            )
                        } else {
                            WebViewPlaceholder()
                        }
                    }
                }

                AnimatedVisibility(
                    visible = authState is LoginState.Error,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(1000, 200, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(800, 400)),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(800, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(600))
                ) {
                    Surface(
                        modifier = Modifier
                            .width(400.dp)
                            .fillMaxHeight()
                            .offset(x = 260.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = onLogin,
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0078D4)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Try Again",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}