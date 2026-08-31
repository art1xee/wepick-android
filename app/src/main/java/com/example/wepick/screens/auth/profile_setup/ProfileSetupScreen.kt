package com.example.wepick.screens.auth.profile_setup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.screens.auth.components.FormTextFields
import com.example.wepick.screens.auth.components.LockedEmailField
import com.example.wepick.screens.auth.components.ProfileAvatar
import com.example.wepick.screens.profile_screens.components.ValidationTrailingIcon
import com.example.wepick.ui.components.SetupProfileButton
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.profile_view_model.ProfileSetupViewModel

@Composable
fun ProfileSetup(
    navController: NavController,
    profileViewModel: ProfileSetupViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                profileViewModel.uploadProfileImage(uri)
            }
        }
    )

    var nameError by remember { mutableStateOf(false) }
    val isUserNameTaken = uiState.userNameStatus == ProfileSetupViewModel.ValidationStatus.TAKEN

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            profileViewModel.clearProfileData()
            navController.navigate(ScreenNav.Home.route) {
                popUpTo(ScreenNav.ProfileSetup.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardYellow),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.profile_setup_title),
                    color = White,
                    fontFamily = PressStart2P,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFC58A1E),
                            offset = Offset(8f, 8f),
                            blurRadius = 0f
                        )
                    )
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.profile_setup_subtitle),
                    color = Black,
                    fontFamily = Nunito,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                ProfileAvatar(
                    photoUrl = uiState.photoUrl,
                    isImageUploading = uiState.isImageUploading,
                    onAddClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.profile_setup_load_photo),
                    fontSize = 16.sp,
                    fontFamily = Nunito,
                    color = PrimaryPurple,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(24.dp))

                FormTextFields(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.name,
                    onValueChanged = {
                        profileViewModel.updateName(it)
                        if (nameError) nameError = false
                    },
                    text = stringResource(R.string.profile_setup_display_name_label),
                    textField = stringResource(R.string.profile_setup_display_name_example),
                    isError = nameError,
                    errorText = if (nameError) stringResource(R.string.profile_setup_error_enter_username) else null
                )

                Spacer(Modifier.height(16.dp))

                FormTextFields( // TODO: when user write user name, text field must add @ in start of the user name
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.userName,
                    onValueChanged = {
                        profileViewModel.updateUsername(it)
                        profileViewModel.checkUsernameAvailability(it)
                    },
                    text = stringResource(R.string.profile_setup_display_username_label),
                    textField = stringResource(R.string.profile_setup_display_username_example),
                    isError = isUserNameTaken,
                    errorText = if (isUserNameTaken) "This username already taken" else null,// TODO: add in the R.string
                    trailingIcon = { ValidationTrailingIcon(status = uiState.userNameStatus) }
                )


                Spacer(Modifier.height(16.dp))

                LockedEmailField(email = uiState.email)

                Spacer(modifier = Modifier.height(32.dp))

                SetupProfileButton(
                    profileViewModel = profileViewModel,
                    error = nameError,
                    name = uiState.name,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading &&
                            uiState.name.isNotBlank() &&
                            uiState.userName.isNotBlank() &&
                            uiState.userNameStatus == ProfileSetupViewModel.ValidationStatus.AVAILABLE,
                    text = stringResource(R.string.profile_setup_save_profile_button),
                    loadingText = stringResource(R.string.loading),
                    loading = uiState.isLoading,
                ) // TODO: if text fields - empty. add error than field cannot be empty and if username which user write already be in db show an error
            }
        }
    }
}


