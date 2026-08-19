package com.example.wepick.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wepick.R
import com.example.wepick.util.UiText // Обязательный импорт твоего нового класса!
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
        transitionState = AuthTransitionState.Success(UiText.StringResource(R.string.auth_transition_welcome))
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
        Log.d("AuthDebug", "Starting check profile for UID: $uid")

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                Log.d(
                    "AuthDebug",
                    "Success respond of Firestore! Document is exist: ${document.exists()}"
                )
                if (document.exists() && document.getBoolean("profileCompleted") == true) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.NeedsProfileSetup
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthDebug", "CRITICAL ERROR OF FIRESTORE", e)
                val errorMsg = e.localizedMessage?.let {
                    UiText.DynamicString(it)
                } ?: UiText.DynamicString("Firestore error")

                _authState.value = AuthState.Error(errorMsg)
            }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            transitionState = AuthTransitionState.Loading(UiText.StringResource(R.string.auth_transition_logining))
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                delay(2000)
                transitionState = null
                verifyUserProfile(auth.currentUser!!.uid)
            } catch (e: Exception) {
                transitionState = null
                val errorMessage = e.localizedMessage?.let {
                    UiText.DynamicString(it)
                } ?: UiText.StringResource(R.string.auth_transition_logining_error)

                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun signup(email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _authState.value = AuthState.Error(UiText.StringResource(R.string.auth_transition_enter_fields_error))
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error(UiText.StringResource(R.string.auth_transition_password_mismatch))
            return
        }

        viewModelScope.launch {
            transitionState = AuthTransitionState.Loading(UiText.StringResource(R.string.auth_transition_creating_account))
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                delay(2500)
                transitionState = null
                verifyUserProfile(auth.currentUser!!.uid)
            } catch (e: Exception) {
                transitionState = null
                val errorMessage = e.localizedMessage?.let {
                    UiText.DynamicString(it)
                } ?: UiText.StringResource(R.string.auth_transition_registration_error)

                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            // 1. Сначала удаляем документ юзера
            db.collection("users").document(uid).delete()
                .addOnSuccessListener {
                    // 2. Затем удаляем сам аккаунт из аутентификации
                    user.delete()
                        .addOnSuccessListener {
                            _authState.value = AuthState.Unauthenticated
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onError(e)
                        }
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (UiText) -> Unit) {
        if (email.isEmpty()) {
            onError(UiText.StringResource(R.string.auth_transition_enter_password))
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    val errorMsg = task.exception?.message?.let {
                        UiText.DynamicString(it)
                    } ?: UiText.StringResource(R.string.auth_transition_reset_password_error)

                    onError(errorMsg)
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
            transitionState = AuthTransitionState.Loading(UiText.StringResource(R.string.auth_transition_logining_with_google))
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
                val errorMessage = e.message?.let {
                    UiText.DynamicString(it)
                } ?: UiText.StringResource(R.string.auth_transition_logining_with_google_error)

                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object NeedsProfileSetup : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: UiText) : AuthState()
}