package com.example.wepick.viewmodel.profile_view_model

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wepick.R
import com.example.wepick.screens.auth.profile_setup.UserProfile
import com.example.wepick.util.UiText
import com.example.wepick.viewmodel.AuthTransitionState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.collections.iterator
import androidx.core.net.toUri
import kotlinx.coroutines.Job
import kotlin.time.Duration.Companion.milliseconds

class ProfileSetupViewModel() : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()


    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var usernameJob: Job? = null
    private var emailJob: Job? = null


    var transitionState by mutableStateOf<AuthTransitionState?>(null)
        private set

    val isGoogleSignup: Boolean
        get() = auth.currentUser?.providerData?.any { it.providerId == "google.com" } == true

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                fetchUserProfile()
            } else {
                clearProfileData()
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
        val currentUser = auth.currentUser ?: return

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
            try {
                db.collection("users").document(currentUser.uid)
                    .update(firestoreUpdates)
                    .await()

                _uiState.update {
                    it.copy(
                        // 1. find any value if firestoreUpdates
                        // 2. if program find "new value" - write "new value"
                        // 3. if key missed, working ?: it.value and in the field stayed value which already was in text-field
                        name = (firestoreUpdates["name"] as? String) ?: it.name,
                        userName = (firestoreUpdates["userName"] as? String) ?: it.userName,
                        bio = (firestoreUpdates["bio"] as? String) ?: it.bio,
                        email = (firestoreUpdates["email"] as? String) ?: it.email,
                        birthday = (firestoreUpdates["birthday"] as? String) ?: it.birthday,
                    )
                }

                onSuccess()
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage?.let {
                    UiText.DynamicString(it)
                } ?: UiText.DynamicString("Save error")
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
                val snapshot =
                    db.collection("users").whereEqualTo("userName", cleanUserName).get().await()
                val currentUid = auth.currentUser?.uid
                val isTaken = snapshot.documents.any { doc -> doc.id != currentUid }

                if (!isTaken) {
                    _uiState.update {
                        it.copy(
                            userNameStatus = ValidationStatus.AVAILABLE
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            userNameStatus = ValidationStatus.TAKEN
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("ProfileSetupAvailability", "Error loading username from DB", e)
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

                val snapshot =
                    db.collection("users").whereEqualTo("email", cleanEmail).get().await()
                val currentUid = auth.currentUser?.uid
                val isTaken = snapshot.documents.any { doc -> doc.id != currentUid }

                if (!isTaken) {
                    _uiState.update {
                        it.copy(
                            emailStatus = ValidationStatus.AVAILABLE
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            emailStatus = ValidationStatus.TAKEN
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("ProfileSetupAvailability", "Error loading email from DB", e)
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
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                Log.d("ProfileSetup", "Starting loading from Firestore for UID: ${currentUser.uid}")
                val document = db.collection("users").document(currentUser.uid).get().await()

                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    Log.d("ProfileSetup", "Date from db is get: $profile")
                    profile?.let { userFromDB ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                name = userFromDB.name,
                                userName = userFromDB.userName,
                                email = userFromDB.email,
                                bio = userFromDB.bio,
                                photoUrl = userFromDB.photoUrl,
                                birthday = userFromDB.birthday
                            )
                        }
                    }

                } else {
                    Log.d("ProfileSetup", "Document of the user in Firestore doesn't exist!")
                    _uiState.update {
                        it.copy(
                            email = currentUser.email ?: "",
                            name = if (isGoogleSignup) currentUser.displayName ?: "" else "",
                            photoUrl = it.photoUrl ?: currentUser.photoUrl?.toString(),
                            userName = "",
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileSetup", "Error loading profile from DB", e)
            }
        }
    }

    fun saveProfile() {
        val currentUiState = _uiState.value
        val currentUser = auth.currentUser ?: return

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
            try {
                val userProfile = UserProfile(
                    uid = currentUser.uid,
                    userName = cleanUserName,
                    name = cleanName,
                    email = currentUiState.email,
                    photoUrl = currentUiState.photoUrl,
                    bio = currentUiState.bio,
                    profileCompleted = true,
                    birthday = currentUiState.birthday
                )

                db.collection("users").document(currentUser.uid).set(userProfile).await()

                val profileUpdates =
                    UserProfileChangeRequest.Builder().setDisplayName(cleanName).apply {
                        if (!currentUiState.photoUrl.isNullOrEmpty()) {
                            photoUri = currentUiState.photoUrl.toUri()
                        }
                    }.build()

                currentUser.updateProfile(profileUpdates).await()

                transitionState =
                    AuthTransitionState.Success(UiText.StringResource(R.string.auth_transition_welcome))
                delay(2000.milliseconds)
                transitionState = null
                _uiState.update {
                    it.copy(
                        isSaved = true
                    )
                }
            } catch (e: Exception) {
                transitionState = null
                e.printStackTrace()
            } finally {
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
        val currentUser = auth.currentUser ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isImageUploading = true
                )
            }
            try {
                val storageRef = storage.reference.child("profile_images/${currentUser.uid}.jpg")
                storageRef.putFile(imageUrl).await()

                val downloadUrl = storageRef.downloadUrl.await().toString()

                db.collection("users").document(currentUser.uid).update("photoUrl", downloadUrl)
                    .await()

                _uiState.update {
                    it.copy(
                        photoUrl = downloadUrl
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileSetup", "ERROR: cannot load the photo", e)
            } finally {
                _uiState.update {
                    it.copy(
                        isImageUploading = false
                    )
                }

            }
        }
    }
}