package com.example.wepick.screens.profile_screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.wepick.R
import com.example.wepick.screens.auth.login.FormTextFields
import com.example.wepick.ui.components.RetroEditProfileButton
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.profile_view_model.ProfileSetupViewModel

@Composable
fun ProfileEditScreen(
    navController: NavHostController,
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

    val context = LocalContext.current

    var textStateName by remember(uiState.name) { mutableStateOf(uiState.name) }
    var textStateUserName by remember(uiState.userName) { mutableStateOf(uiState.userName) }
    var textStateEmail by remember(uiState.email) { mutableStateOf(uiState.email) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    //TODO: good idea add this value when user gonna create account
    //val birthDate by profileViewModel.birthDate.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    Column(
        Modifier
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp, horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 3.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(32.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp)
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = DarkButtonPurple,
                            modifier = Modifier
                                .size(18.dp)
                                .offset(x = 2.dp)
                        )
                    }
                    //TODO: add this text block on the components app level
                    Text(
                        text = stringResource(R.string.profile_edit_label),
                        fontFamily = PressStart2P,
                        fontSize = 18.sp,
                        color = White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 48.dp),
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(0xFFC58A1E),
                                offset = Offset(x = 8f, y = 8f),
                                blurRadius = 0f
                            )
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))

                EditAvatar(
                    photoUrl = uiState.photoUrl,
                    onAddClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    isImageUploading = uiState.isImageUploading,
                )

                Spacer(Modifier.height(16.dp))

                FormTextFields(
                    modifier = Modifier.fillMaxWidth(),
                    value = textStateName,
                    onValueChanged = { newValue ->
                        textStateName = newValue
                        errorMsg = null
                    },
                    text = stringResource(R.string.profile_edit_name_field),
                    textField = stringResource(
                        R.string.profile_edit_previous_name_field,
                        uiState.name
                    )
                )

                Spacer(Modifier.height(12.dp))

                FormTextFields(
                    modifier = Modifier.fillMaxWidth(),
                    value = textStateUserName,
                    onValueChanged = { newValue ->
                        textStateUserName = newValue
                        errorMsg = null
                    },
                    text = stringResource(R.string.profile_edit_user_name_field),
                    textField = stringResource(
                        R.string.profile_edit_previous_user_name_field,
                        uiState.userName
                    )
                )

                Spacer(Modifier.height(12.dp))


                if (!profileViewModel.isGoogleSignup) {
                    FormTextFields(
                        modifier = Modifier.fillMaxWidth(),
                        value = textStateEmail,
                        onValueChanged = { newValue ->
                            textStateEmail = newValue
                            errorMsg = null
                        },
                        text = stringResource(R.string.profile_edit_email_field),
                        textField = stringResource(
                            R.string.profile_edit_previous_email_field,
                            uiState.email
                        ),
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        color = AccentRed,
                        fontSize = 12.sp,
                        fontFamily = Nunito,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }


                RetroEditProfileButton(
                    modifier = Modifier.fillMaxWidth(),
                    loading = isLoading,
                    enabled = !isLoading,
                    text = stringResource(R.string.profile_edit_button_edit),
                    loadingText = stringResource(R.string.profile_edit_button_edit_loading),
                    onClick = {
                        val trimmedName = textStateName.trim()
                        val trimmedUserName = textStateUserName.trim()
                        val trimmedEmail = textStateEmail.trim()

                        val updates = mutableMapOf<ProfileSetupViewModel.ProfileField, String>()

                        if (trimmedName != uiState.name.trim()) {
                            updates[ProfileSetupViewModel.ProfileField.NAME] = trimmedName
                        }
                        if (trimmedUserName != uiState.userName.trim()) {
                            updates[ProfileSetupViewModel.ProfileField.USERNAME] = trimmedUserName
                        }
                        if (trimmedEmail != uiState.email.trim()) {
                            updates[ProfileSetupViewModel.ProfileField.EMAIL] = trimmedEmail
                        }


                        isLoading = true

                        profileViewModel.saveMultipleFields(
                            updates = updates,
                            onSuccess = {
                                isLoading = false
                                navController.popBackStack()
                            },
                            onError = { errorUiText ->
                                isLoading = false
                                errorMsg = errorUiText.asString(context)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun EditAvatar(
    photoUrl: String?,
    onAddClick: () -> Unit,
    isImageUploading: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(White)
                .border(
                    1.dp,
                    DarkButtonPurple, CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isImageUploading) {
                CircularProgressIndicator(
                    color = DarkButtonPurple,
                    modifier = Modifier.size(24.dp)
                )
            } else if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "?",
                    style = TextStyle(
                        fontFamily = PressStart2P,
                        fontSize = 24.sp,
                        color = DarkButtonPurple,
                        fontWeight = FontWeight.Black
                    )
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        if (!isImageUploading) {
            Text(
                text = stringResource(R.string.profile_edit_change_photo),
                style = TextStyle(
                    fontFamily = Nunito,
                    fontSize = 12.sp,
                    color = DarkButtonPurple,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onAddClick() }
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
            )

        }
    }
}





