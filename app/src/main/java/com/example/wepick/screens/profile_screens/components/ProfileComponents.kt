package com.example.wepick.screens.profile_screens.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wepick.R
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DarkButtonPurple
import com.example.wepick.ui.theme.DeepPurple
import com.example.wepick.ui.theme.MidPurple
import com.example.wepick.ui.theme.Nunito
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.viewmodel.profile_view_model.ProfileSetupViewModel


@Composable
fun ProfileInfoBlock(
    photoUrl: String?,
    name: String,
    userName: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            // Главный Box карточки (позволяет наложить кнопку в правый верхний угол)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFFE49A), Color(0xFFFFC94D))
                        )
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {

                // Колонка с контентом по центру
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // ТОЛЬКО аватарка
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, DarkButtonPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Отступ и тексты теперь СНАРУЖИ аватарки, но внутри Column
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = name.ifEmpty { "???" },
                        fontFamily = Nunito,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = userName,
                        fontFamily = Nunito,
                        fontSize = 12.sp,
                        color = Black.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }

                // Кнопка редактирования поверх всего в правом верхнем углу
                IconButton(
                    onClick = onClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Работает идеально, так как родитель - Box
                        .size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit profile",
                        tint = DarkButtonPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileBoxButton(
    onClick: () -> Unit,
    contentDescription: String,
    firstStartColor: Color,
    secondStartColor: Color,
    firstEndColor: Color,
    secondEndColor: Color,
    icon: ImageVector,
    text: String,
    subtext: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Движение карточки
    val offsetX by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        animationSpec = tween(150),
        label = "offset"
    )

    // Цвет карточки
    val startColor by animateColorAsState(
        targetValue = if (isPressed)
            firstStartColor
        else
            secondStartColor,
        animationSpec = tween(150),
        label = "startColor"
    )

    val endColor by animateColorAsState(
        targetValue = if (isPressed)
            firstEndColor
        else
            secondEndColor,
        animationSpec = tween(150),
        label = "endColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = offsetX)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            startColor,
                            endColor
                        )
                    )
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Иконка слева
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = CardYellow,
                modifier = Modifier.size(24.dp)
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            // Название + описание
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    fontFamily = Nunito,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = subtext,
                        fontFamily = Nunito,
                        color = Color(0xFFBFA9C8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Стрелка справа
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFFBFA9C8),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DangerZoneButton(
    icon: ImageVector,
    text: String,
    borderColor: Color,
    containerColor: Color = Color.Transparent,
    textColor: Color = AccentRed,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor.copy(0.3f), shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(14.dp))

            Text(
                text = text,
                fontFamily = Nunito,
                fontSize = 14.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Composable
fun ActionConfirmDialog(
    title: String,
    text: String,
    confirmText: String,
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = text,
                fontFamily = Nunito
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = if (isDestructive) AccentRed else DarkButtonPurple,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.profile_setting_cancel),
                    color = Color.Gray
                )
            }
        },
        containerColor = Color.White, // Или цвет твоего фона карточек
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ValidationTrailingIcon(
    status: ProfileSetupViewModel.ValidationStatus,
    modifier: Modifier = Modifier,
) {
    when (status) {
        ProfileSetupViewModel.ValidationStatus.LOADING -> {
            CircularProgressIndicator(
                modifier = modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = DarkButtonPurple,
            )
        }

        ProfileSetupViewModel.ValidationStatus.AVAILABLE -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Available",
                tint = Color(0xFF2E7D32),
                modifier = modifier.size(20.dp)
            )
        }

        ProfileSetupViewModel.ValidationStatus.TAKEN -> {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Taken",
                tint = Color(0xFFD32F2F),
                modifier = modifier.size(20.dp)
            )
        }

        ProfileSetupViewModel.ValidationStatus.IDLE -> {
            // when status is IDLE showing nothing
        }
    }
}
