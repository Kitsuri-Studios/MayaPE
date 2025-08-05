package com.kitsuri.msa.rapidfetch

import android.util.Log
import net.lenni0451.commons.httpclient.HttpClient
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode
import java.util.concurrent.CompletableFuture

class AuthSession internal constructor(
    private val httpClient: HttpClient,
    private val sessionManager: SessionManager,
    private val callback: AuthCallback
) {

    companion object {
        private const val TAG = "AuthSession"
    }

    private var authFuture: CompletableFuture<StepFullBedrockSession.FullBedrockSession>? = null

    fun start() {
        authFuture = CompletableFuture.supplyAsync {
            try {
                Log.d(TAG, "Starting authentication flow")
                val session = sessionManager.authFlow.getFromInput(httpClient,
                    StepMsaDeviceCode.MsaDeviceCodeCallback { msaDeviceCode ->
                        Log.d(TAG, "Device code received: ${msaDeviceCode.userCode}")
                        callback.onDeviceCodeReceived(msaDeviceCode.userCode, msaDeviceCode.verificationUri)
                    }
                ) as StepFullBedrockSession.FullBedrockSession

                if (session.realmsXsts == null) {
                    throw IllegalStateException("Authentication succeeded but realmsXsts token is missing")
                }

                sessionManager.saveSession(session)
                session
            } catch (e: Exception) {
                Log.e(TAG, "Authentication failed", e)
                callback.onAuthError(e.message ?: "Unknown error")
                throw e
            }
        }.whenComplete { session, throwable ->
            when {
                throwable != null -> {
                    if (!throwable.isCancellation()) {
                        callback.onAuthError(throwable.message ?: "Unknown error")
                    }
                }
                session != null -> {
                    callback.onAuthSuccess(session)
                }
            }
        }
    }

    private fun Throwable.isCancellation(): Boolean {
        return this is java.util.concurrent.CancellationException ||
                this.cause is java.util.concurrent.CancellationException
    }

    fun cancel() {
        authFuture?.cancel(true)
    }
}