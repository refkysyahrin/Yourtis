package com.example.yourtis.ui.theme.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch
import java.io.IOException



// State untuk Dashboard Petani (Pendapatan & List Transaksi)
sealed interface DashboardUiState {
    data class Success(
        val totalPendapatan: Int,
        val jumlahPesanan: Int,
        val listTransaksi: List<Transaksi>
    ) : DashboardUiState
    object Error : DashboardUiState
    object Loading : DashboardUiState
}

// State untuk Produk (CRUD Sayur oleh Petani)
sealed interface ProdukUiState {
    data class Success(val sayur: List<Sayur>) : ProdukUiState
    object Error : ProdukUiState
    object Loading : ProdukUiState
}

class PetaniViewModel(private val repository: YourTisRepository) : ViewModel() {

    // Status UI untuk Dashboard Laporan
    var dashboardUiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)
        private set

    // Status UI untuk Pengelolaan Produk
    var produkUiState: ProdukUiState by mutableStateOf(ProdukUiState.Loading)
        private set

    init {
        loadDashboard()
        loadProduk()
    }

    /**
     * 1. Load Data Dashboard
     * Mengambil dan menghitung data pesanan secara real-time
     */
    fun loadDashboard() {
        viewModelScope.launch {
            dashboardUiState = DashboardUiState.Loading
            try {
                // Mengambil seluruh data transaksi dari repository
                val allTransactions = repository.getAllTransaksi()

                // REQ-MON-01: Menghitung total nominal pendapatan hanya dari pesanan yang 'Selesai'
                val totalSelesai = allTransactions
                    .filter { it.status == "Selesai" }
                    .sumOf { it.total_bayar }

                // Menghitung jumlah total pesanan yang masuk untuk statistik dashboard
                val count = allTransactions.size

                // REQ-MON-02: Menyediakan daftar pesanan lengkap untuk ditampilkan di laporan
                dashboardUiState = DashboardUiState.Success(
                    totalPendapatan = totalSelesai,
                    jumlahPesanan = count,
                    listTransaksi = allTransactions
                )
            } catch (e: Exception) {
                Log.e("PetaniVM", "Error load dashboard: ${e.message}")
                dashboardUiState = DashboardUiState.Error
            }
        }
    }

    /**
     * 2. Load Data Produk
     * Menampilkan katalog sayur yang dikelola petani agar tetap sinkron
     */
    fun loadProduk() {
        viewModelScope.launch {
            produkUiState = ProdukUiState.Loading
            try {
                val listSayur = repository.getSayur()
                produkUiState = ProdukUiState.Success(listSayur)
            } catch (e: Exception) {
                Log.e("PetaniVM", "Error load produk: ${e.message}")
                produkUiState = ProdukUiState.Error
            }
        }
    }

    /**
     * 3. Hapus Produk (REQ-PROD-03)
     * Menghapus sayur dan langsung memperbarui list produk
     */
    fun deleteSayur(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteSayur(id)
                // SINKRONISASI: Panggil ulang agar katalog pembeli & petani terupdate
                loadProduk()
            } catch (e: Exception) {
                Log.e("PetaniVM", "Error delete produk: ${e.message}")
            }
        }
    }

    /**
     * 4. Update Status Transaksi
     * Mengelola progres pesanan (Proses -> Selesai) dan update pendapatan
     */
    fun updateStatusTransaksi(idTransaksi: String, newStatus: String) {
        viewModelScope.launch {
            try {
                // Mengirim pembaruan status ke backend
                repository.updateStatusTransaksi(idTransaksi, newStatus)

                // Refresh dashboard agar angka pendapatan di Admin langsung berubah
                loadDashboard()
            } catch (e: Exception) {
                Log.e("PetaniVM", "Error update status: ${e.message}")
            }
        }
    }
}