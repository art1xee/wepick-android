package com.example.wepick.screens.profile_screens.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.screens.profile_screens.components.BackButton
import com.example.wepick.screens.profile_screens.components.LabelText
import com.example.wepick.screens.profile_screens.components.ProfileBoxButton
import com.example.wepick.ui.theme.CardYellow

@Composable
fun AppSettingScreen(
    navController: NavController,
    modifier: Modifier
) {

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

                ProfileBoxButton(
                    onClick = { navController.navigate(ScreenNav.Notification.route) },
                    contentDescription = "Notification block",
                    //start color of the block
                    firstStartColor = Color(0xFF60A5FA),
                    firstEndColor = Color(0xFF1D4ED8),
                    //end color of the block (when user pressed the button)
                    secondStartColor = Color(0xFF3B82F6),
                    secondEndColor = Color(0xFF1E40AF),
                    icon = Icons.Default.Notifications,
                    text = "Notification",
                    subtext = "Setting your notifications"
                )
                Spacer(Modifier.height(4.dp))
                ProfileBoxButton(
                    onClick = { navController.navigate(ScreenNav.Privacy.route) },
                    contentDescription = "Privacy block",
                    //start color of the block
                    firstStartColor = Color(0xFFA78BFA),
                    firstEndColor = Color(0xFF6D28D9),
                    //end color of the block (when user pressed the button)
                    secondStartColor = Color(0xFF8B5CF6),
                    secondEndColor = Color(0xFF5B21B6),
                    icon = Icons.Default.Shield,
                    text = "Privacy",
                    subtext = "Setting your privacy"
                )
                Spacer(Modifier.height(4.dp))
                ProfileBoxButton(
                    onClick = { navController.navigate(ScreenNav.LanguagePicker.route) },
                    contentDescription = "Language block",
                    //start color of the block
                    firstStartColor = Color(0xFF2DD4BF),
                    firstEndColor = Color(0xFF0F766E),
                    //end color of the block (when user pressed the button)
                    secondStartColor = Color(0xFF14B8A6),
                    secondEndColor = Color(0xFF115E59),
                    icon = Icons.Default.Language,
                    text = "Language",
                    subtext = "Setting your language"
                )

            }
        }
    }
}
