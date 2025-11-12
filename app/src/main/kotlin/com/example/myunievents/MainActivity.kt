package com.example.myunievents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.myunievents.ui.nav.AppNavHost
import com.example.myunievents.ui.theme.MyUniEventsTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val prefs = remember { ServiceLocator.provideThemePrefs(this) }
            var isDarkTheme by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = prefs) {
                prefs.isDarkMode.collectLatest { isDark ->
                    isDarkTheme = isDark
                }
            }

            MyUniEventsTheme(darkTheme = isDarkTheme) {
                Surface {
                    AppNavHost(navController = rememberNavController())
                }
            }
        }
    }
}
