package com.example.yourtis.ui.theme.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.User
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream


class EntryViewModel(private val repository: YourTisRepository) : ViewModel() {

    var uiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    var namaSayur by mutableStateOf("")
    var harga by mutableStateOf("")
    var stok by mutableStateOf("")
    var deskripsi by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)

    // Menyimpan ID Petani yang login
    var currentPetaniId by mutableStateOf(0)

    private var currentIdSayur: Int? = null

    fun resetForm() {
        currentIdSayur = null
        namaSayur = ""
        harga = ""
        stok = ""
        deskripsi = ""
        imageUri = null
        uiState = LoginUiState.Idle
    }

    fun loadDataForEdit(id: Int) {
        if (id == -1) {
            resetForm()
            return
        }
        
        currentIdSayur = id
        viewModelScope.launch {
            try {
                val sayur = repository.getSayurById(id)
                namaSayur = sayur.nama_sayur
                harga = sayur.harga.toString()
                stok = sayur.stok.toString()
                deskripsi = sayur.deskripsi
                uiState = LoginUiState.Idle
            } catch (e: Exception) {
                Log.e("EntryViewModel", "Gagal load data edit: ${e.message}")
            }
        }
    }

    fun saveSayur(context: Context) {
        if (namaSayur.isBlank() || harga.isBlank() || stok.isBlank() || deskripsi.isBlank()) {
            Toast.makeText(context, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentIdSayur == null && imageUri == null) {
            Toast.makeText(context, "Foto sayur wajib diupload untuk produk baru!", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentPetaniId == 0) {
            Toast.makeText(context, "Sesi petani tidak valid. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                val namaReq = namaSayur.toRequestBody("text/plain".toMediaTypeOrNull())
                val hargaReq = harga.toRequestBody("text/plain".toMediaTypeOrNull())
                val stokReq = stok.toRequestBody("text/plain".toMediaTypeOrNull())
                val descReq = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                if (imageUri != null) {
                    val file = uriToFile(imageUri!!, context)
                    val reqFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("gambar", file.name, reqFile)
                }

                if (currentIdSayur == null) {
                    // MODE INSERT
                    val idPetaniReq = currentPetaniId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    repository.insertSayur(idPetaniReq, namaReq, hargaReq, stokReq, descReq, imagePart!!)

                    Toast.makeText(context, "Berhasil menambah produk!", Toast.LENGTH_SHORT).show()
                    uiState = LoginUiState.Success(User(currentPetaniId, "", "", "", "", ""))
                } else {
                    // MODE UPDATE
                    repository.updateSayur(currentIdSayur!!, namaReq, hargaReq, stokReq, descReq, imagePart)

                    Toast.makeText(context, "Berhasil update produk!", Toast.LENGTH_SHORT).show()
                    uiState = LoginUiState.Success(User(currentPetaniId, "", "", "", "", ""))
                }

            } catch (e: Exception) {
                Log.e("EntryViewModel", "Error Simpan: ${e.message}")
                Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
                uiState = LoginUiState.Error
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        val inputStream = contentResolver.openInputStream(uri) ?: throw Exception("Gagal membuka gambar")
        val outputStream = FileOutputStream(myFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return myFile
    }
}