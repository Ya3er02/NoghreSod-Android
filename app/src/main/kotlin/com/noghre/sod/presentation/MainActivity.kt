package com.noghre.sod.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.noghre.sod.presentation.navigation.NoghreSodNavigation
import com.noghre.sod.presentation.theme.NoghreSodTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * MainActivity is the entry point of the NoghreSod jewelry e-commerce application.
 * 
 * Responsibilities:
 * - Initialize the Jetpack Compose UI system
 * - Set up theming and design system
 * - Handle navigation between screens
 * - Manage activity lifecycle
 * 
 * @since 1.0.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Timber.d("MainActivity created - Initializing app UI")
        
        setContent {
            NoghreSodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoghreSodApp()\n                }\n            }\n        }\n    }\n}\n\n/**\n * Root composable for the NoghreSod application.\n * Handles navigation and screen routing for the entire app.\n */\n@Composable\nfun NoghreSodApp() {\n    NoghreSodNavigation()\n}\n