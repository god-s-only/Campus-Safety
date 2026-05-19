package com.caleb.campussafety.auth.presentation.login

import com.caleb.campussafety.auth.domain.model.UserRole

sealed class LoginEvent {
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    data class OnRoleChange(val role: UserRole) : LoginEvent()
    object OnTogglePasswordVisibility : LoginEvent()
    object OnLoginClick : LoginEvent()
}