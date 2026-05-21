package com.daur.app.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.daur.app.ui.screens.LoginScreen
import com.daur.app.ui.screens.SplashScreen

// ── Routes ────────────────────────────────────────────────
object Routes {
    const val SPLASH  = "splash"
    const val LOGIN   = "login"
    const val HOME    = "home"        // akan dibuat di tahap berikutnya
}

@Composable
fun DaurNavGraph(startDestination: String = Routes.SPLASH) {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Login / Register Screen
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Home (placeholder sampai screen berikutnya dibuat)
        composable(Routes.HOME) {
            // HomeScreen() — akan diisi di tahap 2
            androidx.compose.material3.Text("Home Screen — Coming Soon")
        }
    }
}
