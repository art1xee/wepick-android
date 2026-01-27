package com.example.wepick.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wepick.GenresData
import com.example.wepick.MainViewModel
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal

@Composable
fun GenresScreen(navController: NavController, viewModel: MainViewModel,modifier:Modifier) {
    val lang = "ru"
    val option = if (viewModel.currentStep == "dislikes") {
        GenresData.GENRES[lang]?.take(16) ?: emptyList()
    } else {
        GenresData.GENRES[lang] ?: emptyList()
    }
    val dislikesStep = viewModel.currentStep == "dislikes"
    Box(
        modifier
            .fillMaxSize()
            .background(PrimaryPurple),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = CardYellow),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        if (dislikesStep) R.string.genres_dislikes_choose else R.string.genres_likes_choose,
                        viewModel.userName.value
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = PressStart2P,
//                    color =
                    textAlign = TextAlign.Center,
                    modifier = modifier.padding(bottom = 20.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(option) { genre ->
                        val isSelected = if (dislikesStep)
                            viewModel.selectedDislikes.contains(genre)
                        else
                            viewModel.selectedLikes.contains(genre)

                        GenreChip(genre, isSelected) {
                            if (dislikesStep) viewModel.toggleDislike(genre)
                            else viewModel.toggleLikes(genre)
                        }
                    }
                }
                Button(
                    onClick = {
                        if (dislikesStep) viewModel.currentStep = "likes"
                        else navController.navigate(ScreenNav.Summary.route)
                    },
                    enabled = if (dislikesStep)
                        viewModel.selectedDislikes.size == 3
                    else
                        viewModel.selectedLikes.size == 3,
                    modifier = modifier
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text(
                        text = stringResource(R.string.find_content),
                        fontFamily = PressStart2P,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun GenreChip(
    genre: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (isSelected) AccentRed else TextTeal
    val borderColor = if (isSelected) AccentRed else Color.Transparent
    Card(
        modifier = Modifier
            .padding(4.dp)
            .height(50.dp)
            .clickable { onClick() }
            .border(
                1.dp, borderColor, MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = genre,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = PressStart2P,
                color = contentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp),
                maxLines = 4,
            )
        }
    }
}