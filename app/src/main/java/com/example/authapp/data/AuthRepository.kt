package com.example.authapp.data

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(
        email: String,
        password: String,
        name: String
    ): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

            val uid = result.user?.uid

            if(uid != null) {
                val user = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "created_at" to System.currentTimeMillis()
                )
                firestore.collection("users")
                    .document(uid)
                    .set(user)
                    .await()
            }
            true
        } catch (e: Exception) {
            Log.e("/AuthRepository", "Erro no cadastro ${e.message}")
            false
        }
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): Boolean {
        return try {
            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro login ${e.message}")
            false
        }
    }

    suspend fun resetPassword(
        email: String
    ): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao enviar email de recuperação de senha: ${e.message}")
            false
        }
    }

    suspend fun getUserName(): String? {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()
                snapshot.getString("name")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(
                "AuthRepository",
                "Erro ao resgatar nome do usuário: ${e.message}"
            )
            null
        }
    }

    fun getGoogleSignInClient(context: android.content.Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.example.authapp.R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun loginWithGoogle(idToken: String) : Boolean {
        return try {
            var credential = GoogleAuthProvider.getCredential(idToken, null)
            var result = auth.signInWithCredential(credential).await()
            val user = result.user

            user?.let {
                val uid = it.uid
                val nome = it.displayName ?: "Usuário"
                val email = it.email ?: ""

                val userRef = firestore.collection("users").document(uid)
                val snapshot = userRef.get().await()
                if (!snapshot.exists()) {
                    val userData = hashMapOf(
                        "uid" to uid,
                        "name" to nome,
                        "email" to email,
                        "created_at" to System.currentTimeMillis()
                    )
                    userRef.set(userData).await()
                }
            }
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro no login com Google: ${e.message}")
            false
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLogged(): Boolean {
        return auth.currentUser != null
    }
}