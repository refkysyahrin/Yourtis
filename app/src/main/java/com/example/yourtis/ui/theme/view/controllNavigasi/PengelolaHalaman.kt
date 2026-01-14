package com.example.yourtis.ui.theme.view.controllNavigasi

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yourtis.ui.theme.view.auth.HalamanLogin
import com.example.yourtis.ui.theme.view.auth.HalamanRegister
import com.example.yourtis.ui.theme.view.petani.HalamanHomePetani

// Route
object DestinasiLogin { const val route = "login" }
object DestinasiRegister { const val route = "register" }
object DestinasiHomePetani { const val route = "home_petani" }
object DestinasiHomePembeli { const val route = "home_pembeli" }
object DestinasiEntrySayur { const val route = "entry_sayur" }

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
            // 1. LOGIN
            composable(route = DestinasiLogin.route) {
                HalamanLogin(
                    onLoginSuccess = { user ->
                        if (user.role == "Petani") {
                            navController.navigate(DestinasiHomePetani.route) {
                                popUpTo(DestinasiLogin.route) { inclusive = true }
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

            // 2. REGISTER (Sudah menggunakan HalamanRegister Asli)
            composable(route = DestinasiRegister.route) {
                HalamanRegister(
                    onRegisterSuccess = {
                        // Kembali ke login setelah sukses
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        // Kembali jika batal
                        navController.popBackStack()
                    }
                )
            }

            // 3. DASHBOARD PETANI (ASLI)
            composable(route = DestinasiHomePetani.route) {
                HalamanHomePetani(
                    onLogout = {
                        navController.navigate(DestinasiLogin.route) {
                            popUpTo(0)
                        }
                    },
                    onNavigateToEntry = {
                        navController.navigate(DestinasiEntrySayur.route)
                    }
                )
            }
            // 4. HALAMAN TAMBAH SAYUR (Placeholder dulu)
            composable(route = DestinasiEntrySayur.route) {
                // Kita akan buat HalamanEntrySayur di langkah berikutnya
                androidx.compose.material3.Text("Halaman Form Tambah Sayur (Segera Dibuat)")
            }


            // 4. DASHBOARD PEMBELI (Masih Dummy)
            composable(route = DestinasiHomePembeli.route) {
                HalamanHomePembeliDummy(
                    onLogout = {
                        navController.navigate(DestinasiLogin.route) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
    }
}

// Dummy sementara
@Composable
fun HalamanHomePetaniDummy(onLogout: () -> Unit) {
    androidx.compose.material3.Button(onClick = onLogout) {
        Text("Ini Dashboard Petani. Klik Logout")
    }
}

@Composable
fun HalamanHomePembeliDummy(onLogout: () -> Unit) {
    androidx.compose.material3.Button(onClick = onLogout) {
        Text("Ini Katalog Pembeli. Klik Logout")
    }
}