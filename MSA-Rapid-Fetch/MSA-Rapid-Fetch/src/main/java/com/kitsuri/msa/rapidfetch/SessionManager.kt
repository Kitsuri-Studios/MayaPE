package com.kitsuri.msa.rapidfetch

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.lenni0451.commons.httpclient.HttpClient
import net.raphimc.minecraftauth.MinecraftAuth
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession
import net.raphimc.minecraftauth.util.MicrosoftConstants
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

internal class SessionManager(private val context: Context) {

    companion object {
        private const val TAG = "SessionManager"
        private const val SESSION_FILE = "bedrock_session.json"
    }

    internal val authFlow = MinecraftAuth.builder()
        .withClientId(MicrosoftConstants.BEDROCK_ANDROID_TITLE_ID)
        .withScope(MicrosoftConstants.SCOPE_TITLE_AUTH)
        .deviceCode()
        .withDeviceToken("Android")
        .sisuTitleAuthentication(MicrosoftConstants.BEDROCK_XSTS_RELYING_PARTY)
        .buildMinecraftBedrockChainStep(true, true)

    fun loadSavedSession(httpClient: HttpClient): StepFullBedrockSession.FullBedrockSession? {
        return try {
            val file = File(context.filesDir, SESSION_FILE)
            if (!file.exists()) {
                Log.d(TAG, "Session file does not exist")
                return null
            }

            val jsonString = FileInputStream(file).use { fis ->
                fis.readBytes().toString(Charsets.UTF_8)
            }

            if (jsonString.isBlank()) {
                Log.e(TAG, "Session file is empty")
                deleteSession()
                return null
            }

            val json = JsonParser.parseString(jsonString) as JsonObject
            val session = authFlow.fromJson(json)

            if (session.realmsXsts == null) {
                Log.e(TAG, "Session missing realmsXsts token, discarding")
                deleteSession()
                return null
            }

            if (session.isExpiredOrOutdated()) {
                Log.d(TAG, "Session is expired/outdated, attempting to refresh")
                try {
                    val refreshedSession = authFlow.refresh(httpClient, session)
                    if (refreshedSession.realmsXsts == null) {
                        Log.e(TAG, "Refreshed session missing realmsXsts token")
                        deleteSession()
                        return null
                    }
                    saveSession(refreshedSession)
                    refreshedSession
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to refresh session", e)
                    deleteSession()
                    return null
                }
            } else {
                session
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load session", e)
            deleteSession()
            null
        }
    }

    fun saveSession(session: StepFullBedrockSession.FullBedrockSession) {
        try {
            val json = authFlow.toJson(session)
            val file = File(context.filesDir, SESSION_FILE)
            FileOutputStream(file).use { fos ->
                fos.write(json.toString().toByteArray())
                Log.d(TAG, "Session saved to ${file.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save session", e)
            throw e
        }
    }

    fun deleteSession() {
        try {
            val file = File(context.filesDir, SESSION_FILE)
            if (file.exists()) {
                if (file.delete()) {
                    Log.d(TAG, "Session file deleted")
                } else {
                    Log.e(TAG, "Failed to delete session file")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting session file", e)
        }
    }
}