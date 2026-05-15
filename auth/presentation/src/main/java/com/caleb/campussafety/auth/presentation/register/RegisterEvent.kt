package com.caleb.campussafety.auth.presentation.register

import com.caleb.campussafety.auth.domain.model.UserRole

sealed class RegisterEvent {
    data class OnFullNameChange(val fullName: String) : RegisterEvent()
    data class OnEmailChange(val email: String) : RegisterEvent()
    data class OnPasswordChange(val password: String) : RegisterEvent()
    data class OnConfirmPasswordChange(val confirmPassword: String) : RegisterEvent()
    data class OnRoleChange(val role: UserRole) : RegisterEvent()
    data class OnMatricNumberChange(val matricNumber: String) : RegisterEvent()
    data class OnBadgeNumberChange(val badgeNumber: String) : RegisterEvent()
    object OnTogglePasswordVisibility : RegisterEvent()
    object OnToggleConfirmPasswordVisibility : RegisterEvent()
    object OnRegisterClick : RegisterEvent()
}