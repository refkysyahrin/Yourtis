package com.example.yourtis.ui.view.pembeli

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.ui.theme.viewmodel.HomeUiState
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanKatalog(
    onNavigateToCart: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onLogout: () -> Unit,
    viewModel: PembeliViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Katalog Sayur") },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang")
                    }
                    TextButton(onClick = onLogout) { Text("Keluar", color = Color.Red) }
                }
            )
        }
    ) { innerPadding ->
        when (val state = viewModel.homeUiState) {
            is HomeUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            is HomeUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Gagal memuat data") }
            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.sayur) { sayur ->
                        ItemKatalog(
                            sayur = sayur,
                            onAddToCart = { viewModel.addToCart(sayur) },
                            onClickDetail = { onNavigateToDetail(sayur.id_sayur) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemKatalog(sayur: Sayur, onAddToCart: () -> Unit, onClickDetail: () -> Unit) {
    // Penanganan URL Gambar: localhost -> 10.0.2.2 dan Port 3000
    val imageUrl = if (!sayur.gambar_url.isNullOrBlank()) {
        sayur.gambar_url.replace("localhost", "10.0.2.2")
    } else {
        // Port disesuaikan ke 3000 sesuai ContainerApp.kt
        "http://10.0.2.2:3000/uploads/${sayur.gambar}"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClickDetail() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = sayur.nama_sayur, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Rp ${sayur.harga} / kg", color = Color(0xFF2E7D32))
                Text(
                    text = sayur.deskripsi,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onAddToCart) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFF2E7D32))
            }
        }
    }
}
