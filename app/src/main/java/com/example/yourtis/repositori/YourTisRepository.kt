package com.example.yourtis.repositori

import com.example.yourtis.modeldata.LoginResponse
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.modeldata.User
import com.example.yourtis.service.YourTisApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody


interface YourTisRepository {
    suspend fun login(email: String, kataSandi: String): LoginResponse
    suspend fun register(user: User, kataSandi: String)
    suspend fun getSayur(): List<Sayur>
    suspend fun getSayurById(id: Int): Sayur
    suspend fun insertSayur(idPetani: RequestBody, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part)
    suspend fun updateSayur(id: Int, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part?)
    suspend fun deleteSayur(id: Int)
    suspend fun checkout(data: Map<String, Any>)
    suspend fun getAllTransaksi(): List<Transaksi>
    suspend fun getTransaksiByPembeli(idPembeli: Int): List<Transaksi>
    suspend fun updateStatusTransaksi(idTransaksi: String, status: String)
}

class NetworkYourTisRepository(
    private val yourTisApiService: YourTisApiService
) : YourTisRepository {

    override suspend fun login(email: String, kataSandi: String): LoginResponse =
        yourTisApiService.login(mapOf("email" to email, "password" to kataSandi))

    override suspend fun register(user: User, kataSandi: String) {
        yourTisApiService.register(mapOf(
            "username" to user.username, "email" to user.email,
            "password" to kataSandi, "role" to user.role,
            "no_hp" to user.no_hp, "alamat" to user.alamat
        ))
    }

    override suspend fun getSayur(): List<Sayur> = yourTisApiService.getAllSayur()

    override suspend fun getSayurById(id: Int): Sayur {
        return yourTisApiService.getAllSayur().find { it.id_sayur == id } ?: throw Exception("Not Found")
    }

    override suspend fun insertSayur(idPetani: RequestBody, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part) {
        yourTisApiService.insertSayur(idPetani, nama, harga, stok, desc, img)
    }

    override suspend fun updateSayur(id: Int, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part?) {
        yourTisApiService.updateSayur(id, nama, harga, stok, desc, img)
    }

    override suspend fun deleteSayur(id: Int) = yourTisApiService.deleteSayur(id).let { Unit }

    // ✅ PERBAIKAN: Improved error handling untuk checkout
    override suspend fun checkout(data: Map<String, Any>) {
        try {
            val response = yourTisApiService.checkout(data)

            // Check if response is successful
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                throw Exception("Checkout Gagal: HTTP ${response.code()} - $errorBody")
            }

            // Additional validation: check if response has body
            if (response.body() == null) {
                throw Exception("Checkout Response kosong dari server")
            }

        } catch (e: java.net.ConnectException) {
            throw Exception("Gagal terhubung ke server. Periksa koneksi internet.")
        } catch (e: java.net.SocketTimeoutException) {
            throw Exception("Koneksi timeout. Coba lagi dalam beberapa saat.")
        } catch (e: Exception) {
            throw Exception("Checkout error: ${e.message}")
        }
    }

    override suspend fun getAllTransaksi(): List<Transaksi> = yourTisApiService.getAllTransaksi()

    override suspend fun getTransaksiByPembeli(idPembeli: Int): List<Transaksi> = 
        yourTisApiService.getAllTransaksi().filter { it.id_pembeli == idPembeli }

    override suspend fun updateStatusTransaksi(idTransaksi: String, status: String) {
        yourTisApiService.updateStatusTransaksi(idTransaksi, mapOf("status" to status))
    }
}
