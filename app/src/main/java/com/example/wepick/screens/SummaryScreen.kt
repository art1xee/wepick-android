package com.example.wepick.screens

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.ContentType
import com.example.wepick.FindContentButton
import com.example.wepick.MainViewModel
import com.example.wepick.NextButton
import com.example.wepick.R
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.ButtonResetBg
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DecadeColor
import com.example.wepick.ui.theme.DislikeContentColor
import com.example.wepick.ui.theme.LikeContentColor
import com.example.wepick.ui.theme.Muted
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
        Spacer(Modifier.height(30.dp))

        // Основная подложка (Желтая область)
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

                // Тип контенту с 3D текстом
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .background(White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .border(2.dp, AccentRed, RoundedCornerShape(12.dp))
                        .padding(16.dp),
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
                                fontSize = 12.sp,
                                color = White,
                                modifier = Modifier.offset(x = 1.dp, y = 1.dp)
                            )
                            Text(
                                text = contentDisplayName,
                                fontFamily = PressStart2P,
                                fontSize = 12.sp,
                                color = AccentRed
                            )
                        }
                    }
                }

                // Секция участников
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
                    onNextClick = { /* Запуск поиска */ }
                )
            }
        }
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
    // Весь столбец игрока теперь имеет легкую подложку, как на сайте
    Column(
        modifier = modifier
            .background(White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        // Имя игрока (Красный текст)
        Text(
            text = "$label $name",
            fontFamily = PressStart2P,
            fontSize = 10.sp,
            color = AccentRed,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Жанры (Дизлайки)
        SummaryInfoBlock(
            label = stringResource(R.string.dislikes_watch),
            items = dislikes,
            color = DislikeContentColor
        )

        Spacer(Modifier.height(12.dp))

        // Жанры (Лайки)
        SummaryInfoBlock(
            label = stringResource(R.string.likes_watch),
            items = likes,
            color = LikeContentColor
        )

        Spacer(Modifier.height(12.dp))

        // Блок Декады (Коричневатый фон)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ButtonResetBg, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                stringResource(R.string.decade),
                fontFamily = PressStart2P,
                fontSize = 7.sp,
                color = TextTeal
            )
            Text(
                text = decade.toString(),
                fontFamily = PressStart2P,
                fontSize = 10.sp,
                color = Black
            )
        }
    }
}

@Composable
fun SummaryInfoBlock(label: String, items: List<String>, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = PressStart2P,
            fontSize = 8.sp,
            color = TextTeal,
            modifier = Modifier
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (items.isEmpty()) {
                Text("—", fontFamily = PressStart2P, fontSize = 8.sp, color = Black.copy(0.4f))
            } else {
                items.forEach { genre ->
                    Text(
                        text = "• $genre",
                        fontFamily = PressStart2P,
                        fontSize = 8.sp,
                        color = Black
                    )
                }
            }
        }
    }
}
