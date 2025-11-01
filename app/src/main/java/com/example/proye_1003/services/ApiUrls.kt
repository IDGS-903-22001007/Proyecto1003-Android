package com.example.proye_1003.services

/**
 * Helper para normalizar URLs de imagen (absolutas o relativas),
 * igual que en tu front de React.
 */
object ApiUrls {
    // Usa el mismo host que en RetrofitClient (SIN /api)
    private const val BASE = "https://api-farmacia.ngrok.app/"

    fun foto(src: String?): String {
        if (src.isNullOrBlank()) return "https://via.placeholder.com/320x220?text=Sin+Foto"
        return if (src.startsWith("http", ignoreCase = true)) src
        else BASE + src.trimStart('/')
    }
}
