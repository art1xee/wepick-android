package com.example.wepick.screens

import androidx.benchmark.traceprocessor.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.wepick.MainViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import com.example.wepick.R
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.PressStart2P

@Composable
fun MainScaffold(
    viewModel: MainViewModel,
    navController: NavController,
    content: @Composable (Modifier) -> Unit
) {
    val isMenuOpen by viewModel.isMenuOpen

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        content(Modifier.fillMaxSize())

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 25.dp)
                .zIndex(10f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                Text(
                    text = "WePick!",
                    fontFamily = PressStart2P,
                    fontSize = 18.sp,
                    color = Black,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .offset(x = 2.dp, y = 2.dp),
                )

                Text(
                    text = "WePick!",
                    fontFamily = PressStart2P,
                    fontSize = 18.sp,
                    color = CardYellow,
                    modifier = Modifier
                        .padding(start = 8.dp)

                )
            }
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(CardYellow, RoundedCornerShape(12.dp))
                    .border(2.dp, Black, RoundedCornerShape(12.dp))
                    .clickable { viewModel.toggleMenu() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMenuOpen) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = null,
                    tint = Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        OverlayMenu(
            isOpen = isMenuOpen,
            onClose = { viewModel.closeMenu() },
            navController = navController,
            viewModel = viewModel
        )
    }
}