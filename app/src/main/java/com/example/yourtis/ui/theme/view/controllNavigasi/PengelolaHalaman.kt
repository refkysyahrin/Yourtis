package com.example.yourtis.ui.theme.view.controllNavigasi

import androidx.compose.runtime.Composable
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
import com.example.yourtis.ui.theme.view.pembeli.HalamanProfil
import com.example.yourtis.ui.theme.view.petani.HalamanEntrySayur
import com.example.yourtis.ui.theme.view.petani.HalamanHomePetani
import com.example.yourtis.ui.theme.view.petani.HalamanKelolaProduk
import com.example.yourtis.ui.theme.view.petani.HalamanLaporan
import com.example.yourtis.ui.theme.viewmodel.EntryViewModel
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel
import com.example.yourtis.ui.theme.viewmodel.PetaniViewModel
import com.example.yourtis.ui.view.pembeli.HalamanKatalog


@Composable
fun PengelolaHalaman(navController: NavHostController = rememberNavController()) {
    // Shared ViewModels menggunakan Factory Penyedia
    val pembeliVM: PembeliViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val petaniVM: PetaniViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val entryVM: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)

    NavHost(navController = navController, startDestination = "login") {

        // --- AUTHENTICATION ---
        composable("login") {
            HalamanLogin(
                onLoginSuccess = { user ->
                    if (user.role == "Petani") {
                        entryVM.currentPetaniId = user.id_user
                        navController.navigate("home_petani") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        // Simpan ID User ke ViewModel agar data user tetap ada
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

        // --- PETANI FLOW ---
        composable("home_petani") {
            HalamanHomePetani(
                onLogout = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                },
                onNavigateToKelolaProduk = { navController.navigate("kelola_produk") },
                onNavigateToLaporan = { navController.navigate("laporan_transaksi_petani") }
            )
        }

        composable("kelola_produk") {
            HalamanKelolaProduk(
                viewModel = petaniVM,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEntry = { navController.navigate("entry_sayur?id=-1") },
                onNavigateToEdit = { id -> navController.navigate("entry_sayur?id=$id") }
            )
        }

        composable(
            route = "entry_sayur?id={id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            HalamanEntrySayur(
                viewModel = entryVM,
                navigateBack = { navController.popBackStack() },
                idSayur = if (id != -1) id else null
            )
        }

        composable("laporan_transaksi_petani") {
            // PERBAIKAN: Menggunakan petaniVM karena HalamanLaporan membutuhkan PetaniViewModel
            HalamanLaporan(viewModel = petaniVM, onNavigateBack = { navController.popBackStack() })
        }

        // --- PEMBELI FLOW (KATALOG & BOTTOM NAVIGATION) ---
        composable("home_pembeli") {
            HalamanKatalog(
                viewModel = pembeliVM,
                onNavigateToCart = { navController.navigate("cart") },
                onNavigateToDetail = { id -> navController.navigate("detail_sayur/$id") },
                // Hubungkan fungsi navigasi bawah
                onNavigateToPesanan = { navController.navigate("pesanan_pembeli") },
                onNavigateToProfil = { navController.navigate("profil_pembeli") },
                onLogout = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
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

        // --- DESTINASI BARU NAVIGASI BAWAH PEMBELI ---
        composable("pesanan_pembeli") {
            // CATATAN: HalamanLaporan saat ini didesain untuk Petani (Admin).
            // Jika ingin digunakan Pembeli, Anda perlu membuat versi Pembeli atau menyesuaikan ViewModelnya.
            // Untuk sementara, agar tidak error compiler, kita gunakan petaniVM jika fiturnya sama atau hapus navigasi ini.
            HalamanLaporan(
                viewModel = petaniVM, 
                onNavigateBack = {
                    navController.navigate("home_pembeli") {
                        popUpTo("home_pembeli") { inclusive = true }
                    }
                }
            )
        }

        composable("profil_pembeli") {
            HalamanProfil(
                onNavigateToHome = {
                    navController.navigate("home_pembeli") {
                        popUpTo("home_pembeli") { inclusive = true }
                    }
                },
                onNavigateToPesanan = {
                    navController.navigate("pesanan_pembeli")
                },
                onLogout = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}