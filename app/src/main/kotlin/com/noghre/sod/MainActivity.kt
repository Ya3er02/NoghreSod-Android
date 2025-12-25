package com.noghre.sod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
 import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.noghre.sod.presentation.navigation.NoghreSodNavigation
import com.noghre.sod.presentation.theme.NoghreSodTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for Noghresod Android app.
 * Serves as the entry point for Compose UI and navigation.
 *
 * @author Yaser
 * @version 1.0.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoghreSodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoghreSodNavigation()
                }
            }
        }
    }
}
