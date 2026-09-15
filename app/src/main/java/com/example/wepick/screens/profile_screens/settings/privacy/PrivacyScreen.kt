package com.example.wepick.screens.profile_screens.settings.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.wepick.R
import com.example.wepick.screens.profile_screens.components.BackButton
import com.example.wepick.screens.profile_screens.components.LabelText
import com.example.wepick.screens.profile_screens.components.ToggleSwitcher
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.viewmodel.profile_view_model.ProfileSettingViewModel

@Composable
fun PrivacyScreen(
    navController: NavController,
    profileSettingViewModel: ProfileSettingViewModel
) {
    val uiState by profileSettingViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
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

                LabelText("Privacy") // TODO: ADD IN R.STRING

                Spacer(Modifier.height(8.dp))
                ToggleSwitcher(
                    text = stringResource(R.string.settings_privacy),
                    checked = uiState.userProfile?.isPrivate ?: false,
                    onCheckedChanged = { onChecked ->
                        profileSettingViewModel.onPrivacyChanged(onChecked)
                    }
                )
            }
        }
    }
}
