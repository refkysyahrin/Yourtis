package com.example.yourtis.ui.theme.view.pembeli

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourtis.ui.theme.viewmodel.LoginUiState
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanCheckout(
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    viewModel: PembeliViewModel // Menggunakan shared instance dari PengelolaHalaman
) {
    var alamat by remember { mutableStateOf("") }
    var metodeKirim by remember { mutableStateOf("Diantar") }
    var metodeBayar by remember { mutableStateOf("Transfer") }

    val checkoutState = viewModel.checkoutUiState

    // Navigasi otomatis jika status Success
    LaunchedEffect(checkoutState) {
        if (checkoutState is LoginUiState.Success) {
            onCheckoutSuccess()
            viewModel.resetCheckoutState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Ringkasan Pembayaran
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ringkasan Pembayaran", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Belanja")
                        Text(
                            "Rp ${viewModel.calculateTotal()}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Input Alamat Pengiriman
            Text("Alamat Pengiriman", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                placeholder = { Text("Alamat lengkap") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Metode Pengiriman
            Text("Metode Pengiriman", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = metodeKirim == "Diantar", onClick = { metodeKirim = "Diantar" })
                Text("Diantar")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = metodeKirim == "Pickup", onClick = { metodeKirim = "Pickup" })
                Text("Pickup")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metode Pembayaran
            Text("Metode Pembayaran", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = metodeBayar == "Transfer", onClick = { metodeBayar = "Transfer" })
                Text("Transfer")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = metodeBayar == "COD", onClick = { metodeBayar = "COD" })
                Text("COD")
            }

            if (metodeBayar == "Transfer") {
                Card(
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7))
                ) {
                    Text(
                        "Transfer ke: BCA - 1234567890 a.n. Petani Sayur",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Buat Pesanan
            Button(
                onClick = {
                    // PERBAIKAN: Memasukkan parameter (alamat, metodeKirim, metodeBayar)
                    // ID Pembeli sudah tersimpan otomatis di Shared ViewModel
                    viewModel.processCheckout(alamat, metodeKirim, metodeBayar)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = alamat.isNotBlank() && checkoutState !is LoginUiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                if (checkoutState is LoginUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Buat Pesanan", fontWeight = FontWeight.Bold)
                }
            }

            // Pesan Error jika gagal
            if (checkoutState is LoginUiState.Error) {
                Text(
                    "Terjadi kesalahan saat memproses pesanan.",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}