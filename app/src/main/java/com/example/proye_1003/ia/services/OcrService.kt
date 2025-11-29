package com.example.proye_1003.ia.services

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class OCRService {

    suspend fun procesarImagen(bitmap: Bitmap): String {

        val image = InputImage.fromBitmap(bitmap, 0)

        val recognizer = TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

        val result = recognizer.process(image).await()

        return result.text
    }
}
