package com.example.yourtis.repositori

import com.example.yourtis.service.YourTisApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val yourTisRepository: YourTisRepository
}

class DefaultAppContainer : AppContainer {

    private val baseUrl = "http://10.0.2.2:3000/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: YourTisApiService by lazy {
        retrofit.create(YourTisApiService::class.java)
    }

    override val yourTisRepository: YourTisRepository by lazy {
        NetworkYourTisRepository(retrofitService)
    }
}