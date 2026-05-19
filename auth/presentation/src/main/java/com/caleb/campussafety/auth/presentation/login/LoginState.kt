package com.caleb.campussafety.auth.presentation.login

import com.caleb.campussafety.auth.domain.model.UserRole

data class LoginState(
    val email: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.STUDENT,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordVisible: Boolean = false
)