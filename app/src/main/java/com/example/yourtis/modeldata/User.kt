package com.example.yourtis.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id_user: Int,
    val username: String,
    val email: String,
    val role: String, // "Petani" atau "Pembeli"
    val no_hp: String,
    val alamat: String
)