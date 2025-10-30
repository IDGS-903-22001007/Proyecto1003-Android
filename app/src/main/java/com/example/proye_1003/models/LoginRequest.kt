package com.example.proye_1003.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("user") val user: String,
    @SerializedName("contrasena") val contrasena: String
)
