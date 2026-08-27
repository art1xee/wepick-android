package com.example.wepick.screens.profile_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.PlayerViewModel

@Composable
fun FavoriteContentScreen(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    playerVM: PlayerViewModel,
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Favorite"
        )
    }
}