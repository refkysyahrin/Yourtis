package com.example.yourtis.ui.theme.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EntryViewModel(private val repository: YourTisRepository) : ViewModel() {

    // State Input Form
    var namaSayur by mutableStateOf("")
    var harga by mutableStateOf("")
    var stok by mutableStateOf("")
    var deskripsi by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null) // Menyimpan lokasi gambar di HP

    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Idle) // Pakai ulang state Loading/Success/Error

    fun insertSayur(context: Context) {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                if (imageUri == null) {
                    uiState = LoginUiState.Error // Gambar wajib ada
                    return@launch
                }

                // 1. Konversi URI (Galeri) ke File (Fisik sementara)
                val file = uriToFile(imageUri!!, context)

                // 2. Siapkan Gambar untuk Upload (Multipart)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("gambar", file.name, requestFile)

                // 3. Siapkan Data Teks (RequestBody)
                // ID Petani kita hardcode 1 dulu (Nanti bisa ambil dari session user yang login)
                val idPetaniRB = "1".toRequestBody("text/plain".toMediaTypeOrNull())
                val namaRB = namaSayur.toRequestBody("text/plain".toMediaTypeOrNull())
                val hargaRB = harga.toRequestBody("text/plain".toMediaTypeOrNull())
                val stokRB = stok.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiRB = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())

                // 4. Kirim ke Repository
                repository.insertSayur(
                    idPetani = idPetaniRB,
                    nama = namaRB,
                    harga = hargaRB,
                    stok = stokRB,
                    desc = deskripsiRB,
                    img = imagePart
                )

                uiState = LoginUiState.Success
            } catch (e: Exception) {
                uiState = LoginUiState.Error
                e.printStackTrace()
            }
        }
    }

    // --- HELPER FUNCTION: Mengubah Uri Galeri menjadi File ---
    private fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream.close()
        return myFile
    }
}