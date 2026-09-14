package com.example.wepick.viewmodel.profile_view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wepick.data.repository.FirebaseUserRepository
import com.example.wepick.domain.repository.UserRepository
import com.example.wepick.screens.auth.profile_setup.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileSettingUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isSignedOut: Boolean = false,
    val isDeletedAccount: Boolean = false,
    val error: String? = null,
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
                    isLoading = true
                )
            }
            userRepository.getUserProfile()
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            userProfile = profile, isLoading = false
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage, isLoading = false
                        )
                    }
                }
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.signOut()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSignedOut = true
                        )
                    }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage, isLoading = false
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
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage, isLoading = false
                        )
                    }
                }
        }
    }

    fun clearProfileData() {
        _uiState.update {
            ProfileSettingUiState()
        }
    }
}

