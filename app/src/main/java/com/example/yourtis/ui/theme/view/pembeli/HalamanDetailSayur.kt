package com.example.yourtis.ui.theme.view.pembeli

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.yourtis.ui.theme.viewmodel.HomeUiState
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailSayur(
    idSayur: Int,
    onNavigateBack: () -> Unit,
    viewModel: PembeliViewModel
) {
    // Refresh data setiap kali masuk ke halaman detail
    LaunchedEffect(idSayur) {
        viewModel.getSayur()
    }

    val state = viewModel.homeUiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            is HomeUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            is HomeUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Gagal memuat detail produk") }
            is HomeUiState.Success -> {
                // MENCARI DATA LANGSUNG DARI STATE SUKSES
                val data = state.sayur.find { it.id_sayur == idSayur }
                
                if (data != null) {
                    val imageUrl = if (!data.gambar_url.isNullOrBlank()) {
                        data.gambar_url.replace("localhost", "10.0.2.2")
                    } else {
                        "http://10.0.2.2:3000/uploads/${data.gambar}"
                    }

                    Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(300.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(data.nama_sayur, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                Text("Rp ${data.harga} / kg", color = Color(0xFF2E7D32), style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Stok: ${data.stok}", style = MaterialTheme.typography.bodyLarge)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                                Text("Deskripsi Produk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(data.deskripsi, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        Surface(shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { viewModel.addToCart(data) },
                                modifier = Modifier.padding(16.dp).fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                enabled = data.stok > 0
                            ) {
                                Text("Tambah ke Keranjang")
                            }
                        }
                    }
                } else {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("Produk tidak ditemukan (ID: $idSayur)")
                    }
                }
            }
        }
    }
}
