package com.example.wepick.screens


import android.app.Activity
import android.content.Context
import android.widget.Space
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawModifierNode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.size.Size
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.ui.components.NextButton
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.AuthState
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.PlayerViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

private lateinit var firebaseAuth: FirebaseAuth

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
    val authError = "The invalid email or password"

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
    var visiblePassword by remember { mutableStateOf(false) }
    val rememberMeState = remember { mutableStateOf(false) }

    val credentialFill = stringResource(R.string.credential_fill)

    val isFormValid by remember {
        derivedStateOf {
            email.trim().isNotEmpty() && password.trim().isNotEmpty()
        }
    }
    val isLoading = authState is AuthState.Loading

//    // logo animation
//    val infiniteTransition = rememberInfiniteTransition()
//    val offsetY by infiniteTransition.animateValue(
//        initialValue = 0.dp,
//        targetValue = (-15).dp,
//        typeConverter = Dp.VectorConverter,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1000, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "offsetY"
//    )

    Column(
        modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardYellow)
        ) {

            Column(
                modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.logo),
//                    contentDescription = "Bouncing Logo",
//                    modifier = Modifier
//                        .size(140.dp)
//                        .offset(y = offsetY)
//                )

                LoginCardText(
                    text = stringResource(id = R.string.login_main), // Text: Login
                    color = White,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = PressStart2P,
                ) // Label text

                Spacer(modifier.height(12.dp))

                LoginCardText(
                    text = stringResource(id = R.string.login_greeting_main), // Text: Ми раді бачити тебе знову
                    color = TextTeal,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                    fontFamily = PressStart2P,
                ) // greeting text

                Spacer(modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = stringResource(R.string.login_enter_email_main), // Text: enter you`re email
                            fontFamily = PressStart2P,
                            fontSize = 14.sp
                        )
                    },
                    modifier = modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = PressStart2P,
                        color = TextTeal,
                        fontSize = 14.sp
                    ),
                    trailingIcon = {
                        val image = Icons.Filled.Mail
                        Icon(
                            imageVector = image,
                            contentDescription = "Mail text field",
                            tint = TextTeal
                        )
                    }
                )
//TODO: Make text field for email and password in separate func
                Spacer(modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = stringResource(R.string.login_enter_password_main), // Text: Enter you`re password
                            fontFamily = PressStart2P,
                            fontSize = 14.sp
                        )
                    },
                    modifier = modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = PressStart2P,
                        color = TextTeal,
                        fontSize = 14.sp
                    ),
                    visualTransformation = if (visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (visiblePassword)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        IconButton(onClick = {
                            visiblePassword = !visiblePassword
                        }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (visiblePassword) "Hide password" else "Show password",
                                tint = TextTeal
                            )
                        }
                    }
                )
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    ForgotPassword() // forgot password text
                }


                //TODO: Change the logic of the button and also change text on the button
                Spacer(modifier.height(24.dp))

                AnimatedVisibility( // for showing error when user write invalid password or email
                    visible = playerVM.errorMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkOut()
                ) {
                    Text(
                        text = playerVM.errorMessage ?: "",
                        color = AccentRed,
                        fontFamily = PressStart2P,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        if (isFormValid) {
                            authViewModel.login(email, password)
                        } else {
                            playerVM.showLockedError("ENTER EMAIL AND PASSWORD")
                        }

                    },
                    enabled = isFormValid && !isLoading,
                    modifier = Modifier.fillMaxWidth()

                ) {
                    if (isLoading) {
                        Text(
                            text = "Loading...",
                            fontFamily = PressStart2P,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "Login",
                            fontFamily = PressStart2P,
                            fontSize = 12.sp
                        )
                    }
                }


                //TODO: Change the logic of the button and also change text on the button

                LoginDivider()

                // google button
                Button(
                    onClick = { authViewModel.loginWithGoogle(context) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.login_google_main),
                            fontFamily = PressStart2P,
                            fontSize = 14.sp
                        )
                    }
                }

                // gitHub button
//                Button(
//                    onClick = { authViewModel.loginWithGitHub(context as Activity) },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292E))
//                ) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_github),
//                            contentDescription = null,
//                            modifier = Modifier.size(24.dp)
//                        )
//                        Spacer(Modifier.width(8.dp))
//                    }
//                    Text(
//                        text = stringResource(id = R.string.login_apple_main),
//                        fontFamily = PressStart2P,
//                        fontSize = 14.sp
//                    )
//                }

                //TODO: Button for GUEST MODE
                Button(onClick = {}) {
                    Text(
                        text = "Guest Mode"
                    )
                }

                Spacer(modifier.height(28.dp))


                TextButton(onClick = {
                    navController.navigate(ScreenNav.SignUp.route)
                }) {
                    Text(
                        "У мене нема аккаунту (створити)",
                        fontSize = 12.sp,
                        fontFamily = PressStart2P,
                        color = AccentRed,
                        textAlign = TextAlign.Center
                    )
                }


            }
        }
    }
}

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

//@Composable
//fun RetroCheckBox(
//    checkedState: MutableState<Boolean>,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier.clickable { checkedState.value = !checkedState.value },
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Box(
//            modifier = Modifier
//                .size(24.dp)
//                .background(Color.White, shape = RectangleShape)
//                .border(2.dp, TextTeal),
//            contentAlignment = Alignment.Center
//        ) {
//            if (checkedState.value) {
//                Text(
//                    text = "X",
//                    color = AccentRed,
//                    fontFamily = PressStart2P,
//                    fontSize = 14.sp,
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        Text(
//            text = stringResource(id = R.string.login_remember_user_main),
//            fontFamily = PressStart2P,
//            fontSize = 10.sp,
//            color = TextTeal,
//        )
//    }
//}

@Composable
fun ForgotPassword(
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.login_forgot_password_main),
            fontSize = 10.sp,
            color = AccentRed,
            fontFamily = PressStart2P,
        )
    }
}

@Composable
fun LoginDivider(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // left side
        Divider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = PrimaryPurple
        )

        // text center
        Text(
            text = stringResource(id = R.string.login_or_main),
            modifier = Modifier.padding(horizontal = 8.dp),
            fontFamily = PressStart2P,
            fontSize = 10.sp,
            color = PrimaryPurple
        )

        // right side
        Divider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = PrimaryPurple
        )
    }
}
