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

// Status UI untuk Login
sealed interface LoginUiState {
    object Success : LoginUiState
    object Error : LoginUiState
    object Loading : LoginUiState
    object Idle : LoginUiState
}

class AuthViewModel(private val repository: YourTisRepository) : ViewModel() {

    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    // Menyimpan data user yang berhasil login (untuk navigasi role)
    var currentUser: User? by mutableStateOf(null)
        private set

    var errorMessage: String by mutableStateOf("")
        private set

    fun login(email: String, kataSandi: String) {
        viewModelScope.launch {
            loginUiState = LoginUiState.Loading
            try {
                // Panggil repository login
                val response = repository.login(email, kataSandi)

                // Simpan user & update status sukses
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

    // Fungsi untuk reset state jika logout atau kembali ke layar login
    fun resetState() {
        loginUiState = LoginUiState.Idle
        currentUser = null
        errorMessage = ""
    }
}