package com.example.wepick.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wepick.screens.AuthTransitionScreen
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.screens.CharacterPickerScreen
import com.example.wepick.screens.profile_screens.FavoriteContentScreen
import com.example.wepick.screens.auth.forgot_password.ForgotPasswordScreen
import com.example.wepick.screens.FriendNameScreen
import com.example.wepick.screens.GenresScreen
import com.example.wepick.screens.HomeScreen
import com.example.wepick.screens.auth.login.LoginScreen
import com.example.wepick.screens.MainScreen
import com.example.wepick.screens.MatchScreen
import com.example.wepick.screens.PartnerScreen
import com.example.wepick.screens.auth.profile_setup.ProfileSetup
import com.example.wepick.screens.SelectionScreen
import com.example.wepick.screens.profile_screens.ProfileSettingScreen
import com.example.wepick.screens.auth.signup.SignUpScreen
import com.example.wepick.screens.SummaryScreen
import com.example.wepick.screens.profile_screens.ChangePasswordScreen
import com.example.wepick.screens.profile_screens.DeleteAccountScreen
import com.example.wepick.screens.profile_screens.settings.AppSettingScreen
import com.example.wepick.screens.profile_screens.HelpScreen
import com.example.wepick.screens.profile_screens.PersonalDataScreen
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
        composable(ScreenNav.PersonalData.route) {
            PersonalDataScreen(
                navController,
                viewModel,
                modifier = Modifier,
                playerVM,
                profileViewModel
            )
        }
        composable(ScreenNav.Help.route) {
            HelpScreen(
                navController,
                viewModel,
                playerVM,
                profileViewModel,
            )

        }

        composable(ScreenNav.DeleteAccount.route) {
            DeleteAccountScreen(
                navController,
                viewModel,
                playerVM,
                profileViewModel,
            )
        }
        composable(ScreenNav.ChangePassword.route) {
            ChangePasswordScreen(
                navController,
                viewModel,
                playerVM,
                profileViewModel,
            )
        }
        composable(ScreenNav.AppSetting.route) {
            AppSettingScreen(
                navController,
                viewModel,
                modifier = Modifier,
                playerVM,
                profileViewModel,
            )
        }
        composable(ScreenNav.Favorite.route) {
            FavoriteContentScreen(navController, viewModel, modifier = Modifier, playerVM)
        }
        composable(ScreenNav.ProfileSetup.route) {
            ProfileSetup(navController, profileViewModel, modifier = Modifier)
        }
        composable(ScreenNav.Home.route) {
            HomeScreen(navController, authViewModel, profileViewModel)
        }
        composable(ScreenNav.ProfileSettingScreen.route) {
            ProfileSettingScreen(
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                navController = navController,
            )
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
    profileViewModel.transitionState?.let { state ->
        AuthTransitionScreen(state)
    }
}

