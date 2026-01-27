package com.example.wepick

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wepick.screens.CharacterPickerScreen
import com.example.wepick.screens.FriendNameScreen
import com.example.wepick.screens.GenresScreen
import com.example.wepick.screens.MainScreen
import com.example.wepick.screens.MatchScreen
import com.example.wepick.screens.PartnerScreen
import com.example.wepick.screens.SelectionScreen
import com.example.wepick.screens.SummaryScreen


@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ScreenNav.Main.route
    ) {
        composable(ScreenNav.Main.route) {
            MainScreen(navController, viewModel, modifier = Modifier)
        }
        composable(ScreenNav.Selection.route) {
            SelectionScreen(navController, viewModel, modifier = Modifier)
        }
        composable(ScreenNav.Partner.route) {
            PartnerScreen(navController, viewModel,modifier = Modifier)
        }
        composable(ScreenNav.FriendName.route) {
            FriendNameScreen(navController, viewModel,modifier = Modifier)
        }

        composable(ScreenNav.CharacterPicker.route) {
            CharacterPickerScreen(navController, viewModel,modifier = Modifier)
        }

        composable(ScreenNav.Genres.route) {
            GenresScreen(navController, viewModel,modifier = Modifier)
        }
        composable(ScreenNav.Summary.route) {
            SummaryScreen(navController, viewModel,modifier = Modifier)
        }
        composable(ScreenNav.Match.route) {
            MatchScreen(navController, viewModel,modifier = Modifier)
        }
    }

}

