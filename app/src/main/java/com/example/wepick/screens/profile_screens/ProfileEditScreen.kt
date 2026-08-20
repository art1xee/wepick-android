package com.example.wepick.screens.profile_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wepick.screens.auth.login.FormTextFields
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.ProfileSetupViewModel

@Composable
fun ProfileEditScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileSetupViewModel,
    modifier: Modifier = Modifier
) {
    val authState = authViewModel.authState.observeAsState()


    val currentName by profileViewModel.name.collectAsState()
    val currentUserName by profileViewModel.userName.collectAsState()

    val context = LocalContext.current

    var textStateName by remember(currentName) { mutableStateOf(currentName) }
    var textStateUserName by remember(currentUserName) { mutableStateOf(currentUserName) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    //TODO: good idea add this value when user gonna create account
//    val birthDate by profileViewModel.birthDate.collectAsState()

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
                // Label text for Edit profile screen
                //TODO: add this text block on the components app level
                Text(
                    text = "Edit profile",
                    fontFamily = PressStart2P,
                    fontSize = 18.sp,
                    color = White,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFC58A1E),
                            offset = Offset(x = 8f, y = 8f),
                            blurRadius = 0f
                        )
                    )
                )
                Spacer(Modifier.height(8.dp))

                FormTextFields(
                    modifier = Modifier.fillMaxWidth(),
                    value = textStateName,
                    onValueChanged = { newValue ->
                        textStateName = newValue
                        errorMsg = null
                    },
                    text = "Change your name",
                    textField = currentName, // the previous name TODO: <- add in the text field
                )

                Spacer(Modifier.height(12.dp))

                FormTextFields(
                    modifier = Modifier.fillMaxWidth(),
                    value = textStateUserName,
                    onValueChanged = { newValue ->
                        textStateUserName = newValue
                        errorMsg = null
                    },
                    text = "Change your username",
                    textField = currentUserName, // the previous username TODO: <- add in the text field
                )
                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        color = AccentRed,
                        fontSize = 12.sp,
                        fontFamily = Nunito,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        val trimmedName = textStateName.trim()
                        val trimmedUserName = textStateUserName.trim()

                        // create list of the changes
                        val updates = mutableMapOf<ProfileSetupViewModel.ProfileField, String>()

                        // if the name of the user changed - add in the renewal list
                        if (trimmedName != currentName.trim()) {
                            updates[ProfileSetupViewModel.ProfileField.NAME] = trimmedName
                        }

                        // if the username of the user changed - add in the renewal list
                        if (trimmedUserName != currentUserName.trim()) {
                            updates[ProfileSetupViewModel.ProfileField.USERNAME] = trimmedUserName
                        }

                        // add if-else for email, bio, birthdate

                        if (updates.isEmpty()) {
                            errorMsg = "You don`t change anything"
                            return@Button
                        }

                        isLoading = true

                        // call func
                        profileViewModel.saveMultipleFields(
                            updates = updates,
                            onSuccess = {
                                isLoading = false
                                navController.popBackStack()
                            },
                            onError = { errorUiText ->
                                isLoading = false
                                errorMsg = errorUiText.asString(context)
                            },


                            )
                    }, modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLoading) "Loading..." else "Save"
                    )
                }
            }
        }
    }
}
