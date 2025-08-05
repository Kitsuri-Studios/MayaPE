package com.kitsuri.msa.rapidfetch

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.lenni0451.commons.httpclient.HttpClient
import net.raphimc.minecraftauth.MinecraftAuth
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode
import net.raphimc.minecraftauth.util.MicrosoftConstants
import java.util.concurrent.CompletableFuture

/**
 * Main entry point for FletchLink Core functionality
 */
class FletchLinkManager private constructor(private val context: Context) {

    private val sessionManager = SessionManager(context)
    private val httpClient = MinecraftAuth.createHttpClient()

    companion object {
        private const val TAG = "FletchLinkManager"

        @Volatile
        private var INSTANCE: FletchLinkManager? = null

        fun getInstance(context: Context): FletchLinkManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FletchLinkManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /**
     * Check if user has a valid session
     */
    suspend fun hasValidSession(): Boolean = withContext(Dispatchers.IO) {
        val session = sessionManager.loadSavedSession(httpClient)
        session != null && session.realmsXsts != null && !session.isExpiredOrOutdated()
    }

    /**
     * Get current session if valid, null otherwise
     */
    suspend fun getCurrentSession(): StepFullBedrockSession.FullBedrockSession? = withContext(Dispatchers.IO) {
        sessionManager.loadSavedSession(httpClient)
    }

    /**
     * Start authentication flow
     */
    fun startAuthFlow(callback: AuthCallback): AuthSession {
        return AuthSession(httpClient, sessionManager, callback)
    }

    /**
     * Clear saved session
     */
    fun clearSession() {
        sessionManager.deleteSession()
    }

    /**
     * Get user info from current session
     */
    suspend fun getUserInfo(): UserInfo? = withContext(Dispatchers.IO) {
        getCurrentSession()?.let { session ->
            UserInfo(
                displayName = session.mcChain.displayName,
                uuid = session.mcChain.id.toString(),
                hasRealmsAccess = session.realmsXsts != null
            )
        }
    }
}