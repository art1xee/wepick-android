package com.example.wepick.screens.auth.profile_setup

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.screens.auth.login.FormTextFields
import com.example.wepick.ui.components.SetupProfileButton
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.FieldBorder
import com.example.wepick.ui.theme.InkSoft
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
    val name by profileViewModel.name.collectAsState()
    val userName by profileViewModel.userName.collectAsState()
    val email by profileViewModel.email.collectAsState()
    val isSaved by profileViewModel.isSaved.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    val photoUrl by profileViewModel.photoUrl.collectAsState()
    val isImageUploading by profileViewModel.isImageUploading.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                profileViewModel.uploadProfileImage(uri)
            }
        }
    )

    var nameError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.reloadData()
    }

    LaunchedEffect(isSaved) {
        if (isSaved) {
            profileViewModel.reloadData()

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
                    photoUrl = photoUrl,
                    isImageUploading = isImageUploading,
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
                    value = name,
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
                    value = userName,
                    onValueChanged = {
                        profileViewModel.updateUsername(it)
                        if (nameError) nameError = false
                    },
                    text = stringResource(R.string.profile_setup_display_username_label),
                    textField = stringResource(R.string.profile_setup_display_username_example),
                    isError = nameError,
                    errorText = if (nameError) stringResource(R.string.profile_setup_error_enter_username) else null
                )


                Spacer(Modifier.height(16.dp))

                LockedEmailField(email = email)

                Spacer(modifier = Modifier.height(32.dp))

                SetupProfileButton(
                    profileViewModel = profileViewModel,
                    error = nameError,
                    name = name,
                    modifier = modifier,
                    enabled = !isLoading,
                    text = stringResource(R.string.profile_setup_save_profile_button),
                    loadingText = stringResource(R.string.loading),
                    loading = isLoading,
                ) // TODO: if text fields - empty. add error than field cannot be empty
            }
        }
    }
}


@Composable
fun ProfileAvatar(
    photoUrl: String?,
    isImageUploading: Boolean,
    onAddClick: () -> Unit,
) {
    Box(
        modifier = Modifier.size(88.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFE49A), Color(0xFFFFC94D))
                    )
                )
                .border(3.dp, DarkButtonPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isImageUploading) {
                CircularProgressIndicator(
                    color = DarkButtonPurple,
                    modifier = Modifier.size(30.dp)
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
                        fontSize = 32.sp,
                        color = DarkButtonPurple,
                        fontWeight = FontWeight.Black
                    )
                )
            }
        }

        if (!isImageUploading) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(DarkButtonPurple)
                    .border(2.dp, CardYellow, CircleShape)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Photo",
                    tint = CardYellow,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun LockedEmailField(
    email: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.profile_setup_display_email_label),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = InkSoft,
            fontFamily = Nunito,
            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = {},
            enabled = false,
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = Color(0x0F241436),
                disabledBorderColor = FieldBorder,
                disabledTextColor = InkSoft,
                disabledPlaceholderColor = InkSoft
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = InkSoft,
                    modifier = Modifier.size(16.dp)
                )
            },
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        )
    }
}