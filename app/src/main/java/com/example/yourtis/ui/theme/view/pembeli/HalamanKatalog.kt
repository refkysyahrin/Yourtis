package com.example.yourtis.ui.view.pembeli // PERBAIKAN: Package disesuaikan

// HAPUS: import android.R (Ini penyebab error resource merah)

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // PENTING: Wajib import ini manual
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yourtis.R
import com.example.yourtis.modeldata.Sayur // Import Model Data
import com.example.yourtis.ui.theme.view.petani.ErrorScreen
import com.example.yourtis.ui.theme.view.petani.LoadingScreen
import com.example.yourtis.ui.theme.viewmodel.HomeUiState
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanKatalog(
    onLogout: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: PembeliViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Hitung total item di keranjang untuk Badge
    val cartCount = viewModel.cartItems.sumOf { it.qty }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Katalog Sayur") },
                actions = {
                    // Ikon Keranjang dengan Badge
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge { Text(cartCount.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang")
                        }
                    }
                    // Tombol Logout
                    TextButton(onClick = onLogout) {
                        Text("Keluar")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

        when (val state = viewModel.homeUiState) {
            is HomeUiState.Loading -> LoadingScreen(modifier = Modifier.padding(innerPadding))
            is HomeUiState.Success -> ListKatalog(
                listSayur = state.sayur,
                modifier = Modifier.padding(innerPadding),
                onAddToCart = { sayur -> viewModel.addToCart(sayur) }
            )
            is HomeUiState.Error -> ErrorScreen(
                retryAction = { viewModel.getSayur() },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun ListKatalog(
    listSayur: List<Sayur>,
    modifier: Modifier = Modifier,
    onAddToCart: (Sayur) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PERBAIKAN: items sudah dikenali karena import androidx.compose.foundation.lazy.items
        items(listSayur) { sayur ->
            CardKatalog(sayur = sayur, onAdd = onAddToCart)
        }
    }
}

@Composable
fun CardKatalog(sayur: Sayur, onAdd: (Sayur) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Gambar Produk
            val imageUrl = sayur.gambar_url?.replace("localhost", "10.0.2.2")

            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                // PERBAIKAN: Resource diambil dari R (com.example.yourtis.R) bukan android.R
                error = painterResource(R.drawable.ic_launcher_foreground),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = sayur.nama_sayur,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sayur.nama_sayur,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rp ${sayur.harga} / kg",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sisa Stok: ${sayur.stok}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Tombol Tambah ke Keranjang
                FilledTonalIconButton(
                    onClick = { onAdd(sayur) },
                    enabled = sayur.stok > 0
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = "Beli")
                }
            }
        }
    }
}