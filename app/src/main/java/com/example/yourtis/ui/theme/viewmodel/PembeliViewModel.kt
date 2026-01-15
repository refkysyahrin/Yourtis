package com.example.yourtis.ui.theme.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.modeldata.User
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch

// State untuk memantau pengambilan data katalog sayur
sealed interface HomeUiState {
    data class Success(val sayur: List<Sayur>) : HomeUiState
    object Error : HomeUiState
    object Loading : HomeUiState
}

// Model data untuk item di dalam keranjang belanja
data class CartItem(
    val sayur: Sayur,
    val qty: Int
)

class PembeliViewModel(private val repository: YourTisRepository) : ViewModel() {

    // Menyimpan ID User yang login agar transaksi tercatat dengan benar
    var currentUserId by mutableStateOf(0)

    // State untuk memantau data katalog di halaman Home
    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    // State untuk menyimpan list riwayat transaksi
    var listTransaksi by mutableStateOf(listOf<Transaksi>())
        private set

    // Menggunakan mutableStateListOf agar perubahan di Katalog langsung sinkron ke Cart
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    // State untuk memantau proses checkout
    var checkoutUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    init {
        getSayur()
    }

    // Mengambil data sayur dari backend untuk katalog
    fun getSayur() {
        viewModelScope.launch {
            homeUiState = HomeUiState.Loading
            try {
                val listSayur = repository.getSayur()
                homeUiState = HomeUiState.Success(listSayur)
            } catch (e: Exception) {
                homeUiState = HomeUiState.Error
            }
        }
    }

    // Mengambil riwayat transaksi milik pembeli
    fun getTransactions() {
        if (currentUserId == 0) return
        viewModelScope.launch {
            try {
                val response = repository.getTransaksiByPembeli(currentUserId)
                listTransaksi = response
            } catch (e: Exception) {
                Log.e("VM_ERROR", "Gagal mengambil riwayat: ${e.message}")
                listTransaksi = emptyList()
            }
        }
    }

    // Mengambil detail sayur spesifik (untuk halaman detail)
    suspend fun getSayurDetail(id: Int): Sayur? {
        return try {
            repository.getSayurById(id)
        } catch (e: Exception) {
            null
        }
    }

    fun refreshSayur() {
        getSayur()
    }

    // Menambah produk ke keranjang
    fun addToCart(sayur: Sayur) {
        val index = _cartItems.indexOfFirst { it.sayur.id_sayur == sayur.id_sayur }
        if (index != -1) {
            val item = _cartItems[index]
            if (item.qty < sayur.stok) {
                _cartItems[index] = item.copy(qty = item.qty + 1)
            }
        } else {
            _cartItems.add(CartItem(sayur, 1))
        }
    }

    // Mengurangi atau menghapus produk dari keranjang
    fun removeFromCart(cartItem: CartItem) {
        val index = _cartItems.indexOfFirst { it.sayur.id_sayur == cartItem.sayur.id_sayur }
        if (index != -1) {
            val item = _cartItems[index]
            if (item.qty > 1) {
                _cartItems[index] = item.copy(qty = item.qty - 1)
            } else {
                _cartItems.removeAt(index)
            }
        }
    }

    // Menghitung total harga belanjaan
    fun calculateTotal(): Int = _cartItems.sumOf { it.sayur.harga * it.qty }

    // Memproses data pesanan ke backend
    fun processCheckout(alamat: String, metodeKirim: String, metodeBayar: String) {
        // Validasi input
        if (currentUserId == 0) {
            Log.e("CHECKOUT_ERROR", "Current User ID is 0. User belum login.")
            checkoutUiState = LoginUiState.Error
            return
        }

        if (_cartItems.isEmpty()) {
            Log.e("CHECKOUT_ERROR", "Keranjang kosong, tidak bisa checkout.")
            checkoutUiState = LoginUiState.Error
            return
        }

        viewModelScope.launch {
            checkoutUiState = LoginUiState.Loading
            try {
                // Konversi items ke List<Map> untuk memastikan serialisasi benar
                val itemsList = _cartItems.map {
                    mapOf(
                        "id_sayur" to it.sayur.id_sayur,
                        "qty" to it.qty,
                        "subtotal" to (it.sayur.harga * it.qty)
                    )
                }

                val transactionData = mapOf(
                    "id_pembeli" to currentUserId,
                    "total_bayar" to calculateTotal(),
                    "metode_kirim" to metodeKirim,
                    "metode_bayar" to metodeBayar,
                    "alamat_pengiriman" to alamat,
                    "items" to itemsList
                )

                Log.d("CHECKOUT", "Mengirim data: $transactionData")

                // Panggil checkout ke backend
                repository.checkout(transactionData)
                
                Log.d("CHECKOUT", "Checkout berhasil! Membersihkan cart...")

                // ✅ PERBAIKAN: Hanya clear cart setelah backend confirm sukses
                _cartItems.clear()

                // Refresh katalog untuk update stok terbaru
                getSayur()

                // Set success state
                checkoutUiState = LoginUiState.Success(User(id_user = currentUserId, "", "", "", "", ""))
                Log.d("CHECKOUT", "Success state set. Cart cleared.")

            } catch (e: Exception) {
                Log.e("CHECKOUT_ERROR", "Error saat checkout: ${e.message}", e)
                checkoutUiState = LoginUiState.Error
                // ❌ Jangan clear cart jika error - user bisa retry
            }
        }
    }

    fun resetCheckoutState() {
        checkoutUiState = LoginUiState.Idle
    }
}
