package com.example.yourtis.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.User
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// Status UI untuk Login & Register
sealed interface LoginUiState {
    object Success : LoginUiState
    object Error : LoginUiState
    object Loading : LoginUiState
    object Idle : LoginUiState
}

class AuthViewModel(private val repository: YourTisRepository) : ViewModel() {

    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    var currentUser: User? by mutableStateOf(null)
        private set

    var errorMessage: String by mutableStateOf("")
        private set

    fun login(email: String, kataSandi: String) {
        viewModelScope.launch {
            loginUiState = LoginUiState.Loading
            try {
                val response = repository.login(email, kataSandi)
                currentUser = response.user
                loginUiState = LoginUiState.Success
            } catch (e: IOException) {
                loginUiState = LoginUiState.Error
                errorMessage = "Jaringan error: Pastikan server nyala"
            } catch (e: HttpException) {
                loginUiState = LoginUiState.Error
                errorMessage = "Login gagal: Email atau sandi salah"
            }
        }
    }

    // FUNGSI REGISTER LENGKAP
    fun register(
        username: String,
        email: String,
        sandi: String,
        peran: String,
        noHp: String,
        alamat: String
    ) {
        viewModelScope.launch {
            loginUiState = LoginUiState.Loading
            try {
                // Buat objek User (tanpa password, karena di model User.kt tdk ada password)
                val newUser = User(
                    id_user = 0, // 0 karena auto-increment
                    username = username,
                    email = email,
                    role = peran,
                    no_hp = noHp,
                    alamat = alamat
                )

                // Kirim user + sandi ke repository
                repository.register(newUser, sandi)

                loginUiState = LoginUiState.Success
            } catch (e: IOException) {
                loginUiState = LoginUiState.Error
                errorMessage = "Gagal koneksi server"
            } catch (e: HttpException) {
                loginUiState = LoginUiState.Error
                // Ambil pesan error dari response body jika memungkinkan
                errorMessage = "Gagal Daftar: Email mungkin sudah dipakai"
            }
        }
    }

    fun resetState() {
        loginUiState = LoginUiState.Idle
        // Jangan reset currentUser di sini agar data user tetap ada setelah login
        errorMessage = ""
    }

    // Fungsi khusus logout total
    fun logout() {
        loginUiState = LoginUiState.Idle
        currentUser = null
        errorMessage = ""
    }
}