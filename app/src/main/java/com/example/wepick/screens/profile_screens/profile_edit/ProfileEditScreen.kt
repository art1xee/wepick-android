package com.example.wepick.screens.profile_screens.profile_edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.wepick.R
import com.example.wepick.screens.auth.components.FormTextFields
import com.example.wepick.screens.profile_screens.components.ValidationTrailingIcon
import com.example.wepick.ui.components.RetroEditProfileButton
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.profile_view_model.ProfileSetupViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

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

    var textStateName by remember() { mutableStateOf("") }
    var textStateUserName by remember() { mutableStateOf("") }
    var textStateEmail by remember() { mutableStateOf("") }
    var textStateBio by remember() { mutableStateOf("") }
    var textStateBirthday by remember() { mutableStateOf("") }

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    LaunchedEffect(uiState.name, uiState.userName, uiState.email, uiState.bio, uiState.birthday) {
        if (textStateName.isEmpty() && uiState.name.isNotEmpty()) {
            textStateName = uiState.name
        }
        if (textStateUserName.isEmpty() && uiState.userName.isNotEmpty()) {
            textStateUserName = uiState.userName
        }
        if (textStateEmail.isEmpty() && uiState.email.isNotEmpty()) {
            textStateEmail = uiState.email
        }
        if (textStateBio.isEmpty() && uiState.bio.isNotEmpty()) {
            textStateBio = uiState.bio
        }
        if (textStateBirthday.isEmpty() && uiState.birthday.isNotEmpty()) {
            textStateBirthday = uiState.birthday.filter { it.isDigit() }
        }
    }
    val focusManager = LocalFocusManager.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val isUserNameTaken = uiState.userNameStatus == ProfileSetupViewModel.ValidationStatus.TAKEN
    val isEmailTaken = uiState.emailStatus == ProfileSetupViewModel.ValidationStatus.TAKEN
    val isBioOverLimit = textStateBio.length > 150
    val isBirthdayValid = isValidDate(textStateBirthday)

    //TODO: good idea add this value when user gonna create account

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .pointerInput(
                Unit
            ) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .imePadding(),
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


                //NAME OF THE USER FIELD
                FormTextFields(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = textStateName,
                    onValueChanged = { newValue ->
                        textStateName = newValue
                        errorMsg = null
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    text = stringResource(R.string.profile_edit_name_field),
                    textField = stringResource(
                        R.string.profile_edit_previous_name_hint,
                        uiState.name
                    )
                )

                Spacer(Modifier.height(12.dp))

                //USERNAME FIELD
                FormTextFields(
                    modifier = Modifier.fillMaxWidth(),
                    value = textStateUserName,
                    onValueChanged = { newValue ->
                        textStateUserName = newValue
                        if (newValue.trim() != uiState.userName.trim()) {
                            profileViewModel.checkUsernameAvailability(newValue)
                        } else {
                            profileViewModel.resetUserNameValidation()
                        }
                        errorMsg = null
                    },
                    isError = isUserNameTaken,
                    errorText = if (isUserNameTaken) stringResource(R.string.profile_edit_username_taken) else null,
                    text = stringResource(R.string.profile_edit_user_name_field),
                    textField = stringResource(
                        R.string.profile_edit_previous_user_name_hint,
                        uiState.userName
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    trailingIcon = { ValidationTrailingIcon(status = uiState.userNameStatus) }
                )

                Spacer(Modifier.height(12.dp))

                //BIO FIELD
                FormTextFields(
                    modifier = modifier
                        .fillMaxWidth()
                        .heightIn(100.dp),
                    value = textStateBio,
                    onValueChanged = { newValue ->
                        textStateBio = newValue
                        errorMsg = null
                    },
                    singleLine = false,
                    supportingText = {
                        Text(
                            text = "${textStateBio.length} / 150",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            fontFamily = Nunito,
                            fontSize = 12.sp
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    text = stringResource(R.string.profile_edit_about_me_label),
                    textField = stringResource(R.string.profile_edit_about_me_hint),
                    isError = isBioOverLimit,
                )
                Spacer(Modifier.height(12.dp))


                //BIRTHDAY FIELD (optional field for fill)
                FormTextFields(
                    modifier = modifier.fillMaxWidth(),
                    value = textStateBirthday,
                    onValueChanged = { newValue ->
                        val digitsOnly = newValue.filter { it.isDigit() }
                        if (digitsOnly.length <= 8)
                            textStateBirthday = digitsOnly
                    },
                    visualTransformation = DateVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    text = stringResource(R.string.profile_edit_birthday_label),
                    textField = stringResource(R.string.profile_edit_birthday_format),
                    isError = !isBirthdayValid && textStateBirthday.isNotEmpty(),
                    errorText = if (!isBirthdayValid && textStateBirthday.isNotEmpty()) stringResource(
                        R.string.profile_edit_birthday_valid_date
                    ) else null,

                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                )

                Spacer(Modifier.height(12.dp))

                //EMAIL FIELD
                if (!profileViewModel.isGoogleSignup) {
                    FormTextFields(
                        modifier = Modifier.fillMaxWidth(),
                        value = textStateEmail,
                        onValueChanged = { newValue ->
                            textStateEmail = newValue
                            if (newValue.trim() != uiState.email.trim()) {
                                profileViewModel.checkEmailAvailability(newValue)
                            } else {
                                profileViewModel.resetEmailValidation()
                            }

                            errorMsg = null
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        isError = isEmailTaken,
                        errorText = if (isEmailTaken) stringResource(R.string.profile_edit_email_taken) else null,
                        text = stringResource(R.string.profile_edit_email_field),
                        textField = stringResource(
                            R.string.profile_edit_previous_email_hint,
                            uiState.email
                        ),
                        trailingIcon = { ValidationTrailingIcon(status = uiState.emailStatus) }
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


                val trimmedName = textStateName.trim()
                val trimmedUserName = textStateUserName.trim()
                val trimmedBio = textStateBio.trim()
                val trimmedBirthday = textStateBirthday.trim()
                val trimmedEmail = textStateEmail.trim()

                val isNameChanged = textStateName.trim() != uiState.name.trim()
                val isUserNameChanged = textStateUserName.trim() != uiState.userName.trim()
                val isBioChanged = textStateBio.trim() != uiState.bio.trim()
                val isBirthdayChanged =
                    textStateBirthday.trim() != (uiState.birthday ?: "").filter { it.isDigit() }
                val isEmailChanged = textStateEmail.trim() != uiState.email.trim()
                val hasChanges =
                    isNameChanged || isUserNameChanged || isEmailChanged || isBioChanged || isBirthdayChanged

                val isUserNameValid =
                    !isUserNameChanged || uiState.userNameStatus == ProfileSetupViewModel.ValidationStatus.AVAILABLE
                val isEmailValid =
                    !isEmailChanged || uiState.emailStatus == ProfileSetupViewModel.ValidationStatus.AVAILABLE

                RetroEditProfileButton(

                    modifier = Modifier.fillMaxWidth(),
                    loading = isLoading,
                    enabled = !isLoading &&
                            hasChanges &&
                            isBirthdayValid &&
                            isEmailValid &&
                            isUserNameValid &&
                            !isBioOverLimit &&
                            textStateName.isNotBlank()
                            && textStateUserName.isNotBlank(),
                    text = stringResource(R.string.profile_edit_button_edit),
                    loadingText = stringResource(R.string.profile_edit_button_edit_loading),
                    onClick = {

                        val updates = mutableMapOf<ProfileSetupViewModel.ProfileField, String>()

                        if (isNameChanged) {
                            updates[ProfileSetupViewModel.ProfileField.NAME] = trimmedName
                        }
                        if (isUserNameChanged) {
                            updates[ProfileSetupViewModel.ProfileField.USERNAME] = trimmedUserName
                        }
                        if (isEmailChanged) {
                            updates[ProfileSetupViewModel.ProfileField.EMAIL] = trimmedEmail
                        }
                        if (isBioChanged) {
                            updates[ProfileSetupViewModel.ProfileField.USER_BIO] = trimmedBio
                        }

                        val formattedBirthdayToSave = if (trimmedBirthday.length == 8) {
                            "${trimmedBirthday.substring(0, 2)}.${
                                trimmedBirthday.substring(
                                    2,
                                    4
                                )
                            }.${trimmedBirthday.substring(4, 8)}"
                        } else {
                            trimmedBirthday
                        }
                        if (isBirthdayChanged) {
                            updates[ProfileSetupViewModel.ProfileField.BIRTHDAY] =
                                formattedBirthdayToSave
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


fun isValidDate(dateDigits: String): Boolean {
    if (dateDigits.isEmpty()) return true
    if (dateDigits.length != 8) return false

    return try {
        val formatter = SimpleDateFormat("ddMMyyyy", java.util.Locale.getDefault()).apply {
            isLenient = false
        }
        val parsedDate = formatter.parse(dateDigits) ?: return false

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val cal = Calendar.getInstance().apply { time = parsedDate }
        val year = cal.get(Calendar.YEAR)

        year in 1920..currentYear
    } catch (e: Exception) {
        false
    }
}




