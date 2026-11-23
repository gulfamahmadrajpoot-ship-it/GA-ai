package com.example.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    fun getEffectiveApiKey(customKey: String?): String {
        val key = customKey?.trim()
        if (!key.isNullOrEmpty()) return key
        return try {
            val buildConfigKey = BuildConfig.GEMINI_API_KEY
            if (buildConfigKey.isNotBlank() && buildConfigKey != "MY_GEMINI_API_KEY") {
                buildConfigKey
            } else ""
        } catch (e: Throwable) {
            ""
        }
    }

    suspend fun callGenerateContent(
        model: String,
        apiKey: String,
        prompt: String,
        systemInstruction: String? = null,
        referenceBitmap: Bitmap? = null,
        isImageGeneration: Boolean = false,
        aspectRatio: String = "1:1"
    ): JSONObject = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/models/$model:generateContent?key=$apiKey"
        val requestJson = JSONObject()

        // Contents
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()

        // Text Part
        val textPart = JSONObject()
        textPart.put("text", prompt)
        partsArray.put(textPart)

        // Reference Image Part if provided
        referenceBitmap?.let { bmp ->
            val inlineData = JSONObject()
            inlineData.put("mimeType", "image/jpeg")
            inlineData.put("data", bmp.toBase64())
            val imgPart = JSONObject()
            imgPart.put("inlineData", inlineData)
            partsArray.put(imgPart)
        }

        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        requestJson.put("contents", contentsArray)

        // System Instruction if provided
        systemInstruction?.let { sysText ->
            val sysObj = JSONObject()
            val sysParts = JSONArray()
            val p = JSONObject()
            p.put("text", sysText)
            sysParts.put(p)
            sysObj.put("parts", sysParts)
            requestJson.put("systemInstruction", sysObj)
        }

        // Generation Config
        val genConfig = JSONObject()
        if (isImageGeneration) {
            val imageConfig = JSONObject()
            imageConfig.put("aspectRatio", aspectRatio)
            imageConfig.put("imageSize", "1K")
            genConfig.put("imageConfig", imageConfig)
            val modalities = JSONArray()
            modalities.put("TEXT")
            modalities.put("IMAGE")
            genConfig.put("responseModalities", modalities)
        }
        requestJson.put("generationConfig", genConfig)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            throw Exception("API Error (${response.code}): $responseBody")
        }
        JSONObject(responseBody)
    }

    fun Bitmap.toBase64(quality: Int = 85): String {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    fun saveBase64ToFile(context: Context, base64Data: String, extension: String = "jpg"): File {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        val dir = File(context.filesDir, "ga_ai_creations").apply { if (!exists()) mkdirs() }
        val file = File(dir, "ga_ai_${UUID.randomUUID()}.$extension")
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, extension: String = "jpg"): File {
        val dir = File(context.filesDir, "ga_ai_creations").apply { if (!exists()) mkdirs() }
        val file = File(dir, "ga_ai_${UUID.randomUUID()}.$extension")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        return file
    }
}
