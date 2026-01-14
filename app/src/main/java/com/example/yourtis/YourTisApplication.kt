package com.example.yourtis

import android.app.Application
import com.example.yourtis.repositori.AppContainer
import com.example.yourtis.repositori.DefaultAppContainer

class YourTisApplication : Application() {

    // Container untuk Dependency Injection Manual
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi container saat aplikasi pertama kali dijalankan
        container = DefaultAppContainer()
    }
}