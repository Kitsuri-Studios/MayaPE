package io.kitsuri.mayape.ui.components.landing

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    object AwaitingAuth : LoginState()
    data class Error(val message: String) : LoginState()
    object Success : LoginState()
}