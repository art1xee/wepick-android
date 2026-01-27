package com.example.wepick.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wepick.MainViewModel
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.characterList
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.ui.theme.White

@Composable
fun CharacterPickerScreen(navController: NavController, viewModel: MainViewModel) {
    // save id chosen character (null if no one is chosen by user)
    var selectedCharId by remember { mutableStateOf<String?>(null) }
    val selectedChar = characterList.find { it.id == selectedCharId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardYellow)
        ) {
            Text(
                text = stringResource(R.string.popular_character_label),
                fontFamily = PressStart2P,
                color = White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(characterList) { char ->
                    val isSelected = char.id == selectedCharId
                    CharacterCard(
                        char.name,
                        char.imageRes,
                        isSelected = isSelected,
                        onClick = {
                            selectedCharId = char.id
                        },
                    )
                }
            }
            Button(
                onClick = {
                    selectedCharId?.let {
                        viewModel.setPartner("character", it)
                        navController.navigate(ScreenNav.Genres.route)
                    }
                },
                enabled = selectedChar != null,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.next_button))
            }
        }

    }
}

@Composable
fun CharacterCard(name: String, imageRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    val contentColor = if (isSelected) AccentRed else Color.Black
    val borderColor = if (isSelected) AccentRed else Color.Transparent
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable { (onClick()) }
            .border(1.dp, borderColor, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontFamily = PressStart2P,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}