package com.example.wepick.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.wepick.MainViewModel
import com.example.wepick.ScreenNav


@Composable
fun MatchScreen(navController: NavController, viewModel: MainViewModel,modifier:Modifier) {
    Text("the content!")
    Button(
        onClick = {
            navController.navigate(ScreenNav.Main.route)
        }
    ) {
        Text("start over")
    }
}