package com.example.myunievents

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.myunievents.ui.nav.AppNavHost

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    AppNavHost(navController)
}
