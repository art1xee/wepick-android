package com.example.wepick.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.CardYellowSoft
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.PlayerViewModel
import com.example.wepick.viewmodel.ProfileSetupViewModel

@Composable
fun ProfileSettingScreen(
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    playerVM: PlayerViewModel,
    profileViewModel: ProfileSetupViewModel
) {

    val photoUrl by profileViewModel.photoUrl.collectAsState()
    val isImageUploading by profileViewModel.isImageUploading.collectAsState()

    LaunchedEffect(Unit){
        profileViewModel.fetchUserProfile()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardYellow),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text( // TODO make this text in separate func
                    text = "Profile",
                    fontFamily = PressStart2P,
                    fontSize = 18.sp,
                    color = White,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFC58A1E),
                            offset = Offset(x = 8f, y = 8f),
                            blurRadius = 0f
                        )
                    )
                )
                Spacer(Modifier.height(24.dp))

                ProfileAvatarScreen(photoUrl)

            }

        }
    }
}

@Composable
fun ProfileAvatarScreen( // TODO: make this fun for all user info block
    photoUrl: String?,
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFE49A), Color(0xFFFFC94D))
                )
            )
            .border(3.dp, DarkButtonPurple, CircleShape),
        contentAlignment = Alignment.Center
    ) {

        if (!photoUrl.isNullOrEmpty()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "?",
                style = TextStyle(
                    fontFamily = PressStart2P,
                    fontSize = 32.sp,
                    color = DarkButtonPurple
                )
            )
        }
    }
}