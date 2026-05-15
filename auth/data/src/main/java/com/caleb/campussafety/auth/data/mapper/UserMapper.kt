package com.caleb.campussafety.auth.data.mapper

import com.caleb.campussafety.auth.domain.model.User
import com.caleb.campussafety.auth.domain.model.UserRole

fun Map<String, Any?>.toUser(id: String): User {
    return User(
        id = id,
        email = this["email"] as? String ?: "",
        fullName = this["fullName"] as? String ?: "",
        role = when (this["role"] as? String) {
            "SECURITY" -> UserRole.SECURITY
            else -> UserRole.STUDENT
        },
        matricNumber = this["matricNumber"] as? String,
        badgeNumber = this["badgeNumber"] as? String
    )
}

fun User.toMap(): Map<String, Any?> {
    return mapOf(
        "email" to email,
        "fullName" to fullName,
        "role" to role.name,
        "matricNumber" to matricNumber,
        "badgeNumber" to badgeNumber
    )
}