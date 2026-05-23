package com.daur.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daur.app.ui.components.BottomNavBar
import com.daur.app.ui.components.bottomNavItems

// Route tabs yang tampil di BottomNavBar
private val bottomNavRoutes = bottomNavItems.map { it.route }

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "beranda"

    // Sembunyikan BottomNavBar di detail screen
    val showBottomBar = bottomNavRoutes.any { currentRoute.startsWith(it) }

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick  = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = "beranda",
            modifier         = Modifier.padding(innerPadding)
        ) {
            // ── Beranda ──────────────────────────────────
            composable("beranda") {
                BerandaScreen(
                    onSetor        = { navController.navigate("setor") },
                    onTukarPoin    = { navController.navigate("hadiah") },
                    onLihatRiwayat = { navController.navigate("riwayat") }
                )
            }

            // ── Setor Sampah ──────────────────────────────
            composable("setor") {
                SetorSampahScreen()
            }

            // ── Riwayat Setoran ───────────────────────────
            composable("riwayat") {
                RiwayatSetoranScreen()
            }

            // ── Tukar Poin ────────────────────────────────
            composable("hadiah") {
                TukarPoinScreen()
            }

            // ── Katalog Sampah ────────────────────────────
            composable("katalog") {
                KatalogSampahScreen()
            }

            // ── Edukasi (list) ────────────────────────────
            composable("edukasi") {
                EdukasiLingkunganScreen(navController = navController)
            }

            // ── Edukasi Detail ────────────────────────────
            composable(
                route     = "edukasi_detail/{edukasiId}",
                arguments = listOf(navArgument("edukasiId") { type = NavType.StringType })
            ) { backStackEntry ->
                val edukasiId = backStackEntry.arguments?.getString("edukasiId") ?: ""
                EdukasiDetailScreen(
                    edukasiId     = edukasiId,
                    navController = navController
                )
            }

            // ── Profil ────────────────────────────────────
            composable("profil") {
                ProfilScreen(onLogout = onLogout)
            }
        }
    }
}