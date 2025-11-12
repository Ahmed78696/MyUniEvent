package com.example.myunievents.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Announcement
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.example.myunievents.ui.screens.AddEventScreen
import com.example.myunievents.ui.screens.AnnouncementsScreen
import com.example.myunievents.ui.screens.EventDetailScreen
import com.example.myunievents.ui.screens.HomeScreen
import com.example.myunievents.ui.screens.LoginScreen
import com.example.myunievents.ui.screens.MyAnnouncementsScreen
import com.example.myunievents.ui.screens.MyEventsScreen
import com.example.myunievents.ui.screens.ProfileScreen
import com.example.myunievents.ui.screens.PublicEventsScreen
import com.example.myunievents.ui.screens.RegisterScreen
import com.example.myunievents.ui.screens.SettingsScreen

sealed class Routes(val route: String, val icon: ImageVector? = null, val title: String) {
    object AuthGraph : Routes("auth_graph", title = "Authentication")
    object MainGraph : Routes("main_graph", title = "Main App")

    object Login : Routes("login", title = "Login")
    object Register : Routes("register", title = "Register")

    object Home : Routes("home", Icons.Outlined.Home, "Home")
    object MyEvents : Routes("my_events", Icons.Outlined.Event, "My Events")
    object MyAnnouncements : Routes("my_announcements", Icons.Outlined.Announcement, "My Announcements")
    object Profile : Routes("profile", Icons.Outlined.Person, "Profile")

    object PublicEvents : Routes("public_events", title = "Public Events")
    object PublicAnnouncements : Routes("public_announcements", title = "Public Announcements")
    object AddEvent : Routes("add_event", title = "Add Event")
    object Settings : Routes("settings", title = "Settings")
    object EventDetail : Routes("event_detail/{eventId}", title = "Event Details") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
}

val BOTTOM_NAV_ITEMS = listOf(Routes.Home, Routes.MyEvents, Routes.MyAnnouncements, Routes.Profile)

private fun isMainScreen(route: String?): Boolean {
    return BOTTOM_NAV_ITEMS.any { route?.startsWith(it.route) == true } || route == Routes.PublicEvents.route || route == Routes.PublicAnnouncements.route || route == Routes.AddEvent.route || route == Routes.Settings.route || route?.startsWith(Routes.EventDetail.route.substringBefore("/")) == true
}

@Composable
fun AppNavHost(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = isMainScreen(currentDestination?.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BOTTOM_NAV_ITEMS.forEach { screen ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = { Text(screen.title) },
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.AuthGraph.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            navigation(startDestination = Routes.Login.route, route = Routes.AuthGraph.route) {
                composable(Routes.Login.route) { LoginScreen(navController) }
                composable(Routes.Register.route) { RegisterScreen(navController) }
            }

            navigation(startDestination = Routes.Home.route, route = Routes.MainGraph.route) {
                composable(Routes.Home.route) { HomeScreen(navController) }
                composable(Routes.MyEvents.route) { MyEventsScreen(navController) }
                composable(Routes.MyAnnouncements.route) { MyAnnouncementsScreen(navController) }
                composable(Routes.Profile.route) { ProfileScreen(navController) }

                composable(Routes.PublicEvents.route) { PublicEventsScreen(navController) }
                composable(Routes.PublicAnnouncements.route) { AnnouncementsScreen(navController) }
                composable(Routes.AddEvent.route) { AddEventScreen(navController) }
                composable(Routes.Settings.route) { SettingsScreen(navController) }
                composable(Routes.EventDetail.route) { backStackEntry ->
                    val eventId = backStackEntry.arguments?.getString("eventId")
                    if (eventId != null) {
                        EventDetailScreen(navController, eventId)
                    } else {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
