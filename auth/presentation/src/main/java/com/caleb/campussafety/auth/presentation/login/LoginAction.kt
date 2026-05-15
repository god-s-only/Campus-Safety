package com.caleb.campussafety.auth.presentation.login

sealed class LoginAction {
    object NavigateToHome : LoginAction()
    object NavigateToRegister : LoginAction()
    data class ShowError(val message: String) : LoginAction()
}