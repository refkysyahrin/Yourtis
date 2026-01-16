package com.example.yourtis.ui.view.pembeli

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.ui.theme.viewmodel.HomeUiState
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanKatalog(
    viewModel: PembeliViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToPesanan: () -> Unit,
    onNavigateToProfil: () -> Unit,
    onLogout: () -> Unit
) {
    val homeUiState = viewModel.homeUiState
    val cartItems = viewModel.cartItems

    // SINKRONISASI: Memastikan data terbaru ditarik dari server setiap kali halaman dibuka
    LaunchedEffect(Unit) {
        viewModel.getSayur()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Katalog Sayuran",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32) // Hijau YourTis
                ),
                actions = {
                    // Badge Keranjang Real-time (REQ-TRX)
                    BadgedBox(
                        badge = {
                            if (cartItems.isNotEmpty()) {
                                Badge(containerColor = Color.Red) {
                                    Text(cartItems.size.toString(), color = Color.White)
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onNavigateToCart() }
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Keranjang",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Keluar",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Navigasi Bawah (REQ-UI-NAV)
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Tetap di halaman home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2E7D32),
                        selectedTextColor = Color(0xFF2E7D32),
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToPesanan() },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Pesanan") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToProfil() },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profil") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (homeUiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2E7D32))
                    }
                }
                is HomeUiState.Success -> {
                    if (homeUiState.sayur.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada sayur yang tersedia.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                        ) {
                            items(homeUiState.sayur) { sayur ->
                                SayurItem(
                                    sayur = sayur,
                                    onAddToCart = { viewModel.addToCart(it) },
                                    onItemClick = { onNavigateToDetail(sayur.id_sayur) }
                                )
                            }
                        }
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Gagal memuat data.")
                            Button(
                                onClick = { viewModel.getSayur() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SayurItem(
    sayur: Sayur,
    onAddToCart: (Sayur) -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gambar Produk dari Backend
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("http://10.0.2.2:3000/uploads/${sayur.gambar}")
                    .crossfade(true)
                    .build(),
                contentDescription = sayur.nama_sayur,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sayur.nama_sayur,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rp ${sayur.harga}",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Stok: ${sayur.stok} kg",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (sayur.stok > 0) Color.Gray else Color.Red
                )
            }

            // Tombol Tambah ke Keranjang (REQ-TRX)
            Button(
                onClick = { if (sayur.stok > 0) onAddToCart(sayur) },
                enabled = sayur.stok > 0,
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah",
                    tint = Color.White
                )
            }
        }
    }
}