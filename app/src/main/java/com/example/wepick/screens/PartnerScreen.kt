package com.example.wepick.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wepick.MainViewModel
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.PrimaryPurple


@Composable
fun PartnerScreen(navController: NavController, viewModel: MainViewModel) {
//    val type = viewModel.partnerType.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp)
    ) {
        Row {
            PartnerPick(navController)
        }

    }
}



@Composable
fun PartnerPick(navController: NavController) {
    Button(
        onClick = {
            navController.navigate(ScreenNav.CharacterPicker.route)
        }
    ) {
        Text(
            text = stringResource(R.string.popular_character_partner),
        )
    }
    Button(
        onClick = {
            navController.navigate(ScreenNav.FriendName.route)
        }
    ) {

        Text(stringResource(R.string.friend_partner))
    }
}