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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wepick.CustomMatchButton
import com.example.wepick.MainViewModel
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.ButtonResetBg
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.ui.theme.White

@Composable
fun MatchScreen(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val matchedItems by viewModel.items
    var currentIndex by remember { mutableIntStateOf(0) }
    val endMessage = stringResource(R.string.content_end)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(CardYellow.copy()),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.movie_match_label).uppercase(),
                        fontFamily = PressStart2P,
                        color = White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                    )
                    Text(
                        text = stringResource(R.string.movie_match_label).uppercase(),
                        fontFamily = PressStart2P,
                        color = AccentRed,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (matchedItems.isNotEmpty()) {
                    val currentItem = matchedItems[currentIndex]

                    Text(
                        text = currentItem.title.uppercase(),
                        fontFamily = PressStart2P,
                        color = TextTeal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.heightIn(min = 40.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(8.dp)
                    ) {
                        AsyncImage(
                            model = currentItem.posterPath,
                            contentDescription = null,
                            modifier = Modifier
                                .height(300.dp)
                                .width(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.rating) + " ",
                            fontFamily = PressStart2P,
                            fontSize = 10.sp,
                            color = Black,
                        )
                        val formattedRating = "%.1f".format(currentItem.rating)
                        Text(
                            text = "⭐ ${formattedRating}/10",
                            fontFamily = PressStart2P,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            color = Black
                        )
                    }

                    AnimatedVisibility( // for showing error when user trying press next button but i`ts already 6/6
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
//                                .padding(bottom = 8.dp)
                                .fillMaxWidth()
                        )
                    }


                    Row( // next and prev buttons
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "<",
                            fontFamily = PressStart2P,
                            fontSize = 20.sp,
                            color = if (currentIndex > 0) AccentRed else Black.copy(alpha = 0.3f),
                            modifier = Modifier
                                .clickable {
                                    if (currentIndex > 0) {
                                        currentIndex--
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .background(White, RoundedCornerShape(4.dp))
                                .border(2.dp, Black, RoundedCornerShape(4.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${currentIndex + 1} / ${matchedItems.size}",
                                fontFamily = PressStart2P,
                                color = Black,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Text(
                            text = ">",
                            fontFamily = PressStart2P,
                            fontSize = 20.sp,
                            color = if (currentIndex < matchedItems.size - 1) AccentRed else Black.copy(alpha = 0.3f),
                            modifier = Modifier
                                .clickable {
                                    if (currentIndex < matchedItems.size - 1) {
                                        currentIndex++
                                    } else {
                                        viewModel.showLockedError(endMessage)
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }


                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CustomMatchButton( // reload search button
                            text = stringResource(R.string.reload_content),
                            color = AccentRed,
                            onClick = {
                                currentIndex = 0
                                viewModel.processMatches(navController)
                            }
                        )
                        CustomMatchButton( // start over button
                            text = stringResource(R.string.start_again),
                            color = ButtonResetBg,
                            onClick = {
                                viewModel.resetAllData()
                                navController.navigate(ScreenNav.Main.route) {
                                    popUpTo(0)
                                }
                            }
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_found_content),
                        fontFamily = PressStart2P,
                        color = Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            }
        }
    }
}
