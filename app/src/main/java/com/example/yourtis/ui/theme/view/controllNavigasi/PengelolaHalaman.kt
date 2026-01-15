package com.example.yourtis.ui.theme.view.controllNavigasi

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yourtis.ui.theme.view.auth.HalamanLogin
import com.example.yourtis.ui.theme.view.auth.HalamanRegister
import com.example.yourtis.ui.theme.view.pembeli.HalamanCart
import com.example.yourtis.ui.theme.view.pembeli.HalamanCheckout
import com.example.yourtis.ui.theme.view.pembeli.HalamanDetailSayur
import com.example.yourtis.ui.theme.view.petani.HalamanEntrySayur
import com.example.yourtis.ui.theme.view.petani.HalamanHomePetani
import com.example.yourtis.ui.theme.view.petani.HalamanKelolaProduk
import com.example.yourtis.ui.theme.view.petani.HalamanLaporan
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel
import com.example.yourtis.ui.view.pembeli.HalamanKatalog

@Composable
fun PengelolaHalaman(navController: NavHostController = rememberNavController()) {
    // Shared ViewModel untuk Pembeli
    val pembeliVM: PembeliViewModel = viewModel(factory = PenyediaViewModel.Factory)

    NavHost(navController = navController, startDestination = "login") {

        // --- AUTHENTICATION ---
        composable("login") {
            HalamanLogin(
                onLoginSuccess = { user ->
                    if (user.role == "Petani") {
                        navController.navigate("home_petani") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        pembeliVM.currentUserId = user.id_user
                        navController.navigate("home_pembeli") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            HalamanRegister(
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- RUTE PETANI (TAMBAHAN BARU) ---

        // 1. Halaman Utama Petani (Dashboard)
        composable("home_petani") {
            HalamanHomePetani(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToKelolaProduk = { navController.navigate("kelola_produk") },
                onNavigateToLaporan = { navController.navigate("laporan_transaksi") }
            )
        }

        // 2. Halaman Daftar Produk Petani
        composable("kelola_produk") {
            HalamanKelolaProduk(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEntry = { navController.navigate("entry_sayur?id=-1") }, // Tambah Baru
                onNavigateToEdit = { id -> navController.navigate("entry_sayur?id=$id") } // Edit
            )
        }

        // 3. Halaman Form Entry/Edit Sayur (Menggunakan Query Parameter)
        composable(
            route = "entry_sayur?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            HalamanEntrySayur(
                navigateBack = { navController.popBackStack() },
                idSayur = if (id != -1) id else null // Jika -1 berarti tambah baru, jika ada id berarti edit
            )
        }

        // 4. Halaman Laporan Penjualan/Transaksi Petani
        composable("laporan_transaksi") {
            HalamanLaporan(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- RUTE PEMBELI (SHARED VIEWMODEL) ---
        composable("home_pembeli") {
            HalamanKatalog(
                viewModel = pembeliVM,
                onNavigateToCart = { navController.navigate("cart") },
                onNavigateToDetail = { id -> navController.navigate("detail_sayur/$id") },
                onLogout = { navController.navigate("login") { popUpTo(0) } }
            )
        }

        composable(
            route = "detail_sayur/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            HalamanDetailSayur(
                idSayur = id,
                viewModel = pembeliVM,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("cart") {
            HalamanCart(
                viewModel = pembeliVM,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { navController.navigate("checkout") }
            )
        }

        composable("checkout") {
            HalamanCheckout(
                viewModel = pembeliVM,
                onNavigateBack = { navController.popBackStack() },
                onCheckoutSuccess = {
                    navController.navigate("home_pembeli") {
                        popUpTo("home_pembeli") { inclusive = true }
                    }
                }
            )
        }
    }
}