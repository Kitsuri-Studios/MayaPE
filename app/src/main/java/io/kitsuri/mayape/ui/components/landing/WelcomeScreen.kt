package io.kitsuri.mayape.ui.components.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kitsuri.msa.rapidfetch.FletchLinkManager
import kotlinx.coroutines.delay
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession

@Composable
fun WelcomeScreen() {
    val context = LocalContext.current
    val authManager = remember { FletchLinkManager.getInstance(context) }
    var userSession by remember { mutableStateOf<StepFullBedrockSession.FullBedrockSession?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var requiresAuth by remember { mutableStateOf(false) }
    var progressValue by remember { mutableStateOf(0f) }
    var statusMessage by remember { mutableStateOf("Initializing...") }
    var showProgressPanel by remember { mutableStateOf(false) }
    var introAnimationDone by remember { mutableStateOf(false) }

    LaunchedEffect(introAnimationDone) {
        if (introAnimationDone) {
            showProgressPanel = true
            isProcessing = true
            statusMessage = "Checking for saved session..."
            progressValue = 0.2f
            delay(800)

            val session = authManager.getCurrentSession()
            if (session == null) {
                statusMessage = "No saved session found"
                progressValue = 0.5f
                delay(600)
                statusMessage = "Authentication required"
                progressValue = 1f
                delay(300)
                requiresAuth = true
            } else {
                statusMessage = "Validating session tokens..."
                progressValue = 0.5f
                delay(600)

                if (session.realmsXsts == null) {
                    statusMessage = "Invalid session: missing realms token"
                    progressValue = 0.8f
                    delay(700)
                    statusMessage = "Authentication required"
                    progressValue = 1f
                    delay(300)
                    requiresAuth = true
                } else if (session.isExpiredOrOutdated()) {
                    statusMessage = "Session expired, attempting to refresh..."
                    progressValue = 0.8f
                    delay(700)
                    try {
                        val refreshedSession = authManager.getCurrentSession()
                        if (refreshedSession?.realmsXsts == null) {
                            statusMessage = "Failed to refresh: missing realms token"
                            progressValue = 1f
                            delay(300)
                            requiresAuth = true
                        } else {
                            statusMessage = "Session refreshed successfully"
                            progressValue = 1f
                            delay(400)
                            userSession = refreshedSession
                        }
                    } catch (e: Exception) {
                        statusMessage = "Failed to refresh session"
                        progressValue = 1f
                        delay(300)
                        requiresAuth = true
                    }
                } else {
                    statusMessage = "Session valid, loading profile..."
                    progressValue = 1f
                    delay(400)
                    userSession = session
                }
            }
            isProcessing = false
            delay(300)
            showProgressPanel = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        WelcomeScreenContent(
            session = userSession,
            requiresAuth = requiresAuth,
            isProcessing = isProcessing,
            showProgressPanel = showProgressPanel,
            progressValue = progressValue,
            statusMessage = statusMessage,
            onIntroAnimationDone = { introAnimationDone = true },
            onAuthSuccess = { authenticatedSession ->
                userSession = authenticatedSession
                requiresAuth = false
            }
        )
    }
}