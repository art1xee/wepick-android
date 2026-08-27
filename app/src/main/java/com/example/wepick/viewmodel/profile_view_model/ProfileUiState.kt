package com.example.wepick.viewmodel.profile_view_model

data class ProfileUiState(
    val name: String = "",
    val userName: String = "",
    val email: String = "",
    val bio: String = "",
    val birthday: String = "",
    val photoUrl: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isImageUploading: Boolean = false,
    val userNameStatus: ProfileSetupViewModel.ValidationStatus = ProfileSetupViewModel.ValidationStatus.IDLE,
    val emailStatus: ProfileSetupViewModel.ValidationStatus = ProfileSetupViewModel.ValidationStatus.IDLE
)
