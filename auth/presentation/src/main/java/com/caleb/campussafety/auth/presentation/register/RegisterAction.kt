package com.caleb.campussafety.auth.presentation.register

sealed class RegisterAction {
    object NavigateToHome : RegisterAction()
    object NavigateToLogin : RegisterAction()
    data class ShowError(val message: String) : RegisterAction()
}