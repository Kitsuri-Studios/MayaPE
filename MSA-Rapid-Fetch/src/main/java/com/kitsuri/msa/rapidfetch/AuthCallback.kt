package com.kitsuri.msa.rapidfetch

import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession

interface AuthCallback {
    fun onDeviceCodeReceived(userCode: String, verificationUri: String)
    fun onAuthSuccess(session: StepFullBedrockSession.FullBedrockSession)
    fun onAuthError(error: String)
}