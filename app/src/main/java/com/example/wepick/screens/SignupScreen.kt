package com.example.wepick.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.ui.theme.White
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


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

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
            colors = CardDefaults.cardColors(containerColor = CardYellow)
        ) {

            Column(
                modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                LoginCardText(
                    text = stringResource(id = R.string.registration_label_main),
                    color = White,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = PressStart2P,
                ) // Label text

                Spacer(modifier.height(12.dp))

                LoginCardText(
                    text = stringResource(id = R.string.registration_label_second),
                    color = TextTeal,
                    textAlign = TextAlign.Left,
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
                            fontFamily = PressStart2P
                        )
                    },
                    modifier = modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = PressStart2P,
                        color = TextTeal,
                        fontSize = 16.sp
                    ),
                )

                Spacer(modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = stringResource(R.string.login_enter_password_main), // Text: Enter you`re password
                            fontFamily = PressStart2P
                        )
                    },
                    modifier = modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = PressStart2P,
                        color = TextTeal,
                        fontSize = 16.sp
                    ),
                )
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    //   RetroCheckBox(rememberMeState) // Retro check box
                    ForgotPassword(navController) // forgot password text
                }


                //TODO: Change the logic of the button and also change text on the button
                Spacer(modifier.height(24.dp))
//                NextButton(
//                    navController = navController,
//                    modifier = modifier,
//                    route = ScreenNav.Main.route,
//                    enabled = isNameValid,
//                    onNextClick = {
//                        playerVM.updateUserName(name)
//                    })

                Button(
                    onClick = {
                        authViewModel.signup(email, password)

                    }, enabled = authState != AuthState.Loading
                ) {
                    Text(
                        "Create account"
                    )
                }
                //TODO: Change the logic of the button and also change text on the button

                LoginDivider()


                Spacer(modifier.height(28.dp))


                TextButton(onClick = {
                    navController.navigate(ScreenNav.Login.route)
                }) {
                    Text(
                        "Already have an account, Login",
                        fontSize = 10.sp,
                    )
                }


            }
        }
    }
}
