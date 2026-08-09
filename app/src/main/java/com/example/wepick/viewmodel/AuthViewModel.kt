package com.example.wepick.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    var transitionState by mutableStateOf<AuthTransitionState?>(null)
        private set

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            verifyUserProfile(currentUser.uid)
        }
    }


    fun verifyUserProfile(uid: String) {
        Log.d("AuthDebug", "Начинаем проверку профиля для UID: $uid")

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                Log.d(
                    "AuthDebug",
                    "Успешный ответ от Firestore! Документ существует: ${document.exists()}"
                )
                if (document.exists() && document.getBoolean("profileCompleted") == true) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.NeedsProfileSetup
                }
            }
            .addOnFailureListener { e ->
                // ВОТ ЭТА СТРОКА РАСПЕЧАТАЕТ НАСТОЯЩУЮ ПРИЧИНУ:
                Log.e("AuthDebug", "КРИТИЧЕСКАЯ ОШИБКА FIRESTORE", e)

                _authState.value = AuthState.Error(e.localizedMessage ?: "Firestore error")
            }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value =
                AuthState.Error("Email or password can`t be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    verifyUserProfile(task.result.user!!.uid)
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _authState.value =
                AuthState.Error("Email or password can`t be empty")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }


        viewModelScope.launch {
            transitionState = AuthTransitionState.Loading("Creating Account...")
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        transitionState = AuthTransitionState.Success("Welcome!")
                    }
                    verifyUserProfile(task.result.user!!.uid)
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isEmpty()) {
            onError("Please, firstly enter a password.")
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Password reset ERROR")
                }
            }
    }

    fun loginWithGoogle(context: Context) {

        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("871652837283-djgdmj57j7mg1kkqdun3tktb2d8aas0j.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                handleGoogleCredential(result.credential)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google Auth Failed")
            }
        }
    }

    private fun handleGoogleCredential(credential: Credential) {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val authCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)


            auth.signInWithCredential(authCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        verifyUserProfile(task.result.user!!.uid)
                    } else {
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Firebase Auth Failed")
                    }
                }
        }
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object NeedsProfileSetup : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}