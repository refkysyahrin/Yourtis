package com.example.yourtis.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.User
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch

// State UI untuk Login/Register
sealed interface LoginUiState {
    data class Success(val user: User) : LoginUiState
    data class Error(val message: String) : LoginUiState // Ditambah message error
    object Loading : LoginUiState
    object Idle : LoginUiState
}

class AuthViewModel(private val repository: YourTisRepository) : ViewModel() {

    var uiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    // State untuk Form Input
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var noHp by mutableStateOf("")
    var alamat by mutableStateOf("")
    var role by mutableStateOf("Pembeli")

    // Fungsi Login
    fun login() {
        // VALIDASI: Email dan Password tidak boleh kosong
        if (email.isBlank() || password.isBlank()) {
            uiState = LoginUiState.Error("Email dan password tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                val response = repository.login(email, password)

                if (response.data != null) {
                    uiState = LoginUiState.Success(response.data)
                } else {
                    uiState = LoginUiState.Error("Email atau password salah")
                }
            } catch (e: Exception) {
                uiState = LoginUiState.Error("Gagal login: ${e.message}")
            }
        }
    }

    // Fungsi Register
    fun register(onSuccess: () -> Unit) {
        // VALIDASI: Semua field harus diisi
        if (username.isBlank() || email.isBlank() || password.isBlank() || noHp.isBlank() || alamat.isBlank()) {
            uiState = LoginUiState.Error("Semua data harus diisi!")
            return
        }

        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                val newUser = User(
                    id_user = 0,
                    username = username,
                    email = email,
                    role = role,
                    no_hp = noHp,
                    alamat = alamat
                )

                repository.register(newUser, password)
                uiState = LoginUiState.Success(newUser)
                // onSuccess dipanggil di LaunchedEffect UI saja agar konsisten
            } catch (e: Exception) {
                uiState = LoginUiState.Error("Gagal daftar: ${e.message}")
            }
        }
    }

    fun resetState() {
        uiState = LoginUiState.Idle
    }
}
