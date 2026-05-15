package com.caleb.campussafety.auth.presentation.register

import com.caleb.campussafety.auth.domain.model.UserRole

data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: UserRole = UserRole.STUDENT,
    val matricNumber: String = "",
    val badgeNumber: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)