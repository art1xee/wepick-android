package com.example.wepick.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.MainViewModel
import com.example.wepick.NextButton
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.characterList
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal

@Composable
fun CharacterPickerScreen(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier
) {
    // save id chosen character (null if no one is chosen by user)
    var selectedCharId by remember { mutableStateOf<String?>(null) }
    val selectedChar = characterList.find { it.id == selectedCharId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier.height(40.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardYellow)
        ) {
            Column(
                modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.popular_character_label),
                    fontFamily = PressStart2P,
                    color = TextTeal,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center,
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
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
                Spacer(modifier.height(8.dp))
                NextButton(
                    navController = navController,
                    modifier = Modifier,
                    route = ScreenNav.Genres.route,
                    enabled = selectedChar != null,
                    onNextClick = {
                        selectedChar?.let { char ->
                            viewModel.setPartnerType("character") // Исправлено
                            viewModel.selectCharacter(char.name)
                            viewModel.generateCharacterFullProfile()
                            viewModel.prepareForGenres()
                            navController.navigate(ScreenNav.Genres.route)
                        }
                    }
                )
            }
        }

    }
}

@Composable
fun CharacterCard(
    name: String,
    imageRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) AccentRed else Color.Black

    Card(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = contentColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape),
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        lineHeight = 11.sp
                    ),
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    fontFamily = PressStart2P,
                    softWrap = true,
                    maxLines = 3
                )
            }
        }
    }
}
