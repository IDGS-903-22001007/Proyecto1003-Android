package com.example.proye_1003.services



import com.example.proye_1003.models.LoginRequest
import com.example.proye_1003.models.Users
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface AuthApiService {
    @POST("Usuarios/login") // asumiendo que el endpoint de login es así
    suspend fun login(@Body request: LoginRequest): Response<Users>

    @GET("Usuarios")
    suspend fun getUsuarios(): Response<List<Users>>
}
