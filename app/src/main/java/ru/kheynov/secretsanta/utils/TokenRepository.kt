package ru.kheynov.secretsanta.utils

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {
    var token: String? = null
        private set

    suspend fun fetchToken() {
        token = firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
    }
}