package com.example.yourtis.ui.theme.view.petani

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel
import com.example.yourtis.ui.theme.viewmodel.PetaniViewModel
import com.example.yourtis.ui.theme.viewmodel.ProdukUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanKelolaProduk(
    onNavigateBack: () -> Unit,
    onNavigateToEntry: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: PetaniViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.loadProduk()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Produk Sayuran") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToEntry) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { innerPadding ->
        when (val state = viewModel.produkUiState) {
            is ProdukUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            is ProdukUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Gagal memuat data") }
            is ProdukUiState.Success -> {
                if (state.sayur.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Belum ada produk") }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.sayur) { sayur ->
                            CardSayur(sayur = sayur, onDelete = { viewModel.deleteSayur(it) }, onEdit = onNavigateToEdit)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardSayur(sayur: Sayur, onDelete: (Int) -> Unit, onEdit: (Int) -> Unit) {
    // DISESUAIKAN KE PORT 3000 SESUAI CONTAINER APP
    val imageUrl = if (!sayur.gambar_url.isNullOrBlank()) {
        sayur.gambar_url.replace("localhost", "10.0.2.2")
    } else {
        "http://10.0.2.2:3000/uploads/${sayur.gambar}"
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = sayur.nama_sayur, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "Rp ${sayur.harga} / kg", color = MaterialTheme.colorScheme.primary)
                Text(text = "Stok: ${sayur.stok}", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sayur.deskripsi,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { onEdit(sayur.id_sayur) }) { Icon(Icons.Default.Edit, "Edit", tint = Color.Blue) }
                    IconButton(onClick = { onDelete(sayur.id_sayur) }) { Icon(Icons.Default.Delete, "Hapus", tint = Color.Red) }
                }
            }
        }
    }
}
