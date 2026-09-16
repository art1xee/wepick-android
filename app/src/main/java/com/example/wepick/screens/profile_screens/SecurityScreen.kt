package com.example.wepick.screens.profile_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.wepick.R
import com.example.wepick.screens.profile_screens.components.BackButton
import com.example.wepick.screens.profile_screens.components.LabelText
import com.example.wepick.screens.profile_screens.components.ProfileBoxButton
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.viewmodel.profile_view_model.ProfileSettingViewModel

@Composable
fun SecurityScreen(
    navController: NavHostController,
    profileSettingViewModel: ProfileSettingViewModel,
) {
    val uiState by profileSettingViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardYellow),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BackButton(navController)

                LabelText("SECURITY")

                Spacer(Modifier.height(16.dp))

                // Password reset via Firebase email, hidden for Google-auth users.
                // Add more security items here in the future (e.g. sessions, 2FA).
                if (uiState.isGoogleAuth) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.security_google_auth_note),
                            fontFamily = Nunito,
                            fontSize = 13.sp,
                            color = Color.Black.copy(alpha = 0.8f),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                } else {
                    ProfileBoxButton(
                        onClick = { profileSettingViewModel.sendPasswordResetEmail() },
                        contentDescription = stringResource(R.string.profile_setting_change_password_title),
                        icon = Icons.Default.Shield,
                        text = stringResource(R.string.profile_setting_change_password_title),
                        subtext = if (uiState.isPasswordResetSent)
                            stringResource(R.string.security_reset_email_sent)
                        else
                            stringResource(R.string.profile_setting_change_password_subtitle),
                        firstStartColor = Color(0xFFB45309),
                        secondStartColor = Color(0xFF78350F),
                        firstEndColor = Color(0xFFF87171),
                        secondEndColor = Color(0xFFDC2626),
                    )
                }
            }
        }
    }
}
