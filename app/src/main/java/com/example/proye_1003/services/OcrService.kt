// services/OCRService.kt
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI



class OCRService(private val dataPath: String) {
    private val tess = TessBaseAPI().apply { init(dataPath, "spa") }

    fun procesarImagen(bitmap: Bitmap): String {
        tess.setImage(bitmap)
        val resultado = tess.utF8Text
        tess.clear()
        return resultado
    }
}