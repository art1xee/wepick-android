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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wepick.ContentType
import com.example.wepick.ContentTypeButton
import com.example.wepick.MainViewModel
import com.example.wepick.NextButton
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.Anime
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.Movie
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.Series
import com.example.wepick.ui.theme.TextTeal


@Composable
fun SelectionScreen(navController: NavController, viewModel: MainViewModel, modifier: Modifier) {
    val selectedType by viewModel.selectedContentType
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
                    text = stringResource(R.string.selection_label, viewModel.userName.value),
                    fontFamily = PressStart2P,
                    color = TextTeal,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    ContentTypeButton( // content button for movies
                        type = ContentType.Movie,
                        isSelected = (selectedType == ContentType.Movie),
                        onClick = { viewModel.setContentType(ContentType.Movie) },
                        modifier = modifier,
                        activeColor = Movie,
                        text = stringResource(R.string.movie_content)
                    )
                    Spacer(modifier.height(12.dp))

                    ContentTypeButton( // content button for tv shows/series
                        type = ContentType.Tv,
                        isSelected = (selectedType == ContentType.Tv),
                        onClick = { viewModel.setContentType(ContentType.Tv) },
                        modifier = modifier,
                        activeColor = Series,
                        text = stringResource(R.string.series_content)
                    )
                    Spacer(modifier.height(12.dp))
                    ContentTypeButton( // content button for anime/asian content
                        type = ContentType.Anime,
                        isSelected = (selectedType == ContentType.Anime),
                        onClick = { viewModel.setContentType(ContentType.Anime) },
                        modifier = modifier,
                        activeColor = Anime,
                        text = stringResource(R.string.asian_content)
                    )
                }
                Spacer(modifier.height(24.dp))
                NextButton(
                    navController = navController,
                    modifier = modifier,
                    route = ScreenNav.Partner.route,
                    enabled = selectedType != null,
                    onNextClick = {
                        selectedType?.let {
                            viewModel.setContentType(it)
                        }
                    }
                )
            }

        }
    }
}


