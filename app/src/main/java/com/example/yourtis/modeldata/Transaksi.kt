package com.example.yourtis.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class Transaksi(
    val id_transaksi: String, // Format "TRX-timestamp"
    val id_pembeli: Int,
    val total_bayar: Int,
    val metode_kirim: String, // "Pickup" atau "Delivery"
    val metode_bayar: String, // "Transfer" atau "COD"
    val status: String,       // "Pending" atau "Selesai"
    val tgl_transaksi: String? = null // Opsional, dikirim server
)