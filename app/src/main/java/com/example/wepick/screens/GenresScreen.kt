package com.example.wepick.screens

import android.R.attr.onClick
import androidx.annotation.experimental.Experimental
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.GenresData
import com.example.wepick.MainViewModel
import com.example.wepick.NextButton
import com.example.wepick.R
import com.example.wepick.SaveGenres
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.ButtonResetBg
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.Muted
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.ui.theme.White
import com.example.wepick.ui.theme.borderDislikes
import com.example.wepick.ui.theme.borderLikes
import com.example.wepick.ui.theme.contentDislikes
import com.example.wepick.ui.theme.contentLikes
import org.w3c.dom.Text


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenresScreen(navController: NavController, viewModel: MainViewModel, modifier: Modifier) {
    val lang = "ru"
    val dislikesStep = viewModel.currentStep == "dislikes"
    val genreList = GenresData.GENRES[lang]?.take(16) ?: emptyList()
    val lockedMessage = stringResource(R.string.error_genre)


    val currentPlayerName =
        if (viewModel.activePlayer == 1) viewModel.userName.value.ifEmpty { "Player 1" } else viewModel.friendName.value.ifEmpty { "Player 2" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        if (viewModel.isPartnerFriend) {
            Text(
                text = "${
                    stringResource(
                        R.string.player,
                        viewModel.activePlayer
                    )
                }$currentPlayerName",
                fontFamily = PressStart2P,
                fontSize = 10.sp,
                color = AccentRed,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(0.98f),
            colors = CardDefaults.cardColors(containerColor = CardYellow)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        if (dislikesStep) R.string.genres_dislikes_choose else R.string.genres_likes_choose,
                        currentPlayerName
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = PressStart2P,
                    color = TextTeal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                AnimatedVisibility( // error message for users when they trying choose already chosen genres
                    visible = viewModel.errorMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkOut()
                ) {
                    Text(
                        text = viewModel.errorMessage ?: "",
                        color = AccentRed,
                        fontFamily = PressStart2P,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        genreList.forEach { genre ->
                            val listToCompare =
                                if (viewModel.activePlayer == 1) viewModel.selectedDislikes else viewModel.selectedDislikesFriend
                            val isAlreadyInDislikes = !dislikesStep && listToCompare.contains(genre)

                            val isSelected = if (dislikesStep) {
                                if (viewModel.activePlayer == 1) viewModel.selectedDislikes.contains(
                                    genre
                                ) else viewModel.selectedDislikesFriend.contains(genre)
                            } else {
                                if (viewModel.activePlayer == 1) viewModel.selectedLikes.contains(
                                    genre
                                ) else viewModel.selectedLikesFriend.contains(genre)
                            }

                            GenreChip(
                                genre = genre,
                                isSelected = isSelected,
                                isLocked = isAlreadyInDislikes,
                                isDislikeStep = dislikesStep,
                                onClick = {
                                    if (isAlreadyInDislikes) {
                                        viewModel.showLockedError(lockedMessage)
                                    } else {
                                        viewModel.toggleGenre(genre, isDislike = dislikesStep)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!dislikesStep) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut(),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                                .border(1.dp, Black, MaterialTheme.shapes.medium),
                            colors = CardDefaults.cardColors(ButtonResetBg)
                        ) {
                            DecadePicker(viewModel)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                SaveGenres(
                    navController = navController,
                    modifier = Modifier,
                    route = if (!dislikesStep && (viewModel.activePlayer == 2 || !viewModel.isPartnerFriend)) {
                        ScreenNav.Summary.route
                    } else {
                        ""
                    },
                    enabled = (if (dislikesStep) {
                        if (viewModel.activePlayer == 1) viewModel.selectedDislikes.size else viewModel.selectedDislikesFriend.size
                    } else {
                        if (viewModel.activePlayer == 1) viewModel.selectedLikes.size else viewModel.selectedLikesFriend.size
                    }) == 3,
                    onNextClick = {

                        if (dislikesStep) {
                            viewModel.currentStep = "likes"
                        } else {
                            if (viewModel.isPartnerFriend && viewModel.activePlayer == 1) {
                                viewModel.activePlayer = 2
                                viewModel.currentStep = "dislikes"
                            } else {
                                navController.navigate(ScreenNav.Summary.route)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DecadePicker(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentDecade =
        if (viewModel.activePlayer == 1) viewModel.selectedDecade else viewModel.selectedDecadeFriend

    val currentPlayerName =
        if (viewModel.activePlayer == 1) viewModel.userName.value.ifEmpty { "Player 1" } else viewModel.friendName.value.ifEmpty { "Player 2" }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.decade_choose, currentPlayerName),
            fontFamily = PressStart2P,
            fontSize = 10.sp,
            color = TextTeal,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(
                text = "<",
                fontFamily = PressStart2P,
                fontSize = 18.sp,
                modifier = Modifier
                    .clickable { viewModel.prevDecade() }
                    .padding(12.dp),
                color = AccentRed,
            )

            Box(
                modifier = Modifier
                    .background(White, RoundedCornerShape(4.dp))
                    .border(2.dp, Black, RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${currentDecade}s",
                    fontFamily = PressStart2P,
                    fontSize = 14.sp,
                    color = Black
                )
            }

            Text(
                text = ">",
                fontFamily = PressStart2P,
                fontSize = 18.sp,
                modifier = Modifier
                    .clickable { viewModel.nextDecade() }
                    .padding(12.dp),
                color = AccentRed,
            )
        }
    }
}

@Composable
fun GenreChip(
    genre: String,
    isSelected: Boolean,
    isLocked: Boolean,
    isDislikeStep: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = when {
        isLocked -> Black
        isSelected -> if (isDislikeStep) contentDislikes else contentLikes
        else -> TextTeal
    }

    val borderColor = when {
        isLocked -> contentDislikes
        isSelected -> if (isDislikeStep) borderDislikes else borderLikes
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .wrapContentSize()
            .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(
                color = if (isLocked) Color.LightGray else Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                letterSpacing = 0.sp
            ),
            color = contentColor,
            fontFamily = PressStart2P,
            textAlign = TextAlign.Center
        )
    }
}

