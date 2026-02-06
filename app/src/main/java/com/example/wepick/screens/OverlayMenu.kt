package com.example.wepick.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.wepick.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wepick.CustomMatchButton
import com.example.wepick.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.ButtonResetBg
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.Muted
import com.example.wepick.ui.theme.PressStart2P
import androidx.core.net.toUri
import com.example.wepick.MainViewModel
import com.example.wepick.ui.theme.White

const val GIT_HUB = "https://github.com/art1xee/wepick-android"

@Composable
fun OverlayMenu(
    isOpen: Boolean,
    onClose: () -> Unit,
    navController: NavController,
    viewModel: MainViewModel
) {
    val annotatedText = buildAnnotatedString {
        pushStringAnnotation(tag = "URL", annotation = GIT_HUB)
        append("WePick!")
        pop()
    }
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Black.copy(0.4f))
                .clickable { onClose() },
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp)
                    .clickable(enabled = false) { },
                colors = CardDefaults.cardColors(White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, AccentRed)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.wepick_about),
                        fontFamily = PressStart2P,
                        fontSize = 16.sp,
                        color = AccentRed,
                    )
                    Spacer(Modifier.height(16.dp))
                    DashedDivider()
                    Text(
                        text = stringResource(R.string.wepick_info),
                        fontFamily = PressStart2P,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        color = Black
                    )
                    Spacer(Modifier.height(10.dp))
                    DashedDivider()
                    Spacer(Modifier.height(10.dp))
                    Text(

                        text = stringResource(R.string.select_lang),
                        fontFamily = PressStart2P,
                        fontSize = 9.sp,
                        color = Muted
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val langs = listOf("EN" to "en", "UA" to "uk", "RU" to "ru")
                        langs.forEach { (label, code) ->
                            val isSelected = currentLang == code
                            Box(
                                modifier = Modifier
                                    .size(width = 50.dp, height = 30.dp)
                                    .background(
                                        if (isSelected) AccentRed else White,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, Black, RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (!isSelected) viewModel.initLanguage(
                                            context
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontFamily = PressStart2P,
                                    fontSize = 10.sp,
                                    color = if (isSelected) White else Black
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    CustomMatchButton( // start over button
                        text = stringResource(R.string.start_again),
                        color = ButtonResetBg,
                        onClick = {
                            onClose()
                            navController.navigate(ScreenNav.Main.route) {
                                popUpTo(0)
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.wepick_github),
                        fontFamily = PressStart2P,
                        color = Muted,
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    DashedDivider()
                    ClickableText(
                        text = annotatedText,
                        modifier = Modifier.padding(top = 8.dp),
                        style = TextStyle(
                            fontFamily = PressStart2P,
                            fontSize = 10.sp,
                            color = AccentRed,
                            textAlign = TextAlign.Center,
                        ),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations("URL", offset, offset).firstOrNull()
                                ?.let {
                                    val intent = Intent(Intent.ACTION_VIEW, it.item.toUri())
                                    context.startActivity(intent)
                                }

                        },
                    )
                }
            }
        }
    }

}

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = Muted,
    thickness: Dp = 1.dp,
    dashLength: Float = 10f,
    gapLength: Float = 10f
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = thickness.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashLength, gapLength)
            )
        )
    }
}