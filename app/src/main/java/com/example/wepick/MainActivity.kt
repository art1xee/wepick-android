package com.example.wepick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.Muted
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.WePickTheme
import com.example.wepick.ui.theme.White
import okhttp3.Route
import java.nio.file.Files.size

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WePickTheme {
                val navController = rememberNavController()

                val viewModel: MainViewModel = viewModel()
                StartProgram(navController, viewModel)
            }
        }
    }


}

@Composable
fun StartProgram(navController: NavHostController, viewModel: MainViewModel) {
    NavGraph(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun NextButton(
    navController: NavController,
    modifier: Modifier = Modifier,
    route: String,
    enabled: Boolean,
    onNextClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowOffset = 4.dp
    val targetOffset = if (isPressed) shadowOffset else 0.dp

    val containerColor = if (enabled) AccentRed else Color(0xFFDDDDDD)
    val contentColor = if (enabled) White else Color(0xFF111111).copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .padding(top = 16.dp)
            .offset(x = targetOffset, y = targetOffset)
            .drawBehind {
                if (!isPressed && enabled) {
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
                onClick = {
                    if (enabled) {
                        onNextClick()
                        navController.navigate(route)
                    }
                }
            )
            .background(
                color = containerColor,
                shape = RoundedCornerShape(11.dp)
            )
            .border(
                width = 4.dp,
                color = Color(0xFF111111),
                shape = RoundedCornerShape(11.dp)
            )
            .padding(horizontal = 40.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.next_button),
            fontFamily = PressStart2P,
            color = contentColor,
            fontSize = 16.sp
        )
    }
}


