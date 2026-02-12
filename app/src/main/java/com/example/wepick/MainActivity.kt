package com.example.wepick

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wepick.screens.MainScaffold
import com.example.wepick.ui.theme.WePickTheme

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context?) {
        val lang = LocaleSettings.getLanguage(newBase)
        LocalHelper.updateResources(newBase, lang)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()
        viewModel.initLanguage(this)

        enableEdgeToEdge()
        setContent {
            WePickTheme {
                val navController = rememberNavController()
                MainScaffold(viewModel, navController) {
                    StartProgram(navController, viewModel)
                }
            }
        }
    }


}

@Composable
fun StartProgram(navController: NavHostController, viewModel: MainViewModel) {
    NavGraph(
        navController = navController,
        viewModel = viewModel
    )
}



