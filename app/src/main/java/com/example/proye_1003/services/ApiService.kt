package com.example.proye_1003.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.proye_1003.models.Users
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST

class ApiService : Service() {


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}