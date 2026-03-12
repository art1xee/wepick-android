package com.example.wepick.screens

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.data.local.GenresData
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.R
import com.example.wepick.ui.components.SaveGenres
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.ButtonResetBg
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.ui.theme.White
import com.example.wepick.ui.theme.borderDislikes
import com.example.wepick.ui.theme.borderLikes
import com.example.wepick.ui.theme.contentDislikes
import com.example.wepick.ui.theme.contentLikes
import com.example.wepick.util.GenreStep
import com.example.wepick.util.Language
import com.example.wepick.util.Players
import com.example.wepick.viewmodel.PlayerViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenresScreen(
    navController: NavController,
    viewModel: MainViewModel,
    playerVM: PlayerViewModel,
    modifier: Modifier
) {
    val local = LocalConfiguration.current.locales[0]
    val lang = when (local.language) {
        Language.UK -> Language.UA
        Language.RU -> Language.RU
        else -> Language.EN
    }
    val dislikesStep = playerVM.currentStep == GenreStep.DISLIKES
    val genreList = GenresData.GENRES[lang]?.take(16) ?: emptyList()
    val lockedMessage = stringResource(R.string.error_genre)


    val currentPlayerName =
        if (playerVM.activePlayer == 1) playerVM.userName.ifEmpty { Players.PLAYER_1 } else playerVM.friendName.ifEmpty { Players.PLAYER_2 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        if (playerVM.isPartnerFriend) {
            Text(
                text = "${
                    stringResource(
                        R.string.player,
                        playerVM.activePlayer
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
                    visible = playerVM.errorMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkOut()
                ) {
                    Text(
                        text = playerVM.errorMessage ?: "",
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
                                if (playerVM.activePlayer == 1) playerVM.selectedDislikes else playerVM.selectedDislikesFriend
                            val isAlreadyInDislikes = !dislikesStep && listToCompare.contains(genre)

                            val isSelected = if (dislikesStep) {
                                if (playerVM.activePlayer == 1) playerVM.selectedDislikes.contains(
                                    genre
                                ) else playerVM.selectedDislikesFriend.contains(genre)
                            } else {
                                if (playerVM.activePlayer == 1) playerVM.selectedLikes.contains(
                                    genre
                                ) else playerVM.selectedLikesFriend.contains(genre)
                            }

                            GenreChip(
                                genre = genre,
                                isSelected = isSelected,
                                isLocked = isAlreadyInDislikes,
                                isDislikeStep = dislikesStep,
                                onClick = {
                                    if (isAlreadyInDislikes) {
                                        playerVM.showLockedError(lockedMessage)
                                    } else {
                                        playerVM.toggleGenre(genre, isDislike = dislikesStep)
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
                            DecadePicker(playerVM = playerVM)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                SaveGenres(
                    navController = navController,
                    modifier = Modifier,
                    route = "",
                    enabled = (if (dislikesStep) {
                        if (playerVM.activePlayer == 1) playerVM.selectedDislikes.size else playerVM.selectedDislikesFriend.size
                    } else {
                        if (playerVM.activePlayer == 1) playerVM.selectedLikes.size else playerVM.selectedLikesFriend.size
                    }) == 3,
                    onNextClick = {
                        if (dislikesStep) {
                            playerVM.currentStep = GenreStep.LIKES
                        } else {
                            if (playerVM.isPartnerFriend && playerVM.activePlayer == 1) {
                                playerVM.activePlayer = 2
                                playerVM.currentStep = GenreStep.DISLIKES
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
    modifier: Modifier = Modifier,
    playerVM: PlayerViewModel,
) {
    val currentDecade =
        if (playerVM.activePlayer == 1) playerVM.selectedDecade else playerVM.selectedDecadeFriend

    val currentPlayerName =
        if (playerVM.activePlayer == 1) playerVM.userName.ifEmpty { Players.PLAYER_1 }
        else playerVM.friendName.ifEmpty { Players.PLAYER_2 }


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
                    .clickable { playerVM.prevDecade() }
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
                    .clickable { playerVM.nextDecade() }
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
