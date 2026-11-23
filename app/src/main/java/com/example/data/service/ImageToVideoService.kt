package com.example.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.sin

class ImageToVideoService(private val context: Context) {

    suspend fun animateImageToVideo(
        referenceBitmap: Bitmap,
        motionPrompt: String,
        cameraMotion: String, // "Zoom In", "Pan Right", "Orbit 360", "Tilt Up", "Slow Motion Drift"
        durationSeconds: Int = 5,
        customApiKey: String? = null
    ): Result<VideoRenderResult> = withContext(Dispatchers.IO) {
        try {
            val frameCount = (durationSeconds * 6).coerceIn(18, 48)
            val width = 720
            val height = 720
            val frames = mutableListOf<File>()

            for (i in 0 until frameCount) {
                val t = i.toFloat() / frameCount.toFloat()
                val frameBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(frameBmp)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)

                // Compute transformation based on cameraMotion
                val matrix = Matrix()
                val scale = when (cameraMotion) {
                    "Zoom In" -> 1.0f + (t * 0.28f)
                    "Zoom Out" -> 1.28f - (t * 0.28f)
                    else -> 1.15f + (sin(t * Math.PI.toFloat()) * 0.08f)
                }

                val dx = when (cameraMotion) {
                    "Pan Right" -> -t * 60f
                    "Pan Left" -> t * 60f
                    else -> sin(t * Math.PI.toFloat() * 2f) * 20f
                }

                val dy = when (cameraMotion) {
                    "Tilt Up" -> t * 50f
                    "Tilt Down" -> -t * 50f
                    else -> 0f
                }

                val rotation = when (cameraMotion) {
                    "Orbit 360" -> (t * 12f) - 6f
                    else -> sin(t * Math.PI.toFloat()) * 2f
                }

                // Center crop & scale referenceBitmap
                val srcWidth = referenceBitmap.width.toFloat()
                val srcHeight = referenceBitmap.height.toFloat()
                val baseScale = (width / srcWidth).coerceAtLeast(height / srcHeight)

                matrix.postScale(baseScale * scale, baseScale * scale)
                matrix.postRotate(rotation, width / 2f, height / 2f)
                matrix.postTranslate(dx, dy)

                canvas.drawBitmap(referenceBitmap, matrix, paint)

                // Atmospheric lighting sweep
                val sweepX = width * t
                val lightShader = LinearGradient(
                    sweepX - 200f, 0f, sweepX + 200f, height.toFloat(),
                    intArrayOf(Color.TRANSPARENT, Color.argb(60, 255, 215, 0), Color.TRANSPARENT),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
                paint.shader = lightShader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                // HUD overlay
                paint.shader = null
                paint.textSize = 22f
                paint.color = Color.WHITE
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                paint.alpha = 210

                val timeCode = String.format("IMG2VID 00:%02d • %s", (t * durationSeconds).toInt(), cameraMotion)
                canvas.drawText(timeCode, 24f, 44f, paint)

                paint.textSize = 18f
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                paint.alpha = 180
                val displayMotion = if (motionPrompt.length > 35) motionPrompt.take(32) + "..." else motionPrompt
                canvas.drawText("MOTION: $displayMotion", 24f, height - 24f, paint)

                val file = GeminiClient.saveBitmapToFile(context, frameBmp, "jpg")
                frames.add(file)
            }

            val mainFile = frames.firstOrNull() ?: frames[0]
            Result.success(
                VideoRenderResult(
                    mainFile = mainFile,
                    frameFiles = frames,
                    durationSeconds = durationSeconds,
                    motionType = cameraMotion
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
