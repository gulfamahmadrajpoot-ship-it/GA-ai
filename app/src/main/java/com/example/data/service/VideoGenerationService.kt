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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.random.Random

data class VideoRenderResult(
    val mainFile: File,
    val frameFiles: List<File>,
    val durationSeconds: Int,
    val motionType: String
)

class VideoGenerationService(private val context: Context) {

    suspend fun generateVideo(
        prompt: String,
        motionType: String,
        durationSeconds: Int,
        aspectRatio: String,
        customApiKey: String? = null
    ): Result<VideoRenderResult> = withContext(Dispatchers.IO) {
        try {
            // Emulate progressive render stages with real visual frames
            val frames = mutableListOf<File>()
            val frameCount = (durationSeconds * 6).coerceIn(18, 48) // 6 fps for smooth animated preview
            val (width, height) = if (aspectRatio == "9:16") Pair(540, 960) else Pair(960, 540)

            val seed = prompt.hashCode().toLong()
            val rng = Random(seed)

            // Dynamic color grading for motion types
            val (grad1, grad2, accent) = when (motionType.lowercase()) {
                "sci-fi vfx" -> Triple(Color.parseColor("#060A17"), Color.parseColor("#172554"), Color.parseColor("#06B6D4"))
                "hyperlapse" -> Triple(Color.parseColor("#180018"), Color.parseColor("#4A0E4E"), Color.parseColor("#F72585"))
                "slow motion" -> Triple(Color.parseColor("#0A192F"), Color.parseColor("#203A43"), Color.parseColor("#2EC4B6"))
                "dynamic anime" -> Triple(Color.parseColor("#1F0038"), Color.parseColor("#3C096C"), Color.parseColor("#FF9E00"))
                else -> Triple(Color.parseColor("#0F172A"), Color.parseColor("#1E1B4B"), Color.parseColor("#F59E0B")) // Cinematic Drone
            }

            for (frameIdx in 0 until frameCount) {
                val progress = frameIdx.toFloat() / frameCount.toFloat()
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)

                // Background movement with progress
                val shiftX = width * (progress * 0.35f)
                val shader = LinearGradient(
                    shiftX, 0f, width + shiftX, height.toFloat(),
                    intArrayOf(grad1, grad2, grad1),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.MIRROR
                )
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                // Motion effects: Dynamic particles and streaks
                paint.shader = null
                paint.color = accent
                paint.strokeCap = Paint.Cap.ROUND
                for (p in 0..18) {
                    val pRng = Random(seed + p * 31)
                    val basePx = pRng.nextFloat() * width
                    val basePy = pRng.nextFloat() * height
                    val pSpeed = 1.0f + pRng.nextFloat() * 2.5f

                    val curPx = (basePx + (progress * width * pSpeed)) % width
                    val curPy = (basePy + (progress * 60f)) % height

                    paint.strokeWidth = 3f + (p % 4) * 2f
                    paint.alpha = (90 + (p * 8) % 150)

                    val streakLength = 35f * pSpeed
                    canvas.drawLine(curPx - streakLength, curPy, curPx, curPy, paint)
                }

                // Cinematic camera zoom & motion pulse
                val cameraZoom = 1.0f + (progress * 0.22f)
                val centerX = width / 2f
                val centerY = height / 2f

                val coreRadius = (width.coerceAtMost(height) * 0.22f) * cameraZoom
                val radialShader = RadialGradient(
                    centerX, centerY, coreRadius * 1.4f,
                    intArrayOf(accent, Color.TRANSPARENT),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP
                )
                paint.shader = radialShader
                canvas.drawCircle(centerX, centerY, coreRadius * 1.4f, paint)

                // Central cinematic emblem
                paint.shader = null
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
                paint.color = Color.WHITE
                paint.alpha = 210
                canvas.drawCircle(centerX, centerY, coreRadius * 0.7f, paint)

                // HUD overlay
                paint.style = Paint.Style.FILL
                paint.textSize = 24f
                paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                paint.color = Color.WHITE
                paint.alpha = 200

                val timeCode = String.format("00:%02d / 00:%02d", (progress * durationSeconds).toInt(), durationSeconds)
                canvas.drawText("REC [●] $timeCode • $motionType", 30f, 50f, paint)

                val displayPrompt = if (prompt.length > 40) prompt.take(37) + "..." else prompt
                paint.textSize = 22f
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                paint.alpha = 180
                canvas.drawText("GA AI VIDEO • \"$displayPrompt\"", 30f, height - 30f, paint)

                val frameFile = GeminiClient.saveBitmapToFile(context, bitmap, "jpg")
                frames.add(frameFile)
            }

            val mainThumbnail = frames.firstOrNull() ?: frames[0]
            Result.success(
                VideoRenderResult(
                    mainFile = mainThumbnail,
                    frameFiles = frames,
                    durationSeconds = durationSeconds,
                    motionType = motionType
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
