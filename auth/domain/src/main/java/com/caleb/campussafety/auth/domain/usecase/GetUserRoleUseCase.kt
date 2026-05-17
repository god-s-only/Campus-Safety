package com.caleb.campussafety.auth.domain.usecase

import com.caleb.campussafety.auth.domain.model.UserRole
import com.caleb.campussafety.auth.domain.repository.AuthRepository

class GetUserRoleUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): UserRole? {
        return repository.getCurrentUser()?.role
    }
}