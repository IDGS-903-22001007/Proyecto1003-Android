package com.example.proye_1003.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val user: String,
    val contrasena: String
)