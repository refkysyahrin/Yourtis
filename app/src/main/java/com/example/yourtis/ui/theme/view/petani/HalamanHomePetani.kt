package com.example.yourtis.ui.theme.view.petani

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.ui.theme.viewmodel.DashboardUiState
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel
import com.example.yourtis.ui.theme.viewmodel.PetaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHomePetani(
    onLogout: () -> Unit,
    onNavigateToKelolaProduk: () -> Unit,
    onNavigateToLaporan: () -> Unit,
    viewModel: PetaniViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard Admin", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1B5E20)
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = viewModel.dashboardUiState) {
            is DashboardUiState.Loading -> LoadingScreen(modifier = Modifier.padding(innerPadding))
            is DashboardUiState.Error -> ErrorScreen(onRetry = { viewModel.loadDashboard() }, modifier = Modifier.padding(innerPadding))
            is DashboardUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Total Pendapatan", color = Color.White, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Rp ${state.totalPendapatan}",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${state.jumlahPesanan} pesanan bulan ini", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Pesanan Terbaru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.listTransaksi) { trx ->
                            ItemTransaksi(trx)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToKelolaProduk,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Kelola Produk Sayuran")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onNavigateToLaporan,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Lihat Laporan Lengkap", color = Color(0xFF2E7D32))
                    }
                }
            }
        }
    }
}

@Composable
fun ItemTransaksi(trx: Transaksi) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(trx.id_transaksi, fontWeight = FontWeight.Bold)
                Text("Pembeli ID: ${trx.id_pembeli}", style = MaterialTheme.typography.bodySmall)
                Text("Rp ${trx.total_bayar}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }

            val (bgColor, textColor) = when (trx.status) {
                "Selesai" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                "Proses" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                else -> Color(0xFFFFFDE7) to Color(0xFFFBC02D)
            }

            Box(
                modifier = Modifier
                    .background(color = bgColor, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(trx.status, color = textColor, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
