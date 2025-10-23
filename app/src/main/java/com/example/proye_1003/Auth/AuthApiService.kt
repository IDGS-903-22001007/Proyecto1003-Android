package com.example.proye_1003.Auth

import com.example.proye_1003.models.LoginRequest
import com.example.proye_1003.models.RegisterRequest
import com.example.proye_1003.models.RegisterResponse
import com.example.proye_1003.models.Users
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @GET("Usuarios")
    suspend fun getUsuarios(): Response<List<Users>>

    @POST("Auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Users>

    @POST("Usuarios")
    suspend fun registerUsuario(@Body usuario: RegisterRequest): Response<Users>
}

