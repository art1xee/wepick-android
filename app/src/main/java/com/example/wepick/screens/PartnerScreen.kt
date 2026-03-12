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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wepick.data.model.ContentType
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.ui.components.NextButton
import com.example.wepick.ui.components.PartnerChooseButton
import com.example.wepick.R
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.FriendColor
import com.example.wepick.ui.theme.PopularCharacterColor
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.ui.theme.TextTeal
import com.example.wepick.viewmodel.PlayerViewModel


@Composable
fun PartnerScreen(
    navController: NavController,
    viewModel: MainViewModel,
    playerVM: PlayerViewModel,
    modifier: Modifier
) {
    val selectedPartnerType = viewModel.partnerType
    val selectedType = viewModel.selectedContentType.value

    val contentDisplayName = when (selectedType) {
        ContentType.Movie -> stringResource(R.string.movie_content_partner)
        ContentType.Tv -> stringResource(R.string.series_content_partner)
        ContentType.Anime -> stringResource(R.string.asian_content_partner)
        null -> ""
    }

    Column(
        modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
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
                    text = stringResource(R.string.partner_label, contentDisplayName),
                    fontFamily = PressStart2P,
                    color = TextTeal,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PartnerChooseButton(
                        isSelected = (selectedPartnerType == "friend"),
                        onClick = { viewModel.updatePartnerType("friend") },
                        modifier = modifier.padding(bottom = 8.dp),
                        activeColor = FriendColor,
                        text = stringResource(R.string.friend_partner)
                    )
                    Spacer(modifier.height(12.dp))
                    PartnerChooseButton(
                        isSelected = (selectedPartnerType == "character"),
                        onClick = { viewModel.updatePartnerType("character") },
                        modifier = modifier.padding(bottom = 8.dp),
                        activeColor = PopularCharacterColor,
                        text = stringResource(R.string.popular_character_partner)
                    )
                    Spacer(modifier.height(24.dp))
                    NextButton(
                        navController = navController,
                        modifier = modifier,
                        route = if (selectedPartnerType == "friend") {
                            ScreenNav.FriendName.route
                        } else {
                            ScreenNav.CharacterPicker.route
                        },
                        enabled = selectedPartnerType.isNotEmpty(),
                        onNextClick = {
                            viewModel.updatePartnerType(selectedPartnerType)

                            playerVM.isPartnerFriend = (selectedPartnerType == "friend")
                        }
                    )

                }
            }
        }
    }
}