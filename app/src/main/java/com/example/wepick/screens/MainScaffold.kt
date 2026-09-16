package com.example.wepick.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.Games
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.ui.theme.CardYellow
import com.example.wepick.ui.theme.CardYellowSoft
import com.example.wepick.ui.theme.DeepPurple
import com.example.wepick.ui.theme.MidPurple

@Composable
fun MainScaffold(
    navController: NavController,
    content: @Composable (Modifier) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val focusManager = LocalFocusManager.current

    val screensWithBottomBar = listOf(
        ScreenNav.Home.route,
        ScreenNav.ProfileSettingScreen.route,
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
                        navRoute = ScreenNav.ProfileSettingScreen,
                        backRoute = ScreenNav.Home,
                        modifier = Modifier.weight(1f),
                        imageVector = if (currentRoute == ScreenNav.ProfileSettingScreen.route) Icons.Filled.Person else Icons.Filled.PersonOutline,
                        contentDescription = null,
                        tint = if (currentRoute == ScreenNav.ProfileSettingScreen.route) CardYellow else CardYellowSoft,
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
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                content(Modifier.fillMaxSize())
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