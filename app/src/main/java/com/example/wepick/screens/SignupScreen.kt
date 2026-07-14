package com.example.wepick.screens

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.components.CreateAccountButton
import com.example.wepick.ui.components.GoogleLoginButton
import com.example.wepick.ui.components.LoginButton
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.InkSoft
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.ui.theme.White
import com.example.wepick.util.REGEX_LIST
import com.example.wepick.viewmodel.AuthState
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.ContentViewModel
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.PlayerViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier,
    playerVM: PlayerViewModel,
    authViewModel: AuthViewModel,
) {

    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf(false) }


    val emailRegex = REGEX_LIST.toRegex()
    val isEmailValid = email.matches(emailRegex)
    val isPasswordValid =
        password.isNotEmpty() && password == confirmPassword && password.length < 6

    val formErrorMessage = when {
        confirmPasswordError -> stringResource(R.string.signup_form_password_error)
        emailError -> stringResource(R.string.forgot_password_email_error)
        passwordError -> "The password cannot be empty"

        else -> null
    }


    val isFormValid = isEmailValid && isPasswordValid

    val isLoading = authState is AuthState.Loading


    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {

                navController.navigate(ScreenNav.Home.route) {
                    popUpTo(ScreenNav.Login.route) { inclusive = true }
                }
            }

            is AuthState.Error -> {
                Toast.makeText(
                    context, (authState as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> Unit
        }
    }
    val rememberMeState = remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardYellow),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier.padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Registration text (Pixel style with shadow)
                Text(
                    text = stringResource(id = R.string.registration_label_main),
                    color = White,
                    fontFamily = PressStart2P,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFC58A1E),
                            offset = Offset(8f, 8f),
                            blurRadius = 0f,
                        )
                    )
                )

                Spacer(modifier.height(18.dp))

                Text(
                    text = stringResource(id = R.string.registration_label_second),
                    color = Black,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = Nunito,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
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
                    text = stringResource(R.string.login_email_main),
                    textField = "email@example.com",
                    isError = emailError,
                )

                Spacer(modifier.height(12.dp))

                // PASSWORD Text field
                PasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChanged = {
                        password = it
                        if (passwordError) passwordError = false
                    },
                    text = stringResource(R.string.login_password_main),
                    textField = "password",
                    isError = passwordError,
                )
                Spacer(modifier.height(12.dp))

                // CONFIRM PASSWORD Text field
                PasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = confirmPassword,
                    onValueChanged = {
                        confirmPassword = it
                        if (confirmPasswordError) confirmPasswordError = false
                    },
                    text = "Confirm password",
                    textField = "repeat password",
                    isError = confirmPasswordError,
                )

                if (formErrorMessage != null) {
                    Text(
                        text = formErrorMessage,
                        color = AccentRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = Nunito
                    )
                }

                Spacer(modifier.height(20.dp))

                CreateAccountButton(
                    authViewModel = authViewModel,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    text = "Create",
                    loadingText = stringResource(R.string.loading),
                    loading = isLoading,
                    formValid = true,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    onClick = {
                        if (isEmailValid && isPasswordValid) {
                            authViewModel.signup(email, password, confirmPassword)
                        } else {
                            emailError = !isEmailValid
                            passwordError = !isPasswordValid
                            confirmPasswordError = !isPasswordValid
                        }
                    }
                )

                LoginDivider()

                // LOGIN WITH GOOGLE BUTTON
                GoogleLoginButton(
                    onClick = { authViewModel.loginWithGoogle(context) },
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.login_google_main),
                )

                Spacer(modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account, ",
                        fontSize = 13.sp,
                        fontFamily = Nunito,
                        color = InkSoft
                    )
                    Text(
                        text = "Login",
                        fontSize = 13.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentRed,
                        modifier = Modifier.clickable {
                            navController.navigate(ScreenNav.Login.route)
                        }
                    )
                }


            }
        }
    }
}
