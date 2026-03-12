package com.example.wepick.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.screens.CharacterPickerScreen
import com.example.wepick.screens.FriendNameScreen
import com.example.wepick.screens.GenresScreen
import com.example.wepick.screens.MainScreen
import com.example.wepick.screens.MatchScreen
import com.example.wepick.screens.PartnerScreen
import com.example.wepick.screens.SelectionScreen
import com.example.wepick.screens.SummaryScreen
import com.example.wepick.viewmodel.ContentViewModel
import com.example.wepick.viewmodel.PlayerViewModel


@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    playerVM: PlayerViewModel,
    contentVM: ContentViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ScreenNav.Main.route
    ) {
        composable(ScreenNav.Main.route) {
            MainScreen(navController, viewModel, modifier = Modifier, playerVM)
        }
        composable(ScreenNav.Selection.route) {
            SelectionScreen(navController, viewModel, modifier = Modifier, playerVM)
        }
        composable(ScreenNav.Partner.route) {
            PartnerScreen(navController, viewModel, playerVM, modifier = Modifier)
        }
        composable(ScreenNav.FriendName.route) {
            FriendNameScreen(navController, viewModel, playerVM, modifier = Modifier)
        }

        composable(ScreenNav.CharacterPicker.route) {
            CharacterPickerScreen(navController, viewModel, playerVM, modifier = Modifier)
        }

        composable(ScreenNav.Genres.route) {
            GenresScreen(navController, viewModel, playerVM, modifier = Modifier)
        }
        composable(ScreenNav.Summary.route) {
            SummaryScreen(navController, viewModel, modifier = Modifier, playerVM, contentVM)
        }
        composable(ScreenNav.Match.route) {
            MatchScreen(navController, viewModel, modifier = Modifier, playerVM, contentVM)
        }
    }
}

