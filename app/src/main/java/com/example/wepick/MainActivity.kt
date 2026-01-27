package com.example.wepick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wepick.screens.MainScreen
import com.example.wepick.ui.theme.WePickTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WePickTheme {
                val navController = rememberNavController()

                val viewModel: MainViewModel = viewModel()
                StartProgram(navController, viewModel)
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