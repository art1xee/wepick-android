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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    var transitionState by mutableStateOf<AuthTransitionState?>(null)
        private set

    private suspend fun handeAuthSuccess(uid: String) {
        transitionState = AuthTransitionState.Success("Welcome")
        delay(2000)
        transitionState = null
        verifyUserProfile(uid)
    }

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
        viewModelScope.launch {
            transitionState = AuthTransitionState.Loading("Входимо...") // TODO: add this text in R.string
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                delay(2000)
                transitionState = null
                verifyUserProfile(auth.currentUser!!.uid)
            } catch (e: Exception) {
                transitionState = null
                _authState.value = AuthState.Error(e.localizedMessage ?: "Помилка входу") // TODO: add this text in R.string
            }
        }
    }

    fun signup(email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _authState.value = AuthState.Error("Заповніть всі поля") // TODO: add this text in R.string
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Паролі не збігаются") // TODO: add this text in R.string
            return
        }

        viewModelScope.launch {
            transitionState = AuthTransitionState.Loading("Створюємо акаунт...") // TODO: add this text in R.string
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                delay(2500)
                transitionState = null
                verifyUserProfile(auth.currentUser!!.uid)
            } catch (e: Exception) {
                transitionState = null
                _authState.value = AuthState.Error(e.localizedMessage ?: "Помилка реєстрації") // TODO: add this text in R.string
            }
        }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (Exception) -> Unit){
        val user = auth.currentUser
        user?.delete()?.addOnSuccessListener {
            onSuccess()
        }?.addOnFailureListener {e ->
            onError(e)
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isEmpty()) {
            onError("Please, firstly enter a password.") // TODO: add this text in R.string
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Password reset ERROR") // TODO: add this text in R.string
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
            transitionState = AuthTransitionState.Loading("Входимо через Google...") // TODO: add this text in R.string
            try {
                val result = credentialManager.getCredential(context, request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                
                auth.signInWithCredential(authCredential).await()
                delay(2000)
                transitionState = null
                verifyUserProfile(auth.currentUser!!.uid)
            } catch (e: Exception) {
                transitionState = null
                _authState.value = AuthState.Error(e.message ?: "Google Auth Failed") // TODO: add this text in R.string
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