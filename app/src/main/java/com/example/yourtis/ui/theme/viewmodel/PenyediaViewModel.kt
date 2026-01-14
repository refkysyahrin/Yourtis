package com.example.yourtis.ui.theme.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yourtis.YourTisApplication

object PenyediaViewModel {
    val Factory = viewModelFactory {

        // 1. Auth ViewModel (Login & Register)
        initializer {
            AuthViewModel(aplikasiYourTis().container.yourTisRepository)
        }

        // 2. Petani ViewModel (Dashboard: List & Delete)
        initializer {
            PetaniViewModel(aplikasiYourTis().container.yourTisRepository)
        }

        // 3. Entry ViewModel (Tambah Sayur & Upload Gambar) -> BARU DITAMBAHKAN
        initializer {
            EntryViewModel(aplikasiYourTis().container.yourTisRepository)
        }

        // 4. Pembeli ViewModel (Katalog Belanja)
        initializer {
            PembeliViewModel(aplikasiYourTis().container.yourTisRepository)
        }
    }
}

// Extension function untuk akses Application Container
fun CreationExtras.aplikasiYourTis(): YourTisApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as YourTisApplication)