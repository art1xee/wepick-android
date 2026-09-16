package com.example.wepick.screens.profile_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.wepick.R
import com.example.wepick.screens.profile_screens.components.BackButton
import com.example.wepick.screens.profile_screens.components.LabelText
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.TealSuccess
import com.example.wepick.ui.theme.White
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
                PasswordResetCard(
                    isGoogleAuth = uiState.isGoogleAuth,
                    isSent = uiState.isPasswordResetSent,
                    onSendClick = { profileSettingViewModel.sendPasswordResetEmail() }
                )
            }
        }
    }
}

@Composable
private fun PasswordResetCard(
    isGoogleAuth: Boolean,
    isSent: Boolean,
    onSendClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFE49A), Color(0xFFFFC94D))
                    )
                )
                .padding(vertical = 24.dp, horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = if (isSent) TealSuccess else DarkButtonPurple,
                        shape = if (isSent) CircleShape else RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSent) Icons.Rounded.Check else Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (isSent) White else CardYellow,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.profile_setting_change_password_title),
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarkButtonPurple,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (isGoogleAuth) stringResource(R.string.security_google_auth_note)
                else if (isSent) stringResource(R.string.security_reset_email_sent)
                else stringResource(R.string.profile_setting_change_password_subtitle),
                fontFamily = Nunito,
                fontSize = 13.sp,
                color = Black.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            if (!isGoogleAuth) {
                Spacer(Modifier.height(18.dp))

                SendResetButton(
                    text = stringResource(
                        if (isSent) R.string.security_resend_button
                        else R.string.security_send_reset_button
                    ),
                    onClick = onSendClick
                )
            }
        }
    }
}

@Composable
private fun SendResetButton(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isPressed) DarkButtonPurple.copy(alpha = 0.85f) else DarkButtonPurple)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = CardYellow
        )
    }
}
