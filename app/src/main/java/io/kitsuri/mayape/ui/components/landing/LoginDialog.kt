package io.kitsuri.mayape.ui.components.landing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.zIndex
import com.kitsuri.msa.rapidfetch.AuthCallback
import com.kitsuri.msa.rapidfetch.AuthSession
import com.kitsuri.msa.rapidfetch.FletchLinkManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onAuthSuccess: (StepFullBedrockSession.FullBedrockSession) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val authManager = remember { FletchLinkManager.getInstance(context) }
    var authState by remember { mutableStateOf<LoginState>(LoginState.Initial) }
    var userCode by remember { mutableStateOf<String?>(null) }
    var verificationUri by remember { mutableStateOf<String?>(null) }
    var authSession by remember { mutableStateOf<AuthSession?>(null) }
    var showOverlay by remember { mutableStateOf(false) }
    var showWebView by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showOverlay = true
    }

    val authCallback = remember {
        object : AuthCallback {
            override fun onDeviceCodeReceived(receivedUserCode: String, receivedVerificationUri: String) {
                authState = LoginState.AwaitingAuth
                userCode = receivedUserCode
                verificationUri = receivedVerificationUri
                showWebView = true
            }

            override fun onAuthSuccess(session: StepFullBedrockSession.FullBedrockSession) {
                authState = LoginState.Success
                onAuthSuccess(session)
            }

            override fun onAuthError(error: String) {
                authState = LoginState.Error(error)
                showWebView = false
            }
        }
    }

    AnimatedVisibility(
        visible = showOverlay,
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(300)),
        modifier = Modifier.zIndex(10f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        showOverlay = false
                        kotlin.runCatching {
                            kotlinx.coroutines.GlobalScope.launch {
                                delay(300)
                                onDismiss()
                            }
                        }
                    }
            )
            AnimatedVisibility(
                visible = showOverlay,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(600)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(400))
            ) {
                AuthenticationDialog(
                    showWebView = showWebView,
                    authState = authState,
                    userCode = userCode,
                    verificationUri = verificationUri,
                    onDismiss = {
                        showOverlay = false
                        kotlin.runCatching {
                            kotlinx.coroutines.GlobalScope.launch {
                                delay(300)
                                onDismiss()
                            }
                        }
                    },
                    onLogin = {
                        authState = LoginState.Loading
                        authSession = authManager.startAuthFlow(authCallback)
                        authSession?.start()
                    },
                    onCopyCode = {
                        userCode?.let { code ->
                            clipboardManager.setText(AnnotatedString(code))
                        }
                    }
                )
            }
        }
    }
}