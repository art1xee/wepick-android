package com.example.wepick.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wepick.MainViewModel
import com.example.wepick.NextButton
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal

@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel, modifier: Modifier) {
    // создаём переменную, которая будет хранить имя пользователя при регестрации
    var name by remember { mutableStateOf("") }

    /// the button will active if name contains more than 2 symbols
    val isNameValid = name.trim().isNotEmpty()

    Column(
        modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardYellow)
        ) {

            Column(
                modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = stringResource(id = R.string.welcome_main),
                    color = TextTeal,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = PressStart2P,
                ) // greeting text

                Spacer(modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            text = stringResource(R.string.enter_name_main),
                            fontFamily = PressStart2P
                        )
                    },
                    modifier = modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleLarge,
//                    colors = TextFieldDefaults.colors(
//                        cursorColor = Black
//
//                    )
                )
                Spacer(modifier.height(24.dp))
                NextButton(
                    navController = navController,
                    modifier = modifier,
                    route = ScreenNav.Selection.route,
                    enabled = isNameValid,
                    onNextClick = {
                        viewModel.setUserName(name)
                    })
            }
        }
    }
}