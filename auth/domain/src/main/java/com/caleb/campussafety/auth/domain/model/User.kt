package com.caleb.campussafety.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val matricNumber: String? = null,
    val badgeNumber: String? = null
)