package com.rodrigues.pedroschwarz.projetofirebaseauth.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    fun registerUser(
        email: String,
        password: String,
        onComplete: (result: Resource<Unit>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(Success())
                } else {
                    task.exception?.let { exception ->
                        onComplete(Failure(error = exception.message))
                    }
                }
            }
    }

    fun loginUser(email: String, password: String, onComplete: (result: Resource<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(Success())
                } else {
                    task.exception?.let { exception ->
                        onComplete(Failure(error = exception.message))
                    }
                }
            }
    }

    fun getUser(): Resource<FirebaseUser> {
        return if (auth.currentUser != null) {
            Success(data = auth.currentUser)
        } else {
            Failure(error = "No user found.")
        }
    }

    fun checkUser(context: Context): Resource<Unit> {
        return if (auth.currentUser != null || GoogleSignIn.getLastSignedInAccount(context) != null) {
            Success()
        } else {
            Failure()
        }
    }

    fun signOutGoogleAccount(
        googleClient: GoogleSignInClient,
        onComplete: (result: Resource<Unit>) -> Unit
    ) {
        googleClient.signOut()
            .addOnCompleteListener { task ->
                onComplete(Success())
            }
    }

    fun signOutUser() {
        auth.signOut()
    }

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}