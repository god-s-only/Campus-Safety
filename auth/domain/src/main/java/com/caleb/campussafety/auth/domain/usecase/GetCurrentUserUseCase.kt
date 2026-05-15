package com.caleb.campussafety.auth.domain.usecase

import com.caleb.campussafety.auth.domain.model.User
import com.caleb.campussafety.auth.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): User? = repository.getCurrentUser()
}