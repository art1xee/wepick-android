package com.example.wepick

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wepick.navigation.NavGraph
import com.example.wepick.util.LocaleSettings
import com.example.wepick.screens.MainScaffold
import com.example.wepick.ui.theme.WePickTheme
import com.example.wepick.util.LocalHelper
import com.example.wepick.viewmodel.AuthViewModel
import com.example.wepick.viewmodel.ContentViewModel
import com.example.wepick.viewmodel.MainViewModel
import com.example.wepick.viewmodel.PlayerViewModel
import com.example.wepick.viewmodel.ProfileSetupViewModel

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context?) {
        val lang = LocaleSettings.getLanguage(newBase)
        LocalHelper.updateResources(newBase, lang)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()
        val playerVM: PlayerViewModel by viewModels()
        val contentVM: ContentViewModel by viewModels()
        val authViewModel: AuthViewModel by viewModels()
        val profileViewModel: ProfileSetupViewModel by viewModels()
        viewModel.initLanguage(this)

        enableEdgeToEdge()
        setContent {
            WePickTheme {
                val navController = rememberNavController()
                MainScaffold(viewModel, playerVM, contentVM, navController) {
                    StartProgram(navController, viewModel, playerVM, contentVM, authViewModel, profileViewModel)
                }
            }
        }
    }


}

@Composable
fun StartProgram(
    navController: NavHostController,
    viewModel: MainViewModel,
    playerVM: PlayerViewModel,
    contentVM: ContentViewModel,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileSetupViewModel

) {
    NavGraph(
        navController = navController,
        viewModel = viewModel,
        playerVM = playerVM,
        contentVM = contentVM,
        authViewModel = authViewModel,
        profileViewModel = profileViewModel,
    )
}



