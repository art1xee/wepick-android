package com.example.wepick.screens

import android.widget.Space
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
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
            colors = CardDefaults.cardColors(CardYellow),
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

                Spacer(modifier = Modifier.height(20.dp))

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

                    Spacer(Modifier.height(16.dp))

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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.rating) + " ",
                            fontFamily = PressStart2P,
                            fontSize = 10.sp,
                            color = Black,
                        )
                        Text(
                            text = "⭐ ${currentItem.rating}/10",
                            fontFamily = PressStart2P,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            color = Black
                        )
                    }

                    Text(
                        text = "${currentIndex + 1} / ${matchedItems.size}",
                        fontFamily = PressStart2P,
                        color = Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CustomMatchButton(
                            text = stringResource(R.string.seen_content),
                            color = AccentRed,
                            onClick = {
                                if (currentIndex < matchedItems.size - 1) currentIndex++
                            }
                        )
                        CustomMatchButton( // reload search button //TODO
                            text = stringResource(R.string.reload_content),
                            color = AccentRed,
                            onClick = {
                                viewModel.processMatches(navController)
                            }
                        )
                        CustomMatchButton( // start over button
                            text = stringResource(R.string.start_again),
                            color = ButtonResetBg,
                            onClick = {
                                navController.navigate(ScreenNav.Main.route) {
                                    popUpTo(0)
                                }
                            }
                        )
                    }
                } else { // Теперь ELSE на своем месте (вне блока if с предметами)
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
