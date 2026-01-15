package com.example.yourtis.service

import com.example.yourtis.modeldata.LoginResponse
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.Transaksi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface YourTisApiService {

    // --- AUTH ---
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: Map<String, String>): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: Map<String, String>): Map<String, Any>

    // --- PRODUK ---
    @GET("api/products")
    suspend fun getAllSayur(): List<Sayur>

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

    @Multipart
    @PUT("api/products/{id}")
    suspend fun updateSayur(
        @Path("id") id: Int,
        @Part("nama_sayur") nama: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("stok") stok: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part gambar: MultipartBody.Part?
    ): Map<String, Any>

    @DELETE("api/products/{id}")
    suspend fun deleteSayur(@Path("id") id: Int): Map<String, Any>

    // --- TRANSAKSI ---
    // Menggunakan Response agar bisa mengecek isSuccessful tanpa error parsing
    @POST("api/transactions/checkout")
    suspend fun checkout(@Body transactionData: Map<String, Any>): Response<ResponseBody>

    @GET("api/transactions")
    suspend fun getAllTransaksi(): List<Transaksi>

    @PUT("api/transactions/{id}")
    suspend fun updateStatusTransaksi(
        @Path("id") idTransaksi: String,
        @Body statusData: Map<String, String>
    ): Map<String, String>
}
