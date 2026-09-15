package com.example.wepick.screens.profile_screens.settings

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.wepick.R
import com.example.wepick.screens.profile_screens.components.BackButton
import com.example.wepick.screens.profile_screens.components.LabelText
import com.example.wepick.screens.profile_screens.components.ToggleSwitcher
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.Muted
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White
import com.example.wepick.util.Language
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.profile_view_model.ProfileSettingViewModel

@Composable
fun AppSettingScreen(
    navController: NavController,
    viewModel: MainViewModel,
    profileSettingViewModel: ProfileSettingViewModel,
    modifier: Modifier
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage

    val uiState by profileSettingViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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
                BackButton(
                    navController
                )

                LabelText(stringResource(R.string.profile_setting_title))

                Spacer(Modifier.height(8.dp))

                //LANGUAGE BLOCK
                Text(
                    text = stringResource(R.string.settings_language),
                    fontFamily = PressStart2P,
                    fontSize = 9.sp,
                    color = Muted
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val supportedLanguages =
                        listOf("EN" to Language.EN, "UA" to Language.UK, "RU" to Language.RU)
                    supportedLanguages.forEach { (label, code) ->
                        val isSelected = currentLang == code
                        Box(
                            modifier = Modifier
                                .size(width = 50.dp, height = 30.dp)
                                .background(
                                    if (isSelected) AccentRed else White,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(2.dp, Black, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (!isSelected) viewModel.setLanguage(
                                        code, context
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontFamily = PressStart2P,
                                fontSize = 10.sp,
                                color = if (isSelected) White else Black
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                ToggleSwitcher(
                    text = stringResource(R.string.settings_privacy),
                    checked = uiState.userProfile?.isPrivate ?: false,
                    onCheckedChanged = { onChecked ->
                        profileSettingViewModel.onPrivacyChanged(onChecked)
                    }
                )
                Spacer(Modifier.height(8.dp))
                ToggleSwitcher(
                    text = stringResource(R.string.settings_push),
                    checked = uiState.userProfile?.pushEnabled ?: false,
                    onCheckedChanged = { onChecked ->
                        profileSettingViewModel.onPushEnabledChanged(onChecked)
                    }
                )
                Spacer(Modifier.height(8.dp))
                ToggleSwitcher(
                    text = stringResource(R.string.settings_email),
                    checked = uiState.userProfile?.emailEnabled ?: false,
                    onCheckedChanged = { onChecked ->
                        profileSettingViewModel.onEmailEnabledChanged(onChecked)
                    }
                )
            }
        }
    }
}
