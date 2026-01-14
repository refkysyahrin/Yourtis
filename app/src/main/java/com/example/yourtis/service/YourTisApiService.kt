package com.example.yourtis.service

import com.example.yourtis.modeldata.LoginResponse
import com.example.yourtis.modeldata.Sayur
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface YourTisApiService {

    // --- OTENTIKASI (AUTH) ---

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: Map<String, String>): LoginResponse

    // Menggunakan Map agar bisa mengirim password tanpa mengubah model User
    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: Map<String, String>): Map<String, Any>


    // --- MANAJEMEN PRODUK (SAYUR) ---

    // 1. Ambil Semua Data (Katalog)
    @GET("api/products")
    suspend fun getAllSayur(): List<Sayur>

    // 2. Tambah Sayur (Multipart untuk Upload Gambar)
    @Multipart
    @POST("api/products")
    suspend fun insertSayur(
        @Part("id_petani") idPetani: RequestBody,
        @Part("nama_sayur") namaSayur: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("stok") stok: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part gambar: MultipartBody.Part
    ): Map<String, Any>

    // 3. Hapus Sayur
    @DELETE("api/products/{id}")
    suspend fun deleteSayur(@Path("id") id: Int): Map<String, Any>


    // --- TRANSAKSI (CHECKOUT) ---

    @POST("api/transactions/checkout")
    suspend fun checkout(@Body transactionData: Map<String, Any>): Map<String, Any>

    // AMBIL DATA TRANSAKSI
    @GET("api/transactions")
    suspend fun getAllTransaksi(): List<com.example.yourtis.modeldata.Transaksi>
}