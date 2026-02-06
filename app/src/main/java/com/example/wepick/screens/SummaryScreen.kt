package com.example.wepick.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.ContentType
import com.example.wepick.FindContentButton
import com.example.wepick.MainViewModel
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DecadeColor
import com.example.wepick.ui.theme.DislikeContentColor
import com.example.wepick.ui.theme.LikeContentColor
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.ui.theme.White

@Composable
fun SummaryScreen(navController: NavController, viewModel: MainViewModel, modifier: Modifier) {
    val selectedType by viewModel.selectedContentType
    val userName by viewModel.userName
    val friendName by viewModel.friendName
    val isFriend = viewModel.isPartnerFriend


    val contentDisplayName = when (selectedType) {
        ContentType.Movie -> stringResource(R.string.movie_content)
        ContentType.Tv -> stringResource(R.string.series_content)
        ContentType.Anime -> stringResource(R.string.asian_content)
        null -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(Modifier.height(60.dp))

        Card(
            modifier = Modifier.fillMaxWidth(0.98f),
            colors = CardDefaults.cardColors(containerColor = CardYellow),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.summary_selection).uppercase(),
                    fontFamily = PressStart2P,
                    color = TextTeal,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // type content (red color with white shadow)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .background(White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .border(2.dp, AccentRed, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${stringResource(R.string.content_type)} ",
                            fontFamily = PressStart2P,
                            fontSize = 12.sp,
                            color = TextTeal
                        )
                        Box {
                            Text(
                                text = contentDisplayName,
                                fontFamily = PressStart2P,
                                fontSize = 11.sp,
                                color = White,
                                modifier = Modifier.offset(x = 1.dp, y = 1.dp),
                                textAlign = TextAlign.Center,
                                softWrap = true,
                                lineHeight = 14.sp

                            )
                            Text(
                                text = contentDisplayName,
                                fontFamily = PressStart2P,
                                fontSize = 11.sp,
                                color = AccentRed,
                                softWrap = true,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // sections with users
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ParticipantColumn(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.user_name_summary),
                        name = userName,
                        dislikes = viewModel.selectedDislikes,
                        likes = viewModel.selectedLikes,
                        decade = viewModel.selectedDecade
                    )
                    ParticipantColumn(
                        modifier = Modifier.weight(1f),
                        label = if (isFriend) stringResource(R.string.second_user_name_summary) else stringResource(
                            R.string.character_name
                        ),
                        name = if (isFriend) friendName else viewModel.selectedCharacterName,
                        dislikes = viewModel.selectedDislikesFriend,
                        likes = viewModel.selectedLikesFriend,
                        decade = viewModel.selectedDecadeFriend
                    )
                }

                Spacer(Modifier.height(30.dp))

                FindContentButton(
                    navController = navController,
                    route = ScreenNav.Match.route,
                    text = stringResource(R.string.find_content, contentDisplayName),
                    enabled = true,
                    onNextClick = { viewModel.processMatches(navController) }
                )
            }
        }
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
fun ParticipantColumn(
    modifier: Modifier,
    label: String,
    name: String,
    dislikes: List<String>,
    likes: List<String>,
    decade: Int
) {
    Column(
        modifier = modifier
            .background(White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Box {
            Text(
                text = "$label $name",
                fontFamily = PressStart2P,
                fontSize = 10.sp,
                color = White,
                modifier = Modifier.offset(x = 1.dp, y = 1.dp),
                lineHeight = 12.sp,
            )
            // user name (red color with white shadow)
            Text(
                text = "$label $name",
                fontFamily = PressStart2P,
                fontSize = 10.sp,
                color = AccentRed,
                lineHeight = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

        // genres dislikes
        SummaryInfoBlock(
            label = stringResource(R.string.dislikes_watch),
            items = dislikes,
            color = DislikeContentColor.copy(0.5f),
        )

        Spacer(Modifier.height(12.dp))

        // genres likes
        SummaryInfoBlock(
            label = stringResource(R.string.likes_watch),
            items = likes,
            color = LikeContentColor.copy(0.5f),
        )

        Spacer(Modifier.height(12.dp))

        // decade block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DecadeColor, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                stringResource(R.string.decade),
                fontFamily = PressStart2P,
                fontSize = 7.sp,
                color = TextTeal,
                textAlign = TextAlign.Center
            )
            Text(
                text = decade.toString(),
                fontFamily = PressStart2P,
                fontSize = 10.sp,
                color = Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SummaryInfoBlock(label: String, items: List<String>, color: Color) {
    Column(
        modifier = Modifier
            .background(White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            fontFamily = PressStart2P,
            fontSize = 8.sp,
            color = TextTeal,
            modifier = Modifier.padding(bottom = 6.dp),
            textAlign = TextAlign.Start,
            lineHeight = 10.sp,
            softWrap = true
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(color.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            maxItemsInEachRow = Int.MAX_VALUE
        ) {
            if (items.isEmpty()) {
                Text("—", fontFamily = PressStart2P, fontSize = 8.sp, color = Black.copy(0.4f))
            } else {
                items.forEach { genre ->
                    val dynamicFontSize = when {
                        genre.length > 18 -> 6.sp
                        genre.length > 12 -> 7.sp
                        else -> 8.sp
                    }

                    Text(
                        text = "• $genre",
                        fontFamily = PressStart2P,
                        fontSize = dynamicFontSize,
                        color = Black,
                        softWrap = false,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }
        }
    }
}
