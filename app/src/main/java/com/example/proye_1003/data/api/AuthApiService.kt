package com.example.proye_1003.data.api

import com.example.proye_1003.data.model.LoginRequest
import com.example.proye_1003.data.model.LoginResponse
import com.example.proye_1003.data.model.RegisterRequest
import com.example.proye_1003.data.model.RegisterResponse
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