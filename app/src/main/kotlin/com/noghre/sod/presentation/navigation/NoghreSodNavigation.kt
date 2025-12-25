package com.noghre.sod.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noghre.sod.presentation.navigation.NavigationDestinations.HOME_ROUTE
import com.noghre.sod.presentation.navigation.NavigationDestinations.LOGIN_ROUTE
import com.noghre.sod.presentation.navigation.NavigationDestinations.SPLASH_ROUTE

/**
 * Main navigation composable for Noghresod app.
 * Handles navigation between all screens.
 *
 * @author Yaser
 * @version 1.0.0
 */
@Composable
fun NoghreSodNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SPLASH_ROUTE
    ) {
        // ============== SPLASH SCREEN ==============
        composable(route = SPLASH_ROUTE) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(LOGIN_ROUTE) {
                        popUpTo(SPLASH_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(HOME_ROUTE) {
                        popUpTo(SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // ============== AUTH SCREENS ==============
        composable(route = LOGIN_ROUTE) {
            LoginPlaceholder(
                onLoginSuccess = {
                    navController.navigate(HOME_ROUTE) {
                        popUpTo(LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // ============== HOME SCREEN ==============
        composable(route = HOME_ROUTE) {
            HomePlaceholder()
        }
    }
}

/**
 * Temporary placeholder screens during development.
 */
@Composable
private fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    // In production, this would have proper initialization logic
    androidx.compose.runtime.LaunchedEffect(key1 = Unit) {
        kotlinx.coroutines.delay(1000)
        onNavigateToLogin()
    }
}

@Composable
private fun LoginPlaceholder(onLoginSuccess: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder for login screen
        androidx.compose.material3.Text(
            text = "Login Screen Placeholder\n(Implementation in progress)",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun HomePlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder for home screen
        androidx.compose.material3.Text(
            text = "Home Screen Placeholder\n(Implementation in progress)",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
