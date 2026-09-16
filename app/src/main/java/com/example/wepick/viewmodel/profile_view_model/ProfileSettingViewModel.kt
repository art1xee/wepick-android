package com.example.wepick.viewmodel.profile_view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wepick.data.repository.FirebaseUserRepository
import com.example.wepick.domain.repository.UserRepository
import com.example.wepick.screens.auth.profile_setup.UserProfile
import com.example.wepick.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileSettingUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val isSignedOut: Boolean = false,
    val isDeletedAccount: Boolean = false,
    val isGoogleAuth: Boolean = false,
    val isPasswordResetSent: Boolean = false,
)

class ProfileSettingViewModel(
    private val userRepository: UserRepository = FirebaseUserRepository()

) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileSettingUiState())
    val uiState: StateFlow<ProfileSettingUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isDeletedAccount = false,
                    error = null
                )
            }
            userRepository.getUserProfile()
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userProfile = profile,
                            isGoogleAuth = userRepository.isGoogleAuth
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage?.let { msg -> UiText.DynamicString(msg) }
                                ?: UiText.DynamicString("Deleting account error")
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.signOut()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSignedOut = true
                        )
                    }
                }
                .onFailure { e ->
                    Log.e("ProfileSetup", "Error signing out", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.localizedMessage?.let { msg -> UiText.DynamicString(msg) }
                                ?: UiText.DynamicString("Sign out error")
                        )
                    }
                }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            userRepository.deleteAccount()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeletedAccount = true,
                            isLoading = false
                        )
                    }
                }.onFailure { e ->
                    Log.e("DELETE_ACC_ERROR", "Deleting account error: ${e.message}", e)
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage?.let { msg -> UiText.DynamicString(msg) }
                                ?: UiText.DynamicString("Sign out error"), isLoading = false
                        )
                    }
                }
        }
    }

    fun onPrivacyChanged(value: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            userRepository.setPrivacy(value)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            userProfile = it.userProfile?.copy(isPrivate = value),
                            isLoading = false
                        )
                    }
                }.onFailure { e ->
                    Log.e("SET_PRIVACY_ERROR", "Setting privacy error: ${e.message}", e)
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage?.let { msg -> UiText.DynamicString(msg) }
                                ?: UiText.DynamicString("Set Privacy status error"),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onPushEnabledChanged(value: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            userRepository.setPushEnabled(value)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            userProfile = it.userProfile?.copy(pushEnabled = value),
                            isLoading = false
                        )
                    }
                }.onFailure { e ->
                    Log.e("SET_PUSH-MSG_ERROR", "Set push messages error: ${e.message}", e)
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage?.let { msg -> UiText.DynamicString(msg) }
                                ?: UiText.DynamicString("Set Push-messages error"),
                            isLoading = false
                        )
                    }
                }
        }

    }

    fun onEmailEnabledChanged(value: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            userRepository.setEmailEnabled(value)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            userProfile = it.userProfile?.copy(emailEnabled = value),
                            isLoading = false
                        )
                    }
                }.onFailure { e ->
                    Log.e("SET_EMAIL-MSG_ERROR", "Set email msg error: ${e.message}", e)
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage?.let { msg -> UiText.DynamicString(msg) }
                                ?: UiText.DynamicString("Set email msg error"),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun sendPasswordResetEmail() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }
            userRepository.sendPasswordResetEmail()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPasswordResetSent = true
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage?.let { msg -> UiText.DynamicString(msg) }
                                ?: UiText.DynamicString("Something went wrong with sending reset password email"),
                            isLoading = false
                        )
                    }
                }
        }
    }

}

