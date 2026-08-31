package com.example.wepick.screens.auth.login



import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.screens.auth.components.EmailTextField
import com.example.wepick.screens.auth.components.ForgotPassword
import com.example.wepick.screens.auth.components.LoginDivider
import com.example.wepick.screens.auth.components.PasswordTextField
import com.example.wepick.ui.components.GoogleLoginButton
import com.example.wepick.ui.components.LoginButton
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.InkSoft
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White
import com.example.wepick.util.REGEX_LIST
import com.example.wepick.viewmodel.AuthState
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.PlayerViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier,
    playerVM: PlayerViewModel,
    authViewModel: AuthViewModel
) {

    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        Log.d("AuthDebug", "Текущее состояние: $authState")
        when (val state = authState) {
            is AuthState.Authenticated -> {
                Log.d("AuthDebug", "Переход на Home")
                navController.navigate(ScreenNav.Home.route) {
                    popUpTo(ScreenNav.Login.route) { inclusive = true }
                }
            }

            is AuthState.NeedsProfileSetup -> {
                Log.d("AuthDebug", "Переход на ProfileSetup")
                navController.navigate(ScreenNav.ProfileSetup.route) {
                    popUpTo(ScreenNav.Login.route) { inclusive = true }
                }

            }

            is AuthState.Error -> {
                Log.e("AuthDebug", "ОШИБКА АВТОРИЗАЦИИ: ${state.message}")
                playerVM.showLockedError(state.message.asString(context))
            }

            else -> Unit
        }
    }

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }


    val emailRegex = REGEX_LIST.toRegex()

    val isEmailValid = email.matches(emailRegex)
    val isPasswordValid = password.isNotEmpty()

    val focusManager = LocalFocusManager.current

    val isLoading = authState is AuthState.Loading


    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
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
                modifier.padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Login text (Pixel style with shadow)
                Text(
                    text = stringResource(id = R.string.login_title),
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

                Spacer(modifier.height(18.dp))

                Text(
                    text = stringResource(id = R.string.login_subtitle),
                    color = Black,
                    textAlign = TextAlign.Center,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp
                )

                Spacer(modifier.height(24.dp))

                // EMAIL Text field
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
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                )

                Spacer(modifier.height(16.dp))

                // PASSWORD Text field
                PasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChanged = {
                        password = it
                        if (passwordError) passwordError = false
                    },
                    text = stringResource(R.string.login_password_label),
                    textField = "password",
                    isError = passwordError,
                    errorText = if (passwordError) stringResource(R.string.login_error_invalid_credentials) else null,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                )

                ForgotPassword(
                    navController = navController
                )

                Spacer(modifier.height(24.dp))

                // LOGIN BUTTON
                LoginButton(
                    authViewModel = authViewModel,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    text = stringResource(R.string.login_button),
                    loadingText = stringResource(R.string.loading),
                    loading = isLoading,
                    formValid = true,
                    email = email,
                    password = password,
                    onClick = {
                        if (isEmailValid && isPasswordValid) {
                            authViewModel.login(email, password)
                        } else {
                            emailError = !isEmailValid
                            passwordError = !isPasswordValid
                        }
                    }
                )

                LoginDivider()

                // LOGIN WITH GOOGLE BUTTON
                GoogleLoginButton(
                    onClick = { authViewModel.loginWithGoogle(context) },
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.login_google_button),
                )

                Spacer(modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.login_no_account),
                        fontSize = 13.sp,
                        fontFamily = Nunito,
                        color = InkSoft
                    )
                    Text(
                        text = stringResource(R.string.login_create_account),
                        fontSize = 13.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentRed,
                        modifier = Modifier.clickable {
                            navController.navigate(ScreenNav.SignUp.route)
                        }
                    )
                }
            }
        }
    }
}
