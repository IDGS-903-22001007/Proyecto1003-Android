package com.example.proye_1003

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
        @POST("api/Auth/login")
        suspend fun login(
            @Body request: LoginRequest
        ): Response<LoginResponse>

        @POST("api/Usuarios")
        suspend fun registerUser(
            @Body request: RegisterRequest
        ): Response<RegisterResponse>
    }