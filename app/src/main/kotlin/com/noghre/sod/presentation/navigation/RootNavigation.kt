package com.noghre.sod.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noghre.sod.presentation.screen.auth.LoginScreen
import com.noghre.sod.presentation.screen.auth.RegisterScreen
import com.noghre.sod.presentation.screen.home.HomeScreen
import com.noghre.sod.presentation.screen.splash.SplashScreen
import com.noghre.sod.presentation.viewmodel.AuthViewModel

@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn = authViewModel.isLoggedIn.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                isLoggedIn = isLoggedIn.value
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable("home") {
            HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }
    }
}
