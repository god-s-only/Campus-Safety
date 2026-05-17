package com.caleb.campussafety.auth.domain.usecase

import com.caleb.campussafety.auth.domain.model.AuthResult
import com.caleb.campussafety.auth.domain.model.User
import com.caleb.campussafety.auth.domain.model.UserRole
import com.caleb.campussafety.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        fullName: String,
        role: UserRole,
        matricNumber: String? = null,
        badgeNumber: String? = null
    ): AuthResult<User> {
        if (email.isBlank()) return AuthResult.Error("Email cannot be empty")
        if (password.length < 6) return AuthResult.Error("Password must be at least 6 characters")
        if (fullName.isBlank()) return AuthResult.Error("Full name cannot be empty")
        if (role == UserRole.STUDENT && matricNumber.isNullOrBlank())
            return AuthResult.Error("Matric number is required for students")
        if (role == UserRole.SECURITY && badgeNumber.isNullOrBlank())
            return AuthResult.Error("Badge number is required for security personnel")
        return repository.register(email, password, fullName, role, matricNumber, badgeNumber)
    }
}