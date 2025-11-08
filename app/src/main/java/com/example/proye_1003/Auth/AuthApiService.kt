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

    // GET /api/Usuarios
    @GET("api/Usuarios")
    suspend fun getUsuarios(): Response<List<Users>>

    // POST /api/Auth/login
    // Body: { "user": "...", "contrasena": "..." }
    @POST("api/Auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Users>
    // Si tu API NO devuelve un objeto Users (solo 200/OK),
    // cambia el retorno a: Response<Unit>

    // POST /api/Usuarios
    // Ajusta el tipo de respuesta según tu backend:
    // - Si devuelve el usuario creado: Response<Users>
    // - Si devuelve un wrapper: Response<RegisterResponse>
    @POST("api/Usuarios")
    suspend fun registerUsuario(@Body usuario: RegisterRequest): Response<Users>
    // Si realmente devuelve el Users creado, usa:
    // suspend fun registerUsuario(@Body usuario: RegisterRequest): Response<Users>
}
