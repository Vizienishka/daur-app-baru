package com.daur.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daur.app.data.SessionManager
import com.daur.app.ui.components.BottomNavBar
import com.daur.app.ui.components.bottomNavItems
import com.daur.app.viewmodel.*

private val bottomNavRoutes = bottomNavItems.map { it.route }

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    val context       = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStackEntry?.destination?.route ?: "beranda"
    val showBottomBar = bottomNavRoutes.any { currentRoute.startsWith(it) }

    // ── Hoist semua ViewModel ─────────────────────────────
    val berandaVm: BerandaViewModel   = viewModel()
    val riwayatVm: RiwayatViewModel   = viewModel()
    val setorVm:   SetorViewModel     = viewModel()
    val hadiahVm:  TukarPoinViewModel = viewModel()
    val edukasiVm: EdukasiViewModel   = viewModel()

    // ── Observer: session expired → logout → login ────────
    LaunchedEffect(Unit) {
        SessionManager.sessionExpired.collect {
            SessionManager.clear(context)
            onLogout()
        }
    }

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick  = { route ->
                        if (route == currentRoute) {
                            // Tap tab aktif → reload
                            when (route) {
                                "beranda" -> berandaVm.load()
                                "riwayat" -> riwayatVm.load()
                                "setor"   -> setorVm.loadKatalog()
                                "hadiah"  -> {
                                    hadiahVm.load()
                                    berandaVm.load()
                                }
                                "edukasi" -> edukasiVm.load()
                            }
                        } else {
                            // Reload data saat pindah ke tab baru
                            when (route) {
                                "beranda" -> berandaVm.load()
                                "riwayat" -> riwayatVm.load()
                                "hadiah"  -> {
                                    hadiahVm.load()
                                    berandaVm.load()
                                }
                            }
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
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
            composable("beranda") {
                BerandaScreen(
                    onSetor        = { navController.navigate("setor") },
                    onTukarPoin    = { navController.navigate("hadiah") },
                    onLihatRiwayat = { navController.navigate("riwayat") },
                    onProfile      = { navController.navigate("profil") },
                    vm             = berandaVm
                )
            }
            composable("setor") {
                SetorSampahScreen(
                    vm        = setorVm,
                    onSelesai = {
                        berandaVm.load()
                        riwayatVm.load()
                    }
                )
            }
            composable("riwayat") {
                RiwayatSetoranScreen(
                    vm        = riwayatVm,
                    onDeleted = { berandaVm.load() }
                )
            }
            composable("hadiah") {
                val berandaState by berandaVm.state.collectAsState()
                val userPoin = (berandaState as? BerandaState.Success)?.data?.profile?.totalPoin ?: 0
                TukarPoinScreen(
                    userPoin = userPoin,
                    onNavigateToMyVoucher = { 
                        berandaVm.load()
                        navController.navigate("my_voucher") 
                    },
                    vm = hadiahVm
                )
            }
            composable("my_voucher") {
                val myVoucherVm: MyVoucherViewModel = viewModel()
                MyVoucherScreen(
                    onBack = { navController.popBackStack() },
                    vm = myVoucherVm
                )
            }
            composable("katalog") {
                KatalogSampahScreen()
            }
            composable("edukasi") {
                EdukasiLingkunganScreen(
                    navController = navController,
                    vm            = edukasiVm
                )
            }
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
            composable("profil") {
                ProfilScreen(
                    onLogout = onLogout,
                    onMyVoucher = { navController.navigate("my_voucher") }
                )
            }
        }
    }
}