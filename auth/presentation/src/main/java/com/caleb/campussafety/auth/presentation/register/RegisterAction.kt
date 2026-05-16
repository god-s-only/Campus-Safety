package com.caleb.campussafety.auth.presentation.register

sealed class RegisterAction {
    data class NavigateToHome(val isSecurityOfficer: Boolean) : RegisterAction()
    object NavigateToLogin : RegisterAction()
    data class ShowError(val message: String) : RegisterAction()
}