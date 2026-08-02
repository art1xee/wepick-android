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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Games
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.theme.AccentRed
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.ui.theme.Black
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.CardYellowSoft
import com.example.wepick.ui.theme.DeepPurple
import com.example.wepick.ui.theme.MidPurple
import com.example.wepick.ui.theme.PressStart2P
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
        ScreenNav.SettingScreen.route,
        ScreenNav.Favorite.route
        // TODO profile screen
        // TODO Game screen
        // TODO favourite screen
        // etc.
    )
    val shouldShowBottomBar = currentRoute in screensWithBottomBar

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomAppBar(
                    containerColor = Color.Transparent,
                    contentColor = CardYellow
                ) {

                    // Home bottom bar
                    IconsBottomBar(
                        navController = navController,
                        isInclusive = true,
                        navRoute = ScreenNav.Home,
                        backRoute = ScreenNav.Home,
                        modifier = Modifier.weight(1f),
                        imageVector =
                            if (currentRoute == ScreenNav.Home.route) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = null,
                        tint = if (currentRoute == ScreenNav.Home.route) CardYellow else CardYellowSoft,
                    )

                    // Favorite bottom bar 
                    IconsBottomBar(
                        navController = navController,
                        navRoute = ScreenNav.Favorite,
                        backRoute = ScreenNav.Home,
                        modifier = Modifier.weight(1f),
                        imageVector = if (currentRoute == ScreenNav.Favorite.route) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                        contentDescription = null,
                        tint = if (currentRoute == ScreenNav.Favorite.route) AccentRed else CardYellowSoft,
                        isInclusive = false,
                    )


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(ScreenNav.Selection.route) {
                                    popUpTo(ScreenNav.Home.route)
                                }
                            },
                            containerColor = CardYellow
                        ) {
                            Icon(
                                imageVector = if (currentRoute == ScreenNav.Selection.route) Icons.Filled.Games else Icons.Outlined.Games,
                                contentDescription = null,
                                tint = DeepPurple
                            )
                        }
                    }


                    // Profile bottom bar
                    IconsBottomBar(
                        navController = navController,
                        navRoute = ScreenNav.SettingScreen,
                        backRoute = ScreenNav.Home,
                        modifier = Modifier.weight(1f),
                        imageVector = if (currentRoute == ScreenNav.SettingScreen.route) Icons.Filled.Person else Icons.Filled.PersonOutline,
                        contentDescription = null,
                        tint = if (currentRoute == ScreenNav.SettingScreen.route) CardYellow else CardYellowSoft,
                        isInclusive = false
                    )
                    IconsBottomBar(
                        navController = navController,
                        navRoute = ScreenNav.Partner,
                        backRoute = ScreenNav.Home,
                        modifier = Modifier.weight(1f),
                        imageVector = if (currentRoute == ScreenNav.Partner.route) Icons.Filled.People else Icons.Filled.PeopleOutline,
                        contentDescription = null,
                        tint = if (currentRoute == ScreenNav.Partner.route) CardYellow else CardYellowSoft,
                        isInclusive = false
                    )


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
        ) {
            // Apply padding only to the main content
            Box(modifier = Modifier.padding(paddingValues)) {
                content(Modifier.fillMaxSize())
            }

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


@Composable
fun IconsBottomBar(
    navController: NavController,
    navRoute: ScreenNav,
    backRoute: ScreenNav,
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String?,
    tint: Color,
    isInclusive: Boolean, // it's mean that this screen gonna be the main point of program after the login/signup
) {
    IconButton(
        onClick = {
            navController.navigate(navRoute.route) {// the route where user gonna be after press the bottom bar button
                popUpTo(backRoute.route) {
                    inclusive =
                        isInclusive // if the screen 'inclusive' the value MUST be "TRUE" in other situations use "FALSE"
                }
            }
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector, // Icon for the bottom bar
            contentDescription = contentDescription, // content description (optional)
            modifier = Modifier.size(48.dp),
            tint = tint, // change color for the icon
        )
    }
}