package com.caleb.campussafety.auth.data.repository

import com.caleb.campussafety.auth.data.mapper.toMap
import com.caleb.campussafety.auth.data.mapper.toUser
import com.caleb.campussafety.auth.domain.model.AuthResult
import com.caleb.campussafety.auth.domain.model.User
import com.caleb.campussafety.auth.domain.model.UserRole
import com.caleb.campussafety.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): AuthResult<User> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()
            val uid = result.user?.uid
                ?: return AuthResult.Error("Login failed: no user returned")
            val doc = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()
            val user = doc.data?.toUser(uid)
                ?: return AuthResult.Error("User profile not found")
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        role: UserRole,
        matricNumber: String?,
        badgeNumber: String?
    ): AuthResult<User> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
            val uid = result.user?.uid
                ?: return AuthResult.Error("Registration failed: no user returned")
            val user = User(
                id = uid,
                email = email,
                fullName = fullName,
                role = role,
                matricNumber = matricNumber,
                badgeNumber = badgeNumber
            )
            firestore
                .collection("users")
                .document(uid)
                .set(user.toMap())
                .await()
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: return null
            val doc = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()
            doc.data?.toUser(uid)
        } catch (e: Exception) {
            null
        }
    }
}