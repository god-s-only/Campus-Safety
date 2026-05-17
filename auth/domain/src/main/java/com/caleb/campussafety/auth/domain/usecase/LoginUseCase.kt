package com.caleb.campussafety.auth.domain.usecase

import com.caleb.campussafety.auth.domain.model.AuthResult
import com.caleb.campussafety.auth.domain.model.User
import com.caleb.campussafety.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): AuthResult<User> {
        if (email.isBlank()) return AuthResult.Error("Email cannot be empty")
        if (password.isBlank()) return AuthResult.Error("Password cannot be empty")
        if (password.length < 6) return AuthResult.Error("Password must be at least 6 characters")
        return repository.login(email, password)
    }
}