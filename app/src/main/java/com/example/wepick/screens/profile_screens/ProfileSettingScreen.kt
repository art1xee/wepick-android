package com.example.wepick.screens.profile_screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.AuthState
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.PlayerViewModel
import com.example.wepick.viewmodel.ProfileSetupViewModel

@Composable
fun ProfileSettingScreen(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    playerVM: PlayerViewModel,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileSetupViewModel
) {
    val authState = authViewModel.authState.observeAsState()

    val name by profileViewModel.name.collectAsState()
    val userName by profileViewModel.userName.collectAsState()
    val email by profileViewModel.email.collectAsState()

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

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }


    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(ScreenNav.Login.route)
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                    .padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text( // TODO make this text in separate func
                    text = "Profile",
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

                ProfileInfoBlock(
                    photoUrl = photoUrl,
                    isImageUploading = isImageUploading,
                    onAddClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    name = name,
                    userName = userName,
                    email = email
                ) // TODO: when user save profile icon from "profile setting screen" the avatar doesn't save when app is reloaded

                Spacer(Modifier.height(10.dp))

                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.PersonalData.route,
                    contentDescription = "Personal Data",
                    icon = Icons.Default.Person,
                    text = "Personal Data" // Про користувача / Про пользователя
                )

                Spacer(Modifier.height(4.dp))

                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.AppSetting.route,
                    contentDescription = "App Setting",
                    icon = Icons.Default.SettingsApplications,
                    text = "App Setting" // Налаштування застосунку / Настройки приложения
                )

                Spacer(Modifier.height(4.dp))

                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.Favorite.route,
                    contentDescription = "Favorite content",
                    icon = Icons.Default.Favorite,
                    text = "Favorite content"
                )
                Spacer(Modifier.height(4.dp))

                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.Help.route,
                    contentDescription = "FAQ, feedback, contacts",
                    icon = Icons.AutoMirrored.Filled.Help,
                    text = "FAQ, feedback, contacts" // change language
                )
                Spacer(Modifier.height(4.dp))

                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.ChangePassword.route,
                    contentDescription = "Change Password",
                    icon = Icons.Default.Password,
                    text = "Change Password" // change language
                )
                Spacer(Modifier.height(4.dp))

                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.DeleteAccount.route,
                    contentDescription = "Delete account",
                    icon = Icons.Default.Delete,
                    text = "Delete account"
                )

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = {
                        authViewModel.signout()
                        profileViewModel.clearProfileData()
                    },
                    colors = ButtonDefaults.buttonColors(AccentRed)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Sign Out",
                            tint = White,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.height(18.dp))

                        Text(
                            text = "Sign Out",
                            fontFamily = Nunito,
                            color = White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

            }

        }
    }
}

@Composable
fun ProfileInfoBlock(
    photoUrl: String?,
    isImageUploading: Boolean = false,
    onAddClick: () -> Unit,
    name: String,
    userName: String,
    email: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFFE49A), Color(0xFFFFC94D))
                        )
                    )
                    .padding(vertical = 18.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // avatar
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color.White)
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

                        if (!isImageUploading) {
                            Box( // TODO: to solve problem with location "+" button
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

                    Spacer(Modifier.height(12.dp))

                    // text for name
                    Text(
                        text = name.ifEmpty { "???" },
                        fontFamily = Nunito,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(2.dp))

                    // text for username
                    Text(
                        text = userName,
                        fontFamily = Nunito,
                        fontSize = 12.sp,
                        color = Black.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.White.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {

                        // text for email
                        Text(
                            text = email,
                            fontFamily = Nunito,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileBoxButton(
    navController: NavController,
    route: String,
    contentDescription: String,
    icon: ImageVector,
    text: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(route) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF2A1044), Color(0xFF800E9C)) // change colors
                        )
                    )
                    .padding(16.dp),
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        tint = CardYellow,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = text, // Про користувача / Про пользователя
                        fontFamily = Nunito,
                        color = CardYellow,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}



