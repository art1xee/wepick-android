package com.example.wepick

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.Muted
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.White


// the parent button
@Composable
private fun BaseRetroButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color,
    showShadow: Boolean,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowOffset = 4.dp
    val targetOffset = if (isPressed) shadowOffset else 0.dp

    Box(
        modifier = modifier
            .offset(x = targetOffset, y = targetOffset)
            .drawBehind {
                if (showShadow && !isPressed) {
                    drawRoundRect(
                        color = Color(0xFF111111),
                        topLeft = Offset(shadowOffset.toPx(), shadowOffset.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(11.dp.toPx(), 11.dp.toPx())
                    )
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                onClick = { if (enabled) onClick() }
            )
            .background(containerColor, RoundedCornerShape(11.dp))
            .border(4.dp, Color(0xFF111111), RoundedCornerShape(11.dp))
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// button for choose navigation between screens
@Composable
fun NextButton(
    navController: NavController,
    modifier: Modifier = Modifier,
    route: String,
    enabled: Boolean,
    onNextClick: () -> Unit
) {
    BaseRetroButton(
        onClick = {
            if (enabled) {
                onNextClick()
                // Исправление: переходим только если путь не пустой
                if (route.isNotEmpty()) {
                    navController.navigate(route)
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        containerColor = if (enabled) AccentRed else Muted.copy(alpha = 0.5f),
        showShadow = enabled,
        content = {
            Text(
                text = stringResource(R.string.next_button),
                fontFamily = PressStart2P,
                color = if (enabled) White else Black.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
    )
}

@Composable
fun FindContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    route: String,
    enabled: Boolean,
    onNextClick: () -> Unit
) {
    BaseRetroButton(
        onClick = {
            if (enabled) {
                onNextClick()
                if (route.isNotEmpty()) {
                    navController.navigate(route)
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        containerColor = if (enabled) AccentRed else Muted.copy(alpha = 0.5f),
        showShadow = enabled,
        content = {
            Text(
                text = stringResource(R.string.find_content),
                fontFamily = PressStart2P,
                color = if (enabled) White else Black.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
    )
}

// button for choose content type
@Composable
fun ContentTypeButton(
    type: ContentType,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color,
    modifier: Modifier = Modifier,
    text: String
) {
    BaseRetroButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        containerColor = if (isSelected) activeColor else Muted,
        showShadow = true,
        content = {
            Text(
                text = text,
                fontFamily = PressStart2P,
                color = Black,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    )
}

@Composable
fun PartnerChooseButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    activeColor: Color,
) {
    BaseRetroButton(
        onClick = onClick,
        containerColor = if (isSelected) activeColor else Muted,
        showShadow = true,
        modifier = modifier.fillMaxWidth(),
        content = {
            Text(
                text = text,
                fontFamily = PressStart2P,
                color = Black,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    )
}
