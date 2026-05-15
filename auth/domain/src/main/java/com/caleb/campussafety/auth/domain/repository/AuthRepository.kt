package com.caleb.campussafety.auth.domain.repository

import com.caleb.campussafety.auth.domain.model.AuthResult
import com.caleb.campussafety.auth.domain.model.User
import com.caleb.campussafety.auth.domain.model.UserRole

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<User>
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        role: UserRole,
        matricNumber: String? = null,
        badgeNumber: String? = null
    ): AuthResult<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
}