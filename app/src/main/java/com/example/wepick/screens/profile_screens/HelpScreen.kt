package com.example.wepick.screens.profile_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.PlayerViewModel
import com.example.wepick.viewmodel.profile_view_model.ProfileSetupViewModel

@Composable
fun HelpScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    playerVM: PlayerViewModel,
    profileViewModel: ProfileSetupViewModel
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FAQ, contacts, feedback"
        )
    }
}