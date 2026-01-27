package com.example.wepick.screens

import com.example.wepick.R
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.wepick.MainViewModel
import com.example.wepick.ScreenNav


@Composable
fun SelectionScreen(navController: NavController, viewModel: MainViewModel) {
    Column {
        Text(text = stringResource(R.string.selection_label, viewModel.userName.value))
        Text(text = "тут будет выбор контента")
        Button(onClick = { navController.navigate(ScreenNav.Partner.route) }) {
            Text(stringResource(R.string.next_button))
        }
    }
}