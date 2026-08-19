package com.example.wepick.screens.profile_screens

import android.app.AlertDialog
import android.util.Log
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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

                //Profile info block with: avatar, name, username, email
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
                ) // TODO: when user save profile icon from "profile setting screen" the avatar doesn't save when app is reloaded

                Spacer(Modifier.height(10.dp))

                // Profile box button with personal data of user: name, username, date of birth, email, bio etc. (user can change the values in this screen)
                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.PersonalData.route,
                    contentDescription = "Personal Data",
                    icon = Icons.Default.Person,
                    text = "Personal Data", // Про користувача / Про пользователя
                    subtext = "Name, avatar, bio",
                    firstStartColor = Color(0xFF613477),
                    secondStartColor = Color(0xFF2A1044),
                    firstEndColor = Color(0xFF9B4DB3),
                    secondEndColor = Color(0xFF800E9C),
                )

                Spacer(Modifier.height(4.dp))

                // Profile box button with app settings: Get email msg, Change app language, make user profile private/non-private, push-msg from app in user phone
                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.AppSetting.route,
                    contentDescription = "App Setting",
                    icon = Icons.Default.SettingsApplications,
                    text = "App Setting",// Налаштування застосунку / Настройки приложения
                    subtext = "Change setting of the app",
                    //start color of the block
                    firstStartColor = Color(0xFF64748B),
                    secondStartColor = Color(0xFF1E293B),
                    //end color of the block (when user pressed the button)
                    firstEndColor = Color(0xFF94A3B8),
                    secondEndColor = Color(0xFF475569),
                )
                Spacer(Modifier.height(4.dp))

                //TODO: move this block "FAVORITE" in navigation bar or another place of the app
//                // Profile Box button with: Favorite content
//                ProfileBoxButton(
//                    navController = navController,
//                    route = ScreenNav.Favorite.route,
//                    contentDescription = "Favorite content",
//                    icon = Icons.Default.Favorite,
//                    text = "Favorite content"
//                )

                Spacer(Modifier.height(4.dp))

                // Profile box button with: FAQ, feedback, contacts
                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.Help.route,
                    contentDescription = "Help",
                    icon = Icons.AutoMirrored.Filled.Help,
                    text = "Help",// change language
                    subtext = "FAQ, feedback, contacts",
                    //start color of the block
                    firstStartColor = Color(0xFF3B82F6),
                    secondStartColor = Color(0xFF1E40AF),
                    //start color of the block (when user pressed the button)
                    firstEndColor = Color(0xFF67B7E8),
                    secondEndColor = Color(0xFF0891B2)
                )

                Spacer(Modifier.height(4.dp))

                // Profile box button with: Changing the password of the user account
                ProfileBoxButton(
                    navController = navController,
                    route = ScreenNav.ChangePassword.route,
                    contentDescription = "Change Password",
                    icon = Icons.Default.Shield,
                    text = "Change Password",// change language
                    subtext = "Change password of the account",
                    //start color of the block
                    firstStartColor = Color(0xFFB45309),
                    secondStartColor = Color(0xFF78350F),
                    //start color of the block (when user pressed the button)
                    firstEndColor = Color(0xFFF87171),
                    secondEndColor = Color(0xFFDC2626),
                )

                Spacer(Modifier.height(4.dp))

                DangerZoneButton(
                    icon = Icons.Default.Delete,
                    text = "Delete account",
                    textColor = White,
                    onClick = { showDeleteDialog = true }
                )
                if (showDeleteDialog) {
                    ActionConfirmDialog(
                        title = "Delete account",
                        text = "This account is irreversible. All your data will be permanently deleted. Are you absolutely sure?",
                        confirmText = "Delete",
                        onConfirm = {
                            showDeleteDialog = false
                            authViewModel.deleteAccount(
                                onSuccess = {
                                    profileViewModel.clearProfileData()
                                    navController.navigate(ScreenNav.Login.route){
                                        popUpTo(0)
                                    }
                                },
                                onError = {
                                    Log.e("DeletingAccountError", "Something went wrong with deleting account")
                                }
                            )
                            profileViewModel.clearProfileData()
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                }


                Spacer(Modifier.height(4.dp))

                // The button which signout of the user account
//                Button(
//                    onClick = {
//                        authViewModel.signout()
//                        profileViewModel.clearProfileData()
//                    },
//                    colors = ButtonDefaults.buttonColors(AccentRed)
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Start
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBackIosNew,
//                            contentDescription = "Sign Out",
//                            tint = White,
//                            modifier = Modifier.size(24.dp),
//                        )
//                        Spacer(Modifier.height(18.dp))
//
//                        Text(
//                            text = "Sign Out",
//                            fontFamily = Nunito,
//                            color = White,
//                            fontSize = 14.sp,
//                            textAlign = TextAlign.End,
//                            fontWeight = FontWeight.Black
//                        )
//                    }
//                }

                DangerZoneButton(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    text = "Sign Out",
                    onClick = { showSignOutDialog = true }
                )
                if (showSignOutDialog) {
                    ActionConfirmDialog(
                        title = "Sign Out",
                        text = "Are you sure you want to sign out?",
                        confirmText = "Sign Out",
                        onConfirm = {
                            showSignOutDialog = false
                            authViewModel.signout()
                            profileViewModel.clearProfileData()
                        },
                        onDismiss = { showSignOutDialog = false }
                    )
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
                        text = "@$userName",
                        fontFamily = Nunito,
                        fontSize = 12.sp,
                        color = Black.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(6.dp))


                    /* EMAIL BLOCK: MOVE THIS TEXT (EMAIL ADRESS) IN PERSONAL DATA
                    *  Box(
                        modifier = Modifier
                            .background(
                                color = Color.White.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = email,
                            fontFamily = Nunito,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Black,
                            textAlign = TextAlign.Center
                        )
                    }
                    * */

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
    firstStartColor: Color,
    secondStartColor: Color,
    firstEndColor: Color,
    secondEndColor: Color,
    icon: ImageVector,
    text: String,
    subtext: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Движение карточки
    val offsetX by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        animationSpec = tween(150),
        label = "offset"
    )

    // Цвет карточки
    val startColor by animateColorAsState(
        targetValue = if (isPressed)
            firstStartColor
        else
            secondStartColor,
        animationSpec = tween(150),
        label = "startColor"
    )

    val endColor by animateColorAsState(
        targetValue = if (isPressed)
            firstEndColor
        else
            secondEndColor,
        animationSpec = tween(150),
        label = "endColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = offsetX)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                navController.navigate(route)
            }
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            startColor,
                            endColor
                        )
                    )
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Иконка слева
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = CardYellow,
                modifier = Modifier.size(24.dp)
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            // Название + описание
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    fontFamily = Nunito,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = subtext,
                        fontFamily = Nunito,
                        color = Color(0xFFBFA9C8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Стрелка справа
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFFBFA9C8),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun DangerZoneButton(
    icon: ImageVector,
    text: String,
    textColor: Color = AccentRed,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, textColor.copy(0.3f), shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(14.dp))

            Text(
                text = text,
                fontFamily = Nunito,
                fontSize = 14.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Composable
fun ActionConfirmDialog(
    title: String,
    text: String,
    confirmText: String,
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = text,
                fontFamily = Nunito
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = if (isDestructive) AccentRed else DarkButtonPurple,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = Color.Gray) // change the language
            }
        },
        containerColor = Color.White, // Или цвет твоего фона карточек
        shape = RoundedCornerShape(16.dp)
    )
}
