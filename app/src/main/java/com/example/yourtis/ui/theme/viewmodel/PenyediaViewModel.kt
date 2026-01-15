package com.example.yourtis.ui.theme.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yourtis.YourTisApplication


object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            AuthViewModel(yourTisApplication().container.yourTisRepository)
        }
        initializer {
            EntryViewModel(yourTisApplication().container.yourTisRepository)
        }
        // Inisialisasi PembeliViewModel sebagai shared viewmodel
        initializer {
            PembeliViewModel(yourTisApplication().container.yourTisRepository)
        }
        initializer {
            PetaniViewModel(yourTisApplication().container.yourTisRepository)
        }
    }
}

fun CreationExtras.yourTisApplication(): YourTisApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as YourTisApplication)