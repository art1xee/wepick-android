package com.example.wepick.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.viewmodel.AuthState
import com.example.wepick.viewmodel.AuthViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        val destination = when (authState) {
            is AuthState.Authenticated -> ScreenNav.Home.route
            is AuthState.NeedsProfileSetup -> ScreenNav.ProfileSetup.route
            is AuthState.Unauthenticated, is AuthState.Error -> ScreenNav.Login.route
            else -> null // ещё ждём первый ответ AuthStateListener/Firestore
        }

        destination?.let {
            navController.navigate(it) {
                popUpTo(ScreenNav.Splash.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
