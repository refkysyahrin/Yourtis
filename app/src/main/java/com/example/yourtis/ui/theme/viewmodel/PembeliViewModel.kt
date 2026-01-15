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

sealed interface HomeUiState {
    data class Success(val sayur: List<Sayur>) : HomeUiState
    object Error : HomeUiState
    object Loading : HomeUiState
}

data class CartItem(
    val sayur: Sayur,
    val qty: Int
)

class PembeliViewModel(private val repository: YourTisRepository) : ViewModel() {

    // Menyimpan ID User dari Login agar Checkout tidak gagal di Database
    var currentUserId by mutableStateOf(0)

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    // Menggunakan mutableStateListOf agar perubahan di Katalog/Detail langsung muncul di Cart
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    var checkoutUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    init { getSayur() }

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

    fun calculateTotal(): Int = _cartItems.sumOf { it.sayur.harga * it.qty }

    fun processCheckout(alamat: String, metodeKirim: String, metodeBayar: String) {
        if (currentUserId == 0) return

        viewModelScope.launch {
            checkoutUiState = LoginUiState.Loading
            try {
                val transactionData = mapOf(
                    "id_pembeli" to currentUserId,
                    "total_bayar" to calculateTotal(),
                    "metode_kirim" to metodeKirim,
                    "metode_bayar" to metodeBayar,
                    "alamat_pengiriman" to alamat,
                    "items" to _cartItems.map {
                        mapOf("id_sayur" to it.sayur.id_sayur, "qty" to it.qty, "subtotal" to (it.sayur.harga * it.qty))
                    }
                )
                repository.checkout(transactionData)
                _cartItems.clear()
                // Gunakan dummy User agar tipe Success cocok dengan LoginUiState
                checkoutUiState = LoginUiState.Success(User(0, "", "", "", "", ""))
            } catch (e: Exception) {
                checkoutUiState = LoginUiState.Error
            }
        }
    }

    fun resetCheckoutState() { checkoutUiState = LoginUiState.Idle }
}