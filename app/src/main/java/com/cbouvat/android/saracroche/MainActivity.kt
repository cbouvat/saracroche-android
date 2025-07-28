package com.cbouvat.android.saracroche

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cbouvat.android.saracroche.ui.help.HelpScreen
import com.cbouvat.android.saracroche.ui.home.HomeScreen
import com.cbouvat.android.saracroche.ui.report.ReportScreen
import com.cbouvat.android.saracroche.ui.settings.SettingsScreen
import com.cbouvat.android.saracroche.ui.theme.SaracrocheTheme

sealed class BottomNavItem(
  val route: String,
  val title: String,
  val icon: ImageVector
) {
  object Home : BottomNavItem("home", "Accueil", Icons.Filled.Home)
  object Report : BottomNavItem("report", "Signaler", Icons.Filled.Report)
  object Help : BottomNavItem("help", "Aide", Icons.Filled.QuestionMark)
  object Settings : BottomNavItem("settings", "Réglages", Icons.Filled.Settings)
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SaracrocheTheme {
        SaracrocheApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaracrocheApp() {
  val navController = rememberNavController()
  val navItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Report,
    BottomNavItem.Help,
    BottomNavItem.Settings
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        navItems.forEach { item ->
          NavigationBarItem(
            icon = { Icon(item.icon, contentDescription = item.title) },
            label = { Text(item.title) },
            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
            onClick = {
              navController.navigate(item.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                  saveState = true
                }
                launchSingleTop = true
                restoreState = true
              }
            }
          )
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = BottomNavItem.Home.route,
      modifier = Modifier.padding(innerPadding)
    ) {
      composable(BottomNavItem.Home.route) {
        HomeScreen()
      }
      composable(BottomNavItem.Report.route) {
        ReportScreen()
      }
      composable(BottomNavItem.Help.route) {
        HelpScreen()
      }
      composable(BottomNavItem.Settings.route) {
        SettingsScreen()
      }
    }
  }
}