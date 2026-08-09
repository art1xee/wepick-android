package com.example.wepick.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.transform.CircleCropTransformation
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DeepPurple
import com.example.wepick.ui.theme.MidPurple
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.TealSuccess
import com.example.wepick.ui.theme.White
import com.example.wepick.viewmodel.AuthTransitionState

@Composable
fun AuthTransitionScreen(state: AuthTransitionState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush =
                    Brush.linearGradient(listOf(MidPurple, DeepPurple))
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = state,
                label = "auth_transition",
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { s ->
                when (s) {
                    is AuthTransitionState.Loading -> CircularProgressIndicator(
                        color = CardYellow,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(54.dp)
                    )

                    is AuthTransitionState.Success -> Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(TealSuccess),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = when (state) {
                    is AuthTransitionState.Loading -> state.message
                    is AuthTransitionState.Success -> state.message
                },
                color = White,
                fontFamily = PressStart2P,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}