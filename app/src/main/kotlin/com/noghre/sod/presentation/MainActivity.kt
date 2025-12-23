package com.noghre.sod.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.noghre.sod.presentation.navigation.BottomNavigationBar
import com.noghre.sod.presentation.navigation.NavGraph
import com.noghre.sod.presentation.navigation.Routes
import com.noghre.sod.presentation.theme.NoghreSodTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoghreSodTheme {
                NoghreSodApp()
            }
        }
    }
}

@Composable
fun NoghreSodApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavGraph(
                navController = navController,
                startDestination = Routes.HOME
            )
        }
    }
}
