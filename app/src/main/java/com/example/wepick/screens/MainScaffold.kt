package com.example.wepick.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.graphics.Brush
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.White
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.DeepPurple
import com.example.wepick.ui.theme.MidPurple
import com.example.wepick.ui.theme.PressStart2P
import com.example.wepick.ui.theme.PrimaryPurple
import com.example.wepick.viewmodel.ContentViewModel
import com.example.wepick.viewmodel.PlayerViewModel

@Composable
fun MainScaffold(
    viewModel: MainViewModel,
    playerVM: PlayerViewModel,
    contentVM: ContentViewModel,
    navController: NavController,
    content: @Composable (Modifier) -> Unit
) {
    val isMenuOpen by viewModel.isMenuOpen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    val shouldShowLogo = currentRoute != ScreenNav.Login.route &&
            currentRoute != ScreenNav.ForgotPassword.route &&
            currentRoute != ScreenNav.SignUp.route

    val screensWithBottomBar = listOf(
        ScreenNav.Home.route,
        ScreenNav.SettingScreen.route
    )
    val shouldShowBottomBar = currentRoute in screensWithBottomBar

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomAppBar(
                    containerColor = Color.Transparent,
                    contentColor = CardYellow
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate(ScreenNav.Home.route) {
                                popUpTo(ScreenNav.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (currentRoute == ScreenNav.Home.route) White else Color.DarkGray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        FloatingActionButton(
                            onClick = {
                                Toast.makeText(context, "Open Bottom Sheet", Toast.LENGTH_SHORT).show()
                            },
                            containerColor = CardYellow
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = DeepPurple
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            navController.navigate(ScreenNav.SettingScreen.route) {
                                popUpTo(ScreenNav.Home.route)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (currentRoute == ScreenNav.SettingScreen.route) White else Color.DarkGray
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent // Keep our custom background visible
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(MidPurple, DeepPurple)
                    )
                )
                .padding(paddingValues)
        ) {

            content(Modifier.fillMaxSize())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 25.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (shouldShowLogo) {
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
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
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


            if (isMenuOpen) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    OverlayMenu(
                        onClose = { viewModel.closeMenu() },
                        navController = navController,
                        viewModel = viewModel,
                        playerVM = playerVM,
                        contentVM = contentVM,
                    )
                }

            }
        }
    }
}