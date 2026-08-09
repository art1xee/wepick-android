package com.example.wepick.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wepick.screens.AuthTransitionScreen
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.screens.CharacterPickerScreen
import com.example.wepick.screens.FavoriteScreen
import com.example.wepick.screens.ForgotPasswordScreen
import com.example.wepick.screens.FriendNameScreen
import com.example.wepick.screens.GenresScreen
import com.example.wepick.screens.HomeScreen
import com.example.wepick.screens.LoginScreen
import com.example.wepick.screens.MainScreen
import com.example.wepick.screens.MatchScreen
import com.example.wepick.screens.PartnerScreen
import com.example.wepick.screens.ProfileSetup
import com.example.wepick.screens.SelectionScreen
import com.example.wepick.screens.SettingsScreen
import com.example.wepick.screens.SignUpScreen
import com.example.wepick.screens.SummaryScreen
import com.example.wepick.viewmodel.AuthTransitionState
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.ContentViewModel
import com.example.wepick.viewmodel.PlayerViewModel
import com.example.wepick.viewmodel.ProfileSetupViewModel


@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    playerVM: PlayerViewModel,
    contentVM: ContentViewModel,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileSetupViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ScreenNav.Login.route
    ) {
        composable(ScreenNav.Favorite.route) {
            FavoriteScreen(navController, viewModel, modifier = Modifier, playerVM)
        }
        composable(ScreenNav.ProfileSetup.route) {
            ProfileSetup(navController, profileViewModel, modifier = Modifier)
        }
        composable(ScreenNav.Home.route) {
            HomeScreen(navController, authViewModel)
        }
        composable(ScreenNav.SettingScreen.route) {
            SettingsScreen(navController, viewModel, modifier = Modifier, playerVM)
        }
        composable(ScreenNav.SignUp.route) {
            SignUpScreen(navController, viewModel, modifier = Modifier, playerVM, authViewModel)
        }
        composable(ScreenNav.Login.route) {
            LoginScreen(navController, viewModel, modifier = Modifier, playerVM, authViewModel)
        }
        composable(ScreenNav.ForgotPassword.route) {
            ForgotPasswordScreen(
                navController,
                viewModel,
                modifier = Modifier,
                playerVM,
                authViewModel
            )

        }
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
    authViewModel.transitionState?.let { state ->
        AuthTransitionScreen(state)
    }
}

