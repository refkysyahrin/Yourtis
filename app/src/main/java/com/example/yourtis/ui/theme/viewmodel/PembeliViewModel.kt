package com.example.yourtis.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.Sayur
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

    // Menyimpan ID User yang login agar transaksi tercatat dengan benar di database
    var currentUserId by mutableStateOf(0)

    // State untuk memantau data katalog di halaman Home
    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    // Menggunakan mutableStateListOf agar perubahan di Katalog/Detail langsung sinkron ke Cart
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    // State untuk memantau proses checkout
    var checkoutUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    init {
        getSayur()
    }

    // Mengambil data sayur dari backend untuk ditampilkan di katalog
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

    // Mengambil detail sayur spesifik dari backend (untuk halaman detail dengan data terbaru)
    suspend fun getSayurDetail(id: Int): Sayur? {
        return try {
            repository.getSayurById(id)
        } catch (e: Exception) {
            null
        }
    }

    // Refresh data katalog (untuk pull-to-refresh)
    fun refreshSayur() {
        getSayur()
    }

    // Menambah produk ke keranjang belanja
    fun addToCart(sayur: Sayur) {
        val index = _cartItems.indexOfFirst { it.sayur.id_sayur == sayur.id_sayur }
        if (index != -1) {
            val item = _cartItems[index]
            // Memastikan penambahan tidak melebihi stok yang tersedia
            if (item.qty < sayur.stok) {
                _cartItems[index] = item.copy(qty = item.qty + 1)
            }
        } else {
            // Jika produk belum ada di keranjang, tambahkan sebagai item baru
            _cartItems.add(CartItem(sayur, 1))
        }
    }

    // Mengurangi atau menghapus produk dari keranjang belanja
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
        // Mencegah checkout jika user belum teridentifikasi
        if (currentUserId == 0) return

        viewModelScope.launch {
            checkoutUiState = LoginUiState.Loading
            try {
                // Menyiapkan payload data sesuai kebutuhan backend
                val transactionData = mapOf(
                    "id_pembeli" to currentUserId,
                    "total_bayar" to calculateTotal(),
                    "metode_kirim" to metodeKirim,
                    "metode_bayar" to metodeBayar,
                    "alamat_pengiriman" to alamat,
                    "items" to _cartItems.map {
                        mapOf(
                            "id_sayur" to it.sayur.id_sayur,
                            "qty" to it.qty,
                            "subtotal" to (it.sayur.harga * it.qty)
                        )
                    }
                )

                // Mengirim data ke repository
                repository.checkout(transactionData)

                // Mengosongkan keranjang belanja setelah sukses
                _cartItems.clear()

                // Mengubah status menjadi Success agar UI melakukan navigasi balik
                checkoutUiState = LoginUiState.Success(User(id_user = currentUserId, "", "", "", "", ""))
            } catch (e: Exception) {
                // Mengatur status Error jika koneksi atau server bermasalah
                checkoutUiState = LoginUiState.Error
            }
        }
    }

    // Reset status checkout agar bisa digunakan kembali nanti
    fun resetCheckoutState() {
        checkoutUiState = LoginUiState.Idle
    }
}