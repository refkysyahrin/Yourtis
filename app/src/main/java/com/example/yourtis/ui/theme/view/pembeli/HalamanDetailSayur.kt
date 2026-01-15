package com.example.yourtis.ui.theme.view.pembeli

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.ui.theme.viewmodel.HomeUiState
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailSayur(
    idSayur: Int,
    onNavigateBack: () -> Unit,
    viewModel: PembeliViewModel
) {
    // State untuk menyimpan data sayur detail yang di-fetch dari backend
    var sayur by remember { mutableStateOf<Sayur?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    // Fetch data sayur detail saat halaman pertama kali dibuka
    LaunchedEffect(idSayur) {
        isLoading = true
        hasError = false
        try {
            val fetchedSayur = viewModel.getSayurDetail(idSayur)
            sayur = fetchedSayur
            if (fetchedSayur == null) {
                hasError = true
            }
        } catch (e: Exception) {
            hasError = true
        } finally {
            isLoading = false
        }
    }

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
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2E7D32))
                }
            }
            hasError || sayur == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Gagal memuat data produk. Coba kembali nanti.")
                }
            }
            else -> {
                val data = sayur!!

                // Mapping URL Gambar:
                // 1. Gunakan gambar_url jika tersedia (URL lengkap dari server)
                // 2. Ganti localhost dengan 10.0.2.2 untuk emulator
                // 3. Fallback ke gambar (nama file) jika gambar_url kosong
                val imageUrl = if (!data.gambar_url.isNullOrBlank()) {
                    data.gambar_url.replace("localhost", "10.0.2.2")
                } else if (!data.gambar.isNullOrBlank()) {
                    "http://10.0.2.2:3000/uploads/${data.gambar}"
                } else {
                    "https://via.placeholder.com/300?text=Gambar+Tidak+Tersedia"
                }

                Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = data.nama_sayur,
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(data.nama_sayur, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text("Rp ${data.harga} / kg", color = Color(0xFF2E7D32), style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Stok: ${data.stok} kg", style = MaterialTheme.typography.bodyLarge)
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
            }
        }
    }
}
