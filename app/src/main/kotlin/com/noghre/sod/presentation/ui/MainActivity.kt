package com.noghre.sod.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noghre.sod.presentation.theme.NoghreSodTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - نقطه ورود برنامه
 * تمام UI از طریق Jetpack Compose و NoghreSodTheme
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            NoghreSodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigation Graph
                    NoghreSodApp()
                }
            }
        }
    }
}

/**
 * تطبیق اصلی NoghreSod
 * شامل Navigation، Screens، و Composables
 */
@Composable
fun NoghreSodApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }
        composable("catalog") {
            CatalogScreen(navController)
        }
        composable("cart") {
            CartScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("payment") {
            PaymentScreen(navController)
        }
    }
}

/**
 * صفحه خانه
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    // صفحه خانه با استفاده از Vazir fonts و رنگ‌های ایرانی
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // محتوا
    }
}

/**
 * صفحه نمایش
 */
@Composable
fun CatalogScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // نمایش محصولات
    }
}

/**
 * صفحه سبد خرید
 */
@Composable
fun CartScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // سبد خرید
    }
}

/**
 * صفحه پروفایل
 */
@Composable
fun ProfileScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // اطلاعات پروفایل
    }
}

/**
 * صفحه پرداخت
 */
@Composable
fun PaymentScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // درگاه‌های پرداخت
    }
}
