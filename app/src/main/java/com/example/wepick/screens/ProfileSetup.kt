package com.example.wepick.screens

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.components.LoginButton
import com.example.wepick.ui.theme.*
import com.example.wepick.viewmodel.ProfileSetupViewModel

@Composable
fun ProfileSetup(
    navController: NavController,
    profileViewModel: ProfileSetupViewModel,
    modifier: Modifier = Modifier
) {
    val name by profileViewModel.name.collectAsState()
    val email by profileViewModel.email.collectAsState()
    val isSaved by profileViewModel.isSaved.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()


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
                    text = "PROFILE SETUP",
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
                    text = "Almost there! Let`s complete your profile.",
                    color = Black,
                    fontFamily = Nunito,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Заглушка для аватарки (потом заменить на Coil Image)
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = FieldBeige,
                    border = BorderStroke(2.dp, FieldBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "PHOTO",
                            color = InkSoft,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FormTextFields(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChanged = {
                        profileViewModel.updateName(it)
                        if (nameError) nameError = false
                    },
                    text = "Display Name",
                    textField = "e.g. John Doe",
                    isError = nameError,
                    errorText = if (nameError) "Name cannot be empty" else null
                )

                Spacer(Modifier.height(16.dp))

                FormTextFields(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChanged = {},
                    text = "Email (Verified)",
                    textField = "",
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            profileViewModel.saveProfile()
                        } else {
                            nameError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Black),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "SAVE PROFILE",
                            color = White,
                            fontFamily = PressStart2P,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

