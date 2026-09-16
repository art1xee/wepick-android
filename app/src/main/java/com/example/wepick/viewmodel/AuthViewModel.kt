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
import com.example.wepick.data.repository.FirebaseUserRepository
import com.example.wepick.domain.repository.UserRepository
import com.example.wepick.util.UiText
import androidx.annotation.StringRes
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel (
    private val userRepository: UserRepository = FirebaseUserRepository()
): ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    var transitionState by mutableStateOf<AuthTransitionState?>(null)
        private set

    private suspend fun handeAuthSuccess(uid: String) {
        transitionState =
            AuthTransitionState.Success(UiText.StringResource(R.string.auth_transition_welcome))
        delay(2000)
        transitionState = null
        verifyUserProfile(uid)
    }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                _authState.value = AuthState.Unauthenticated
            } else {
                verifyUserProfile(user.uid)
            }
        }
    }

    fun verifyUserProfile(uid: String) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            viewModelScope.launch {
                userRepository.updateFcmToken(token)
            }
        }

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

    // Maps Firebase Auth error codes onto localized strings instead of showing
    // Firebase's own (always-English) exception messages to the user.
    private fun mapAuthError(e: Exception, @StringRes fallback: Int): UiText {
        val errorCode = (e as? FirebaseAuthException)?.errorCode
        val resId = when (errorCode) {
            "ERROR_INVALID_EMAIL", "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND", "ERROR_INVALID_CREDENTIAL" ->
                R.string.login_error_invalid_credentials
            "ERROR_USER_DISABLED" -> R.string.auth_error_user_disabled
            "ERROR_EMAIL_ALREADY_IN_USE" -> R.string.auth_error_email_already_in_use
            "ERROR_WEAK_PASSWORD" -> R.string.auth_error_weak_password
            "ERROR_TOO_MANY_REQUESTS" -> R.string.auth_error_too_many_requests
            "ERROR_NETWORK_REQUEST_FAILED" -> R.string.auth_error_network
            else -> fallback
        }
        return UiText.StringResource(resId)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            transitionState =
                AuthTransitionState.Loading(UiText.StringResource(R.string.auth_transition_logining))
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                delay(2000)
                transitionState = null
            } catch (e: Exception) {
                transitionState = null
                val errorMessage = mapAuthError(e, R.string.auth_transition_logining_error)

                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun signup(email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _authState.value =
                AuthState.Error(UiText.StringResource(R.string.auth_transition_enter_fields_error))
            return
        }
        if (password != confirmPassword) {
            _authState.value =
                AuthState.Error(UiText.StringResource(R.string.auth_transition_password_mismatch))
            return
        }

        viewModelScope.launch {
            transitionState =
                AuthTransitionState.Loading(UiText.StringResource(R.string.auth_transition_creating_account))
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                delay(2500)
                transitionState = null
            } catch (e: Exception) {
                transitionState = null
                val errorMessage = mapAuthError(e, R.string.auth_transition_registration_error)

                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun signout() {
        auth.signOut()
    }

    // authState is Activity-scoped and outlives a single screen, so an unconsumed
    // Error would still be sitting there and re-fire on the next screen that
    // observes it (e.g. navigating from Login to Signup after a failed login).
    fun consumeError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
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
                    val errorMsg = task.exception?.let {
                        mapAuthError(it, R.string.auth_transition_reset_password_error)
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
            transitionState =
                AuthTransitionState.Loading(UiText.StringResource(R.string.auth_transition_logining_with_google))
            try {
                val result = credentialManager.getCredential(context, request)
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)
                val authCredential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                auth.signInWithCredential(authCredential).await()
                delay(2000)
                transitionState = null
            } catch (e: Exception) {
                transitionState = null
                val errorMessage = mapAuthError(e, R.string.auth_transition_logining_with_google_error)

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