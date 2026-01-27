package com.example.wepick.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.wepick.MainViewModel
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.Gray
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White

@Composable
fun FriendNameScreen(navController: NavController, viewModel: MainViewModel) {
    var friendName by remember { mutableStateOf("") }
    val isFriendNameValid = friendName.trim().isNotEmpty()

    Column {
        Text(stringResource(R.string.friend_name_label))
        OutlinedTextField(
            value = friendName,
            onValueChange = { friendName = it },
            label = {
                Text(
                    text = stringResource(R.string.enter_name_friend_text_field),
                    fontFamily = PressStart2P
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleLarge,
        )
        Button(
            onClick = { navController.navigate(ScreenNav.Genres.route) },
            enabled = isFriendNameValid,
            colors = ButtonDefaults.buttonColors(
                contentColor = White,
                containerColor = AccentRed,
                disabledContentColor = Black,
                disabledContainerColor = Gray,
            )
        ) {
            Text(stringResource(R.string.next_button))
        }
    }
}