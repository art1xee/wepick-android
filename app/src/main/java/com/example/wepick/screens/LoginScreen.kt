package com.example.wepick.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.components.GoogleLoginButton
import com.example.wepick.ui.components.LoginButton
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DeepPurple
import com.example.wepick.ui.theme.FieldBeige
import com.example.wepick.ui.theme.FieldBorder
import com.example.wepick.ui.theme.InkSoft
import com.example.wepick.ui.theme.MidPurple
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.AuthState
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.PlayerViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier,
    playerVM: PlayerViewModel,
    authViewModel: AuthViewModel
) {

    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                navController.navigate(ScreenNav.Home.route) {
                    popUpTo(ScreenNav.Login.route) { inclusive = true }
                }
            }

            is AuthState.Error -> {
                playerVM.showLockedError(state.message)
            }

            else -> Unit
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isFormValid by remember {
        derivedStateOf {
            email.trim().isNotEmpty() && password.trim().isNotEmpty()
        }
    }
    val isLoading = authState is AuthState.Loading


    Column(
        modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(MidPurple, DeepPurple)
                )
            )
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
                // Login text (Pixel style with shadow)
                Text(
                    text = stringResource(id = R.string.login_main),
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

                // Greeting text TODO: change fontFamily
                Text(
                    text = stringResource(id = R.string.login_greeting_main),
                    color = Black,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    lineHeight = 24.sp
                )

                Spacer(modifier.height(24.dp))

                // EMAIL Text field
                EmailTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChanged = { email = it },
                    text = stringResource(R.string.login_email_main),
                    textField = "email@example.com"
                )

                Spacer(modifier.height(16.dp))

                // PASSWORD Text field
                PasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChanged = { password = it },
                    text = stringResource(R.string.login_password_main),
                    textField = stringResource(R.string.login_password_main)
                )

                ForgotPassword()

                Spacer(modifier.height(24.dp))

                // ERROR TEXT IF FORM IS NOT VALID
                AnimatedVisibility(
                    visible = playerVM.errorMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkOut()
                ) {
                    Text(
                        text = playerVM.errorMessage ?: "",
                        color = AccentRed,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                // LOGIN BUTTON
                LoginButton(
                    authViewModel = authViewModel,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isLoading,
                    text = stringResource(R.string.login_enter_main),
                    loadingText = stringResource(R.string.loading),
                    loading = isLoading,
                    formValid = isFormValid,
                    email = email,
                    password = password,
                )

                LoginDivider()

                // LOGIN WITH GOOGLE BUTTON
                GoogleLoginButton(
                    onClick = { authViewModel.loginWithGoogle(context) },
                    modifier = Modifier.fillMaxWidth(),
                    text = "Продовжити з Google", // TODO change localization text
                )

                Spacer(modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "У мене немає акаунту, ", // TODO change localization text
                        fontSize = 13.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = InkSoft
                    )
                    Text(
                        text = "створити.", // TODO change localization text
                        fontSize = 13.sp,
                        fontFamily = FontFamily.SansSerif,
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


// =============================== COMPOSABLE FUNCTIONS ==================================
@Composable
fun LoginCardText(
    text: String,
    color: Color,
    textAlign: TextAlign,
    style: TextStyle,
    fontFamily: FontFamily,
) {
    Text(
        text = text,
        color = color,
        textAlign = textAlign,
        style = style,
        fontFamily = fontFamily,
        fontSize = 18.sp
    )
}

@Composable
fun ForgotPassword() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = stringResource(id = R.string.login_forgot_password_main),
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = InkSoft,
            fontFamily = FontFamily.SansSerif,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { /* TODO */ }
        )
    }
}

@Composable
fun LoginDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp,
            color = Black.copy(alpha = 0.15f)
        )
        Text(
            text = stringResource(id = R.string.login_or_main),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = InkSoft
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp,
            color = Black.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun FormTextFields(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    text: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textField: String,
) {
    Column(modifier = modifier) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = InkSoft,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FieldBeige,
                unfocusedContainerColor = FieldBeige,
                focusedBorderColor = TextTeal,
                unfocusedBorderColor = FieldBorder,
            ),
            textStyle = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Black,
                fontSize = 15.sp
            ),
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            placeholder = {
                Text(
                    text = textField.lowercase(), // TODO change localization text
                    color = Color(0xFFB7A574),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        )
    }
}

@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    text: String,
    textField: String,
) {
    FormTextFields(
        modifier = modifier,
        value = value,
        onValueChanged = onValueChanged,
        text = text,
        textField = textField,
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Mail,
                contentDescription = "Email Icon",
                tint = InkSoft
            )
        }
    )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    text: String,
    textField: String,
) {

    var visiblePassword by remember { mutableStateOf(false) }
    FormTextFields(
        modifier = modifier,
        value = value,
        onValueChanged = onValueChanged,
        text = text,
        visualTransformation = if (visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
        textField = textField,
        trailingIcon = {
            val image = if (visiblePassword)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            IconButton(
                onClick = {
                    visiblePassword = !visiblePassword
                }) {
                Icon(
                    imageVector = image,
                    contentDescription = if (visiblePassword) "Hide password" else "Show Password",
                    tint = InkSoft
                )
            }
        }
    )
}