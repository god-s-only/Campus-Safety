package com.caleb.campussafety.auth.presentation.login

sealed class LoginEvent {
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    object OnTogglePasswordVisibility : LoginEvent()
    object OnLoginClick : LoginEvent()
}