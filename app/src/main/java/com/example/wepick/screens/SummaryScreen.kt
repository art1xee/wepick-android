package com.example.wepick.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.wepick.MainViewModel
import com.example.wepick.R
import com.example.wepick.ScreenNav

@Composable
fun SummaryScreen(navController: NavController, viewModel: MainViewModel){
    Text("the summary screen")
    Button(
        onClick = {
            navController.navigate(ScreenNav.Match.route)
        }
    ) {
        Text(stringResource(R.string.find_content))
    }
}
