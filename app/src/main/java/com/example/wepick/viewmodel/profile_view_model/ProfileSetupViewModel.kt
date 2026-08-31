package com.example.wepick.viewmodel.profile_view_model

import android.net.Uri
import android.util.Log.e
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wepick.R
import com.example.wepick.data.repository.FirebaseUserRepository
import com.example.wepick.domain.repository.UserRepository
import com.example.wepick.screens.auth.profile_setup.UserProfile
import com.example.wepick.util.UiText
import com.example.wepick.viewmodel.AuthTransitionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ProfileSetupViewModel(
    private val userRepository: UserRepository = FirebaseUserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var usernameJob: Job? = null
    private var emailJob: Job? = null

    var transitionState by mutableStateOf<AuthTransitionState?>(null)
        private set

    val isGoogleSignup: Boolean
        get() = userRepository.isGoogleAuth


    init {
        viewModelScope.launch {
            userRepository.authStateFlow.collect { isAuthenticated ->
                if (isAuthenticated) {
                    fetchUserProfile()
                } else {
                    clearProfileData()
                }

            }
        }
    }

    enum class ProfileField {
        NAME,
        USERNAME,
        EMAIL,
        USER_BIO,
        BIRTHDAY
    }

    enum class ValidationStatus {
        IDLE,
        LOADING,
        AVAILABLE,
        TAKEN,
    }

    fun saveMultipleFields(
        updates: Map<ProfileField, String>,
        onSuccess: () -> Unit,
        onError: (UiText) -> Unit
    ) {

        val firestoreUpdates = mutableMapOf<String, Any>()

        for ((field, value) in updates) {
            val cleanValue = value.trim()

            if ((field == ProfileField.NAME || field == ProfileField.USERNAME) && cleanValue.isEmpty()) {
                onError(UiText.DynamicString("Поля не могут быть пустыми"))
                return
            }

            when (field) {
                ProfileField.NAME -> firestoreUpdates["name"] = cleanValue
                ProfileField.USERNAME -> firestoreUpdates["userName"] = cleanValue.removePrefix("@")
                ProfileField.USER_BIO -> firestoreUpdates["bio"] = cleanValue
                ProfileField.EMAIL -> firestoreUpdates["email"] = cleanValue
                ProfileField.BIRTHDAY -> firestoreUpdates["birthday"] = cleanValue

            }
        }

        if (firestoreUpdates.isEmpty()) {
            onSuccess()
            return
        }

        viewModelScope.launch {
            userRepository.updateProfileFields(
                firestoreUpdates
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        name = (firestoreUpdates["name"] as? String) ?: it.name,
                        userName = (firestoreUpdates["userName"] as? String) ?: it.userName,
                        bio = (firestoreUpdates["bio"] as? String) ?: it.bio,
                        email = (firestoreUpdates["email"] as? String) ?: it.email,
                        birthday = (firestoreUpdates["birthday"] as? String) ?: it.birthday,
                    )
                }
                onSuccess()
            }.onFailure { it ->
                val errorMsg = it.localizedMessage?.let {
                    UiText.DynamicString(it)
                } ?: UiText.DynamicString("Save Error")
                onError(errorMsg)
            }

        }
    }

    fun updateName(newName: String) {
        _uiState.update {
            it.copy(name = newName)
        }
    }

    fun updateUsername(newUserName: String) {
        _uiState.update {
            it.copy(userName = newUserName)
        }
    }

    fun checkUsernameAvailability(userNameToCheck: String) {
        val cleanUserName = userNameToCheck.trim().removePrefix("@")

        usernameJob?.cancel()
        usernameJob = viewModelScope.launch {
            delay(500.milliseconds)
            if (cleanUserName.isEmpty()) {
                _uiState.update {
                    it.copy(
                        userNameStatus = ValidationStatus.IDLE
                    )
                }
                return@launch
            }
            try {
                _uiState.update {
                    it.copy(
                        userNameStatus = ValidationStatus.LOADING,
                    )
                }
                userRepository.isUsernameAvailable(
                    cleanUserName
                ).onSuccess { isAvailable ->
                    _uiState.update {
                        it.copy(
                            userNameStatus = if (isAvailable) ValidationStatus.AVAILABLE else ValidationStatus.TAKEN
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            userNameStatus = ValidationStatus.IDLE
                        )
                    }

                }

            } catch (e: Exception) {
                e("ProfileSetupAvailability", "Error loading username from DB", e)
                _uiState.update { it.copy(userNameStatus = ValidationStatus.IDLE) }
            }
        }
    }

    fun checkEmailAvailability(emailToCheck: String) {
        val cleanEmail = emailToCheck.trim()

        emailJob?.cancel()
        emailJob = viewModelScope.launch {
            delay(500.milliseconds)

            if (cleanEmail.isEmpty()) {
                _uiState.update {
                    it.copy(
                        emailStatus = ValidationStatus.IDLE
                    )
                }
                return@launch
            }
            try {
                _uiState.update {
                    it.copy(
                        emailStatus = ValidationStatus.LOADING
                    )

                }

                userRepository.isEmailAvailable(cleanEmail)
                    .onSuccess { isAvailable ->
                        _uiState.update {
                            it.copy(
                                emailStatus = if (isAvailable) ValidationStatus.AVAILABLE else ValidationStatus.TAKEN
                            )
                        }
                    }
                    .onFailure {
                        _uiState.update {
                            it.copy(
                                emailStatus = ValidationStatus.IDLE
                            )
                        }
                    }

            } catch (e: Exception) {
                e("ProfileSetupAvailability", "Error loading email from DB", e)
                _uiState.update { it.copy(emailStatus = ValidationStatus.IDLE) }
            }

        }
    }


    fun resetUserNameValidation() {
        usernameJob?.cancel()
        _uiState.update {
            it.copy(
                userNameStatus = ValidationStatus.IDLE,
            )
        }
    }

    fun resetEmailValidation() {
        emailJob?.cancel()
        _uiState.update {
            it.copy(
                emailStatus = ValidationStatus.IDLE,
            )
        }
    }


    fun fetchUserProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile()
                .onSuccess { profile ->
                    if (profile != null) {
                        _uiState.update {
                            it.copy(
                                name = profile.name,
                                userName = profile.userName,
                                email = profile.email,
                                bio = profile.bio,
                                birthday = profile.birthday,
                                photoUrl = profile.photoUrl,
                            )
                        }
                    }
                }.onFailure { e ->
                    e("ProfileSetup", "Error loading profile", e)
                }
        }
    }

    fun saveProfile() {
        val currentUiState = _uiState.value
        val currentUid = userRepository.currentUserId ?: return

        val cleanName = currentUiState.name.trim()
        val cleanUserName = currentUiState.userName.trim().removePrefix("@")

        if (cleanName.isEmpty() || cleanUserName.isEmpty()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            transitionState =
                AuthTransitionState.Loading(UiText.StringResource(R.string.profile_view_model_saving_profile))

            val userProfile = UserProfile(
                uid = currentUid,
                userName = cleanUserName,
                name = cleanName,
                email = currentUiState.email,
                photoUrl = currentUiState.photoUrl,
                bio = currentUiState.bio,
                profileCompleted = true,
                birthday = currentUiState.birthday
            )

            userRepository.saveUserProfile(userProfile)
                .onSuccess {
                    transitionState =
                        AuthTransitionState.Success(UiText.StringResource(R.string.auth_transition_welcome))
                    delay(2000.milliseconds)
                    transitionState = null
                    _uiState.update {
                        it.copy(
                            isSaved = true,
                            isLoading = false
                        )
                    }
                }.onFailure {
                    transitionState = null
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }


    fun clearProfileData() {
        _uiState.update {
            ProfileUiState()
        }
    }

    fun uploadProfileImage(imageUrl: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isImageUploading = true
                )
            }
            userRepository.uploadAvatar(imageUrl)
                .onSuccess { downloadUrl ->
                    _uiState.update {
                        it.copy(
                            photoUrl = downloadUrl,
                            isImageUploading = false
                        )
                    }
                }.onFailure { e ->
                    e("ProfileSetup", "Error: cannot load the photo", e)
                    _uiState.update { it.copy(isImageUploading = false) }
                }

        }

    }
}