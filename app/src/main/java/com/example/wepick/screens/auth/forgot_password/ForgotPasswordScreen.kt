package com.example.wepick.screens.auth.forgot_password

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.R
import com.example.wepick.screens.auth.login.EmailTextField
import com.example.wepick.ui.components.LoginButton
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.ProgressGray
import com.example.wepick.ui.theme.TealSuccess
import com.example.wepick.ui.theme.White
import com.example.wepick.util.REGEX_LIST
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.PlayerViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    playerVM: PlayerViewModel,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var currentStep by remember { mutableIntStateOf(1) }
    val context = LocalContext.current

    val emailRegex = REGEX_LIST.toRegex()


    val isEmailValid = remember(email) {
        email.matches(emailRegex)
    }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    // TOP BAR
    {
        Row(
            modifier = Modifier
                .padding(top = 44.dp, bottom = 8.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            IconButton(
                onClick = {
                    if (currentStep == 1) navController.popBackStack() else currentStep = 1
                },
                modifier = Modifier
                    .size(38.dp)
                    .background(White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))

            ) {
                Icon(
                    Icons.Rounded.ArrowBackIosNew,
                    contentDescription = null,
                    tint = CardYellow,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = if (currentStep == 1) stringResource(R.string.forgot_password_step) else stringResource(
                    R.string.forgot_password_complete
                ),
                color = White.copy(alpha = 0.55f),
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
        }
        // PROGRESS DOTS
        Row(
            modifier = Modifier
                .padding(vertical = 14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(2) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            if (index + 1 < currentStep) TealSuccess
                            else if (index + 1 == currentStep) CardYellow
                            else ProgressGray,
                            RoundedCornerShape(4.dp)
                        )
                )

            }
        }
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp),
            colors = CardDefaults.cardColors(containerColor = CardYellow),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentStep == 1) {
                    // STEP 1: EMAIL INPUT
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(DarkButtonPurple, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Mail,
                            contentDescription = null,
                            tint = CardYellow,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = stringResource(R.string.login_forgot_password),
                        fontFamily = PressStart2P,
                        color = White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(0xFFC58A1E),
                                offset = Offset(8f, 8f),
                                blurRadius = 0f
                            )
                        )
                    )
                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = stringResource(R.string.forgot_password_description),
                        fontFamily = Nunito,
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        textAlign = TextAlign.Center,

                        )

                    Spacer(Modifier.height(26.dp))

                    EmailTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email,
                        onValueChanged = {
                            email = it
                            if (emailError) emailError = false
                        },
                        text = stringResource(R.string.login_email_label),
                        textField = "email@example.com",
                        isError = emailError,
                        errorText = stringResource(R.string.forgot_password_email_error),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                    )
                    Spacer(Modifier.height(26.dp))

                    LoginButton(
                        authViewModel = authViewModel,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true,
                        text = stringResource(R.string.forgot_password_send_button),
                        loadingText = stringResource(R.string.forgot_password_sending),
                        loading = false,
                        formValid = true,
                        email = "", password = "",
                        onClick = {
                            if (isEmailValid) {
                                authViewModel.resetPassword(
                                    email = email,
                                    onSuccess = { currentStep = 2 },
                                    onError = { errorUiText ->
                                        playerVM.showLockedError(errorUiText.asString(context))
                                    }
                                )
                            } else {
                                emailError = true
                            }
                        }
                    )

                } else {
                    // STEP 2: SUCCESS
                    Spacer(Modifier.weight(0.5f))
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .background(TealSuccess, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(Modifier.height(22.dp))

                    Text(
                        text = stringResource(R.string.forgot_password_complete), //TODO: add this string in the R.string
                        fontFamily = PressStart2P,
                        color = White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,

                        )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = stringResource(R.string.forgot_password_success),
                        fontFamily = Nunito,
                        color = Black,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                    Spacer(Modifier.weight(1f))

                    LoginButton(
                        authViewModel = authViewModel,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true,
                        text = stringResource(R.string.forgot_password_back_to_login),
                        loadingText = "",
                        loading = false,
                        formValid = true,
                        email = "",
                        password = "",
                        onClick = {
                            navController.popBackStack()
                        }
                    )


                }

            }
        }


    }
}