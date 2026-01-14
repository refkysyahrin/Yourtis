package com.example.yourtis.ui.theme.view.controllNavigasi

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yourtis.ui.theme.view.auth.HalamanLogin
import com.example.yourtis.ui.theme.view.auth.HalamanRegister
import com.example.yourtis.ui.theme.view.pembeli.HalamanCart
import com.example.yourtis.ui.theme.view.petani.HalamanEntrySayur
import com.example.yourtis.ui.theme.view.petani.HalamanHomePetani
import com.example.yourtis.ui.view.pembeli.HalamanKatalog

// --- Definisi Route (Alamat Navigasi) ---
object DestinasiLogin {
    const val route = "login"
}

object DestinasiRegister {
    const val route = "register"
}

object DestinasiHomePetani {
    const val route = "home_petani"
}

object DestinasiEntrySayur {
    const val route = "entry_sayur"
}

object DestinasiHomePembeli {
    const val route = "home_pembeli"
}

object DestinasiCart {
    const val route = "cart"
}

@Composable
fun PengelolaHalaman(
    navController: NavHostController = rememberNavController()
) {
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DestinasiLogin.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // 1. HALAMAN LOGIN
            composable(route = DestinasiLogin.route) {
                HalamanLogin(
                    onLoginSuccess = { user ->
                        // Logika: Cek Role User untuk menentukan arah navigasi
                        if (user.role == "Petani") {
                            navController.navigate(DestinasiHomePetani.route) {
                                popUpTo(DestinasiLogin.route) { inclusive = true } // Hapus history login
                            }
                        } else {
                            navController.navigate(DestinasiHomePembeli.route) {
                                popUpTo(DestinasiLogin.route) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(DestinasiRegister.route)
                    }
                )
            }

            // 2. HALAMAN REGISTER
            composable(route = DestinasiRegister.route) {
                HalamanRegister(
                    onRegisterSuccess = {
                        navController.popBackStack() // Sukses daftar -> Kembali ke Login
                    },
                    onNavigateBack = {
                        navController.popBackStack() // Batal -> Kembali ke Login
                    }
                )
            }

            // --- AREA PETANI ---

            // 3. DASHBOARD PETANI
            composable(route = DestinasiHomePetani.route) {
                HalamanHomePetani(
                    onLogout = {
                        navController.navigate(DestinasiLogin.route) {
                            popUpTo(0) // Logout -> Hapus semua stack, kembali ke Login
                        }
                    },
                    onNavigateToEntry = {
                        navController.navigate(DestinasiEntrySayur.route)
                    }
                )
            }

            // 4. HALAMAN TAMBAH SAYUR (ENTRY)
            composable(route = DestinasiEntrySayur.route) {
                HalamanEntrySayur(
                    navigateBack = {
                        navController.popBackStack() // Selesai/Batal -> Kembali ke Dashboard
                    }
                )
            }

            // --- AREA PEMBELI ---

            // 5. KATALOG SAYUR (HOME PEMBELI)
            composable(route = DestinasiHomePembeli.route) {
                HalamanKatalog(
                    onLogout = {
                        navController.navigate(DestinasiLogin.route) {
                            popUpTo(0)
                        }
                    },
                    onNavigateToCart = {
                        navController.navigate(DestinasiCart.route)
                    }
                )
            }

            // 6. KERANJANG BELANJA & CHECKOUT
            composable(route = DestinasiCart.route) {
                HalamanCart(
                    onNavigateBack = {
                        navController.popBackStack() // Kembali ke Katalog
                    }
                )
            }
        }
    }
}