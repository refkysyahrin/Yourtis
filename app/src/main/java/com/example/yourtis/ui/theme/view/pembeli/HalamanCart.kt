package com.example.yourtis.ui.theme.view.pembeli

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanCart(
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    viewModel: PembeliViewModel // Menerima shared instance dari PengelolaHalaman
) {
    val cartItems = viewModel.cartItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang Belanja") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Pembayaran:", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Rp ${viewModel.calculateTotal()}",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToCheckout,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("Lanjut ke Checkout")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                Text("Keranjang Anda kosong", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Menambahkan gambar kecil di keranjang agar sesuai SRS
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.sayur.gambar_url?.replace("localhost", "10.0.2.2"))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(70.dp)
                                    .padding(4.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.sayur.nama_sayur,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Rp ${item.sayur.harga} x ${item.qty} kg",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Rp ${item.sayur.harga * item.qty}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }

                            // Kontrol Jumlah (Quantity)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.removeFromCart(item) },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Kurang")
                                }

                                Text(
                                    text = "${item.qty}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                IconButton(
                                    onClick = { viewModel.addToCart(item.sayur) },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFF2E7D32)),
                                    enabled = item.qty < item.sayur.stok
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}