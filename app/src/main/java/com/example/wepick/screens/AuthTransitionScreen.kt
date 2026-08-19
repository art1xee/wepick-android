package com.example.wepick.screens

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wepick.R
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DeepPurple
import com.example.wepick.ui.theme.MidPurple
import com.example.wepick.ui.theme.Nunito
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (state) {
                    is AuthTransitionState.Loading -> {
                        CircularProgressIndicator(
                            color = CardYellow,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = state.message,
                            color = White,
                            fontFamily = Nunito,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    is AuthTransitionState.Success -> {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(TealSuccess, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.auth_transition_welcome),
                            fontFamily = PressStart2P,
                            color = Color.White,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color(0x40000000),
                                    offset = Offset(4f, 4f),
                                    blurRadius = 8f
                                )
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.auth_transition_subtitle),
                            fontFamily = Nunito,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}


