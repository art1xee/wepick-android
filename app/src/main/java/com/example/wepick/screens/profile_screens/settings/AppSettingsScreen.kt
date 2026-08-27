package com.example.wepick.screens.profile_screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.PlayerViewModel
import com.example.wepick.viewmodel.profile_view_model.ProfileSetupViewModel

@Composable
fun AppSettingScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    playerVM: PlayerViewModel,
    profileViewModel: ProfileSetupViewModel
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "App Setting"
        )
    }
}

