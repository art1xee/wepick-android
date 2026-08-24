package com.example.wepick.screens.profile_screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.screens.profile_screens.components.ActionConfirmDialog
import com.example.wepick.screens.profile_screens.components.DangerZoneButton
import com.example.wepick.screens.profile_screens.components.ProfileBoxButton
import com.example.wepick.screens.profile_screens.components.ProfileInfoBlock
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.AuthState
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.profile_view_model.ProfileSetupViewModel

@Composable
fun ProfileSettingScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileSetupViewModel
) {
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()

    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }


    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> {
                navController.navigate(ScreenNav.Login.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
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
                //TODO: add this text block on the components app level
                Text(
                    text = stringResource(R.string.profile_setting_label),
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
                    photoUrl = uiState.photoUrl,
                    name = uiState.name,
                    userName = uiState.userName,
                    onClick = { navController.navigate(ScreenNav.ProfileEdit.route) }
                )

                Spacer(Modifier.height(10.dp))

                // Profile box button with personal data of user: name, username, date of birth, email, bio etc. (user can change the values in this screen)
                ProfileBoxButton(
                    onClick = { navController.navigate(ScreenNav.PersonalData.route) },
                    contentDescription = stringResource(R.string.profile_setting_personal_data_title),
                    icon = Icons.Default.Person,
                    text = stringResource(R.string.profile_setting_personal_data_title), // Про користувача / Про пользователя
                    subtext = stringResource(R.string.profile_setting_personal_data_subtitle),
                    firstStartColor = Color(0xFF613477),
                    secondStartColor = Color(0xFF2A1044),
                    firstEndColor = Color(0xFF9B4DB3),
                    secondEndColor = Color(0xFF800E9C),
                )

                Spacer(Modifier.height(4.dp))

                // Profile box button with app settings: Get email msg, Change app language, make user profile private/non-private, push-msg from app in user phone
                ProfileBoxButton(
                    onClick = { navController.navigate(ScreenNav.AppSetting.route) },
                    contentDescription = stringResource(R.string.profile_setting_title),
                    icon = Icons.Default.SettingsApplications,
                    text = stringResource(R.string.profile_setting_title),// Налаштування застосунку / Настройки приложения
                    subtext = stringResource(R.string.profile_setting_subtitle),
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
                    onClick = { navController.navigate(ScreenNav.Help.route) },
                    contentDescription = stringResource(R.string.profile_setting_help_title),
                    icon = Icons.AutoMirrored.Filled.Help,
                    text = stringResource(R.string.profile_setting_help_title),// change language
                    subtext = stringResource(R.string.profile_setting_help_subtitle),
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
                    onClick = { navController.navigate(ScreenNav.ChangePassword.route) },
                    contentDescription = stringResource(R.string.profile_setting_change_password_title),
                    icon = Icons.Default.Shield,
                    text = stringResource(R.string.profile_setting_change_password_title),// change language
                    subtext = stringResource(R.string.profile_setting_change_password_subtitle),
                    //start color of the block
                    firstStartColor = Color(0xFFB45309),
                    secondStartColor = Color(0xFF78350F),
                    //start color of the block (when user pressed the button)
                    firstEndColor = Color(0xFFF87171),
                    secondEndColor = Color(0xFFDC2626),
                )

                Spacer(Modifier.height(18.dp))

                DangerZoneButton(
                    icon = Icons.Default.Delete,
                    text = stringResource(R.string.profile_setting_delete_account),
                    textColor = White,
                    onClick = { showDeleteDialog = true },
                    borderColor = AccentRed,
                    containerColor = AccentRed,
                )

                if (showDeleteDialog) {
                    ActionConfirmDialog(
                        title = stringResource(R.string.profile_setting_delete_account),
                        text = stringResource(R.string.profile_setting_delete_account_alert),
                        confirmText = stringResource(R.string.profile_setting_delete_account_confirm),
                        isDestructive = true,
                        onConfirm = {
                            showDeleteDialog = false
                            authViewModel.deleteAccount(
                                onSuccess = {
                                    profileViewModel.clearProfileData()
                                },
                                onError = {
                                    Log.e("DeletingAccountError", "Deleting error: ${it.message}")
                                }
                            )
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                }

                Spacer(Modifier.height(8.dp))

                DangerZoneButton(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    text = stringResource(R.string.profile_setting_sign_out),
                    onClick = { showSignOutDialog = true },
                    borderColor = AccentRed,
                )
                if (showSignOutDialog) {
                    ActionConfirmDialog(
                        title = stringResource(R.string.profile_setting_sign_out),
                        text = stringResource(R.string.profile_setting_sign_out_alert),
                        confirmText = stringResource(R.string.profile_setting_sign_out_confirm),
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
