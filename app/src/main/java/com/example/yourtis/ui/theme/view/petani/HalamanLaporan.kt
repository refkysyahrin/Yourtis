package com.example.yourtis.ui.theme.view.petani

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.ui.theme.viewmodel.DashboardUiState
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel
import com.example.yourtis.ui.theme.viewmodel.PetaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanLaporan(
    onNavigateBack: () -> Unit,
    viewModel: PetaniViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val state = viewModel.dashboardUiState
    val isRefreshing = state is DashboardUiState.Loading
    val pullToRefreshState = rememberPullToRefreshState() // Definisikan state secara eksplisit

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedTransaksi by remember { mutableStateOf<Transaksi?>(null) }
    var filterStatus by remember { mutableStateOf("Semua") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Transaksi", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadDashboard() },
            state = pullToRefreshState, // Pasang state ke Box
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState, // Gunakan state yang sama di sini
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = Color(0xFF2E7D32),
                    color = Color.White
                )
            }
        ) {
            when (state) {
                is DashboardUiState.Loading -> LoadingScreen(Modifier.fillMaxSize())
                is DashboardUiState.Error -> ErrorScreen(onRetry = { viewModel.loadDashboard() }, modifier = Modifier.fillMaxSize())
                is DashboardUiState.Success -> {
                    val filteredTransaksi = if (filterStatus == "Semua") {
                        state.listTransaksi
                    } else {
                        state.listTransaksi.filter { it.status == filterStatus }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterButton("Semua", filterStatus == "Semua") { filterStatus = "Semua" }
                            FilterButton("Pending", filterStatus == "Pending") { filterStatus = "Pending" }
                            FilterButton("Proses", filterStatus == "Proses") { filterStatus = "Proses" }
                            FilterButton("Selesai", filterStatus == "Selesai") { filterStatus = "Selesai" }
                        }

                        if (filteredTransaksi.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Belum ada transaksi status '$filterStatus'.", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredTransaksi) { trx ->
                                    ItemTransaksiLengkap(trx) {
                                        selectedTransaksi = trx
                                        showDialog = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog && selectedTransaksi != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Update Status Pesanan") },
                text = { Text("ID: ${selectedTransaksi!!.id_transaksi}\nStatus: ${selectedTransaksi!!.status}") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.updateStatusTransaksi(selectedTransaksi!!.id_transaksi, "Selesai")
                        showDialog = false
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) { Text("Selesai") }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        viewModel.updateStatusTransaksi(selectedTransaksi!!.id_transaksi, "Proses")
                        showDialog = false
                    }) { Text("Proses") }
                }
            )
        }
    }
}

// Fungsi ini akan digunakan bersama oleh HalamanHomePetani
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFF2E7D32))
    }
}

@Composable
fun ErrorScreen(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gagal memuat data.")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Coba Lagi") }
    }
}

@Composable
fun ItemTransaksiLengkap(trx: Transaksi, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "ID: #${trx.id_transaksi}", fontWeight = FontWeight.Bold)
                StatusBadge(status = trx.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Alamat: ${trx.alamat_pengiriman}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = "Metode: ${trx.metode_bayar} (${trx.metode_kirim})", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Pembayaran", fontWeight = FontWeight.SemiBold)
                Text(text = "Rp ${trx.total_bayar}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "Selesai" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "Proses" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        else -> Color(0xFFFFFDE7) to Color(0xFFFBC02D)
    }
    Box(modifier = Modifier
        .background(bgColor, RoundedCornerShape(4.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(status, color = textColor, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun FilterButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2E7D32) else Color(0xFFF1F1F1),
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(8.dp)
    ) { Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold) }
}
