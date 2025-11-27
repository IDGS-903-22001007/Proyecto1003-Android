package com.example.proye_1003.Auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proye_1003.services.GeminiService

class ChatViewModelFactory(
    private val geminiService: GeminiService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(geminiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
