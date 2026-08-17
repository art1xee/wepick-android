package com.example.wepick.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wepick.screens.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileSetupViewModel() : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _isImageUploading = MutableStateFlow(false)
    val isImageUploading = _isImageUploading.asStateFlow()

    var transitionState by mutableStateOf<AuthTransitionState?>(null)
        private set


    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                loadInitialData()
                fetchUserProfile()
            } else {
                clearProfileData()
            }
        }
    }

    fun loadInitialData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            // the blocking possibility change user email `cause it's always written
            _email.value = user.email ?: ""

            _name.value = user.displayName ?: ""
            _photoUrl.value = user.photoUrl?.toString()

        }
    }

    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateUsername(newUserName: String) {
        _userName.value = newUserName
    }


    fun fetchUserProfile() {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                Log.d("ProfileSetup", "Начинаем загрузку из Firestore для UID: ${currentUser.uid}")
                val document = db.collection("users").document(currentUser.uid).get().await()
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    Log.d("ProfileSetup", "Данные из базы получены: $profile") // Смотрим в Logcat!
                    profile?.let {
                        _photoUrl.value = it.photoUrl
                        _name.value = it.name
                        _userName.value = it.userName.removePrefix("@")
                        _email.value = it.email
                    }
                }else{
                    Log.d("ProfileSetup", "Документ пользователя в Firestore НЕ СУЩЕСТВУЕТ!")
                }
            } catch (e: Exception) {
                Log.e("ProfileSetup", "Error loading profile from DB", e)
            }
        }
    }

    fun saveProfile() {
        val currentUser = auth.currentUser ?: return
        val currentUserName = _userName.value.trim()
        val currentName = _name.value.trim()

        val formattedUserName = if (currentUserName.startsWith("@")) {
            currentUserName
        } else {
            "@$currentUserName"
        }


        if (currentName.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            transitionState =
                AuthTransitionState.Loading("Зберігаємо профіль...") // TODO: Add in R.string
            try {
                val userProfile = UserProfile(
                    uid = currentUser.uid,
                    userName = formattedUserName,
                    name = currentName,
                    email = _email.value,
                    photoUrl = _photoUrl.value,
                    profileCompleted = true,
                )

                db.collection("users").document(currentUser.uid).set(userProfile).await()

                val profileUpdates =
                    UserProfileChangeRequest.Builder().setDisplayName(currentName).apply {
                        if (!_photoUrl.value.isNullOrEmpty()) {
                            photoUri = Uri.parse(_photoUrl.value)
                        }
                    }.build()

                currentUser.updateProfile(profileUpdates).await()

                transitionState =
                    AuthTransitionState.Success("Ласкаво просимо!") // TODO: add R.string
                delay(2000)
                transitionState = null
                _isSaved.value = true
            } catch (e: Exception) {
                transitionState = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reloadData() {
        _isSaved.value = false
        _isLoading.value = false

        val currentUser = auth.currentUser

        currentUser?.let { user ->
            _email.value = user.email ?: ""
            _name.value = user.displayName ?: ""
            _photoUrl.value = user.photoUrl?.toString()
        }
    }

    fun clearProfileData() {
        _name.value = ""
        _userName.value = ""
        _email.value = ""
        _photoUrl.value = null
        _isSaved.value = false
    }


    fun uploadProfileImage(imageUrl: Uri) {
        val currentUser = auth.currentUser ?: return

        viewModelScope.launch {
            _isImageUploading.value = true
            try {
                val storageRef = storage.reference.child("profile_images/${currentUser.uid}.jpg")

                storageRef.putFile(imageUrl).await()

                val downloadUrl = storageRef.downloadUrl.await()

                _photoUrl.value = downloadUrl.toString()
            } catch (e: Exception) {
                Log.e("ProfileSetup", "ERROR: cannot load the photo", e)
            } finally {
                _isImageUploading.value = false
            }
        }
    }
}