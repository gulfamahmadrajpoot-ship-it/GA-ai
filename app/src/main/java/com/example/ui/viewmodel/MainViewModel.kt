package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CreationRepository
import com.example.data.model.CreationEntity
import com.example.data.model.MediaType
import com.example.data.service.GeneratedPromptResult
import com.example.data.service.ImageGenerationService
import com.example.data.service.ImageToVideoService
import com.example.data.service.MediaExportHelper
import com.example.data.service.PromptGenerationService
import com.example.data.service.VideoGenerationService
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = CreationRepository(db.creationDao())

    private val imageService = ImageGenerationService(application)
    private val videoService = VideoGenerationService(application)
    private val img2vidService = ImageToVideoService(application)
    private val promptService = PromptGenerationService()

    // Preferences & Settings
    private val _language = MutableStateFlow(AppLanguage.URDU)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // Profile & Credits
    val userName = MutableStateFlow("Gulfam Ahmad Rajpoot")
    val userBio = MutableStateFlow("Creative AI Director • GA AI Studio")
    val creditsRemaining = MutableStateFlow(85)
    val totalCredits = 100

    // Notification / Toast Events
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    // All Creations Flow
    val allCreations = repository.allCreations.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Image Generator UI State
    val imagePrompt = MutableStateFlow("A majestic lion adorned with gold filigree armor in a neon-lit futuristic city, cinematic bokeh, 8k uhd")
    val imageNegativePrompt = MutableStateFlow("blurry, low resolution, extra limbs, watermark")
    val imageAspectRatio = MutableStateFlow("1:1")
    val imageStyle = MutableStateFlow("Photorealistic")
    val referenceImageBitmap = MutableStateFlow<Bitmap?>(null)
    val isImageGenerating = MutableStateFlow(false)
    val lastGeneratedImage = MutableStateFlow<CreationEntity?>(null)

    // Video Generator UI State
    val videoPrompt = MutableStateFlow("Cinematic drone flight through glowing cybernetic canyon with bioluminescent waterfalls")
    val videoMotionType = MutableStateFlow("Cinematic Drone")
    val videoDuration = MutableStateFlow(5)
    val videoAspectRatio = MutableStateFlow("16:9")
    val img2vidBitmap = MutableStateFlow<Bitmap?>(null)
    val isVideoGenerating = MutableStateFlow(false)
    val lastGeneratedVideo = MutableStateFlow<CreationEntity?>(null)
    val activeVideoFrames = MutableStateFlow<List<File>>(emptyList())

    // Prompt Builder UI State
    val urduPromptInput = MutableStateFlow("لاہور کی قدیم تاریخی عمارتیں نیون بارش میں چمکتی ہوئی")
    val promptResult = MutableStateFlow<GeneratedPromptResult?>(null)
    val isEnhancingPrompt = MutableStateFlow(false)
    val activeChips = MutableStateFlow<Set<String>>(emptySet())

    // Creations Detail Dialog
    val selectedDetailCreation = MutableStateFlow<CreationEntity?>(null)
    val creationsFilter = MutableStateFlow("All")

    init {
        // Pre-populate with initial starter creation if database is completely empty
        viewModelScope.launch {
            if (db.creationDao().getById(1) == null) {
                // Generate a starter sample artwork
                imageService.generateImage(
                    prompt = "Futuristic GA AI Studio Logo with glowing golden calligraphy and violet neural circuits",
                    style = "Cyberpunk Neon",
                    aspectRatio = "1:1"
                ).onSuccess { file ->
                    repository.insert(
                        CreationEntity(
                            prompt = "Futuristic GA AI Studio Logo with glowing golden calligraphy and violet neural circuits",
                            urduPrompt = "مستقبل کی جدید جی اے اے آئی لوگو ڈیزائن سنہری خطاطی اور نیون سرکٹس کے ساتھ",
                            mediaType = MediaType.IMAGE,
                            mediaUrl = file.absolutePath,
                            aspectRatio = "1:1",
                            style = "Cyberpunk Neon",
                            isFavorite = true
                        )
                    )
                }
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
        viewModelScope.launch {
            _toastEvent.emit("API Key saved successfully")
        }
    }

    fun toggleChip(chip: String) {
        val current = activeChips.value.toMutableSet()
        if (current.contains(chip)) {
            current.remove(chip)
        } else {
            current.add(chip)
        }
        activeChips.value = current
    }

    fun generateImage() {
        val promptText = imagePrompt.value.trim()
        if (promptText.isEmpty()) {
            viewModelScope.launch { _toastEvent.emit("Please enter a prompt") }
            return
        }
        if (creditsRemaining.value <= 0) {
            viewModelScope.launch { _toastEvent.emit("Daily AI credits exhausted") }
            return
        }

        isImageGenerating.value = true
        viewModelScope.launch {
            val result = imageService.generateImage(
                prompt = promptText,
                style = imageStyle.value,
                aspectRatio = imageAspectRatio.value,
                negativePrompt = imageNegativePrompt.value,
                referenceBitmap = referenceImageBitmap.value,
                customApiKey = _customApiKey.value
            )

            result.onSuccess { file ->
                val entity = CreationEntity(
                    prompt = promptText,
                    urduPrompt = promptResult.value?.urduPrompt ?: "",
                    mediaType = MediaType.IMAGE,
                    mediaUrl = file.absolutePath,
                    aspectRatio = imageAspectRatio.value,
                    style = imageStyle.value
                )
                val id = repository.insert(entity)
                val savedEntity = entity.copy(id = id)
                lastGeneratedImage.value = savedEntity
                creditsRemaining.value = (creditsRemaining.value - 1).coerceAtLeast(0)
                _toastEvent.emit("✨ Image generated successfully!")
            }.onFailure { err ->
                _toastEvent.emit("Generation error: ${err.localizedMessage}")
            }
            isImageGenerating.value = false
        }
    }

    fun generateVideo() {
        val promptText = videoPrompt.value.trim()
        if (promptText.isEmpty()) {
            viewModelScope.launch { _toastEvent.emit("Please enter a video prompt") }
            return
        }
        if (creditsRemaining.value <= 1) {
            viewModelScope.launch { _toastEvent.emit("Need at least 2 credits for video") }
            return
        }

        isVideoGenerating.value = true
        viewModelScope.launch {
            val result = videoService.generateVideo(
                prompt = promptText,
                motionType = videoMotionType.value,
                durationSeconds = videoDuration.value,
                aspectRatio = videoAspectRatio.value,
                customApiKey = _customApiKey.value
            )

            result.onSuccess { res ->
                val entity = CreationEntity(
                    prompt = promptText,
                    urduPrompt = "",
                    mediaType = MediaType.VIDEO,
                    mediaUrl = res.mainFile.absolutePath,
                    aspectRatio = videoAspectRatio.value,
                    motionType = res.motionType,
                    durationSeconds = res.durationSeconds
                )
                val id = repository.insert(entity)
                val saved = entity.copy(id = id)
                lastGeneratedVideo.value = saved
                activeVideoFrames.value = res.frameFiles
                creditsRemaining.value = (creditsRemaining.value - 2).coerceAtLeast(0)
                _toastEvent.emit("🎬 Video rendered successfully!")
            }.onFailure { err ->
                _toastEvent.emit("Video error: ${err.localizedMessage}")
            }
            isVideoGenerating.value = false
        }
    }

    fun animateImageToVideo() {
        val bmp = img2vidBitmap.value ?: referenceImageBitmap.value
        if (bmp == null) {
            viewModelScope.launch { _toastEvent.emit("Please pick a reference photo to animate") }
            return
        }

        isVideoGenerating.value = true
        viewModelScope.launch {
            val result = img2vidService.animateImageToVideo(
                referenceBitmap = bmp,
                motionPrompt = videoPrompt.value.ifBlank { "Dynamic cinematic camera motion" },
                cameraMotion = videoMotionType.value,
                durationSeconds = videoDuration.value,
                customApiKey = _customApiKey.value
            )

            result.onSuccess { res ->
                val entity = CreationEntity(
                    prompt = "Animated Photo: " + videoPrompt.value,
                    urduPrompt = "",
                    mediaType = MediaType.VIDEO,
                    mediaUrl = res.mainFile.absolutePath,
                    aspectRatio = "1:1",
                    motionType = res.motionType,
                    durationSeconds = res.durationSeconds
                )
                val id = repository.insert(entity)
                val saved = entity.copy(id = id)
                lastGeneratedVideo.value = saved
                activeVideoFrames.value = res.frameFiles
                creditsRemaining.value = (creditsRemaining.value - 2).coerceAtLeast(0)
                _toastEvent.emit("🎬 Photo animated into video successfully!")
            }.onFailure { err ->
                _toastEvent.emit("Animation error: ${err.localizedMessage}")
            }
            isVideoGenerating.value = false
        }
    }

    fun enhanceUrduPrompt() {
        val input = urduPromptInput.value.trim()
        if (input.isEmpty()) return

        isEnhancingPrompt.value = true
        viewModelScope.launch {
            val res = promptService.enhanceOrTranslatePrompt(input, _customApiKey.value)
            promptResult.value = res
            isEnhancingPrompt.value = false
            _toastEvent.emit("✓ Prompt enhanced with Gemini AI")
        }
    }

    fun toggleFavorite(creation: CreationEntity) {
        viewModelScope.launch {
            val updated = !creation.isFavorite
            repository.toggleFavorite(creation.id, updated)
            if (selectedDetailCreation.value?.id == creation.id) {
                selectedDetailCreation.value = creation.copy(isFavorite = updated)
            }
        }
    }

    fun deleteCreation(creation: CreationEntity) {
        viewModelScope.launch {
            repository.delete(creation)
            if (selectedDetailCreation.value?.id == creation.id) {
                selectedDetailCreation.value = null
            }
            _toastEvent.emit("Creation deleted")
        }
    }

    fun regenerate(creation: CreationEntity) {
        if (creation.mediaType == MediaType.IMAGE) {
            imagePrompt.value = creation.prompt
            imageStyle.value = creation.style
            imageAspectRatio.value = creation.aspectRatio
            generateImage()
        } else {
            videoPrompt.value = creation.prompt
            videoMotionType.value = creation.motionType
            videoDuration.value = creation.durationSeconds
            generateVideo()
        }
    }

    fun sendPromptToImageGenerator(prompt: String) {
        imagePrompt.value = prompt
        viewModelScope.launch { _toastEvent.emit("Sent to Image Generator") }
    }

    fun sendPromptToVideoGenerator(prompt: String) {
        videoPrompt.value = prompt
        viewModelScope.launch { _toastEvent.emit("Sent to Video Generator") }
    }

    fun saveCreationToGallery(context: Context, creation: CreationEntity) {
        viewModelScope.launch {
            val file = File(creation.mediaUrl)
            if (!file.exists()) {
                _toastEvent.emit("Media file not found")
                return@launch
            }
            val ok = if (creation.mediaType == MediaType.VIDEO) {
                MediaExportHelper.saveVideoToGallery(context, file, creation.prompt)
            } else {
                MediaExportHelper.saveImageToGallery(context, file, creation.prompt)
            }
            if (ok) {
                _toastEvent.emit("✓ Saved to device gallery!")
            } else {
                _toastEvent.emit("Failed to save to gallery")
            }
        }
    }

    fun shareCreation(context: Context, creation: CreationEntity) {
        val file = File(creation.mediaUrl)
        if (file.exists()) {
            MediaExportHelper.shareCreation(
                context,
                file,
                creation.prompt,
                creation.mediaType == MediaType.VIDEO
            )
        } else {
            viewModelScope.launch { _toastEvent.emit("Media file not found for sharing") }
        }
    }

    fun clearCache(context: Context) {
        viewModelScope.launch {
            val dir = File(context.filesDir, "ga_ai_creations")
            if (dir.exists()) {
                dir.deleteRecursively()
                dir.mkdirs()
            }
            _toastEvent.emit("Studio cache cleared")
        }
    }

    // Admin & App Distribution Functions
    val isAdmin = MutableStateFlow(true)
    val adminTitle = MutableStateFlow("Master Admin & Founder")

    fun grantUnlimitedCredits() {
        creditsRemaining.value = 99999
        viewModelScope.launch {
            _toastEvent.emit("✨ Unlimited Credits Activated (∞) for Master Admin!")
        }
    }

    fun addCredits(amount: Int) {
        creditsRemaining.value += amount
        viewModelScope.launch {
            _toastEvent.emit("✓ Added $amount Credits successfully!")
        }
    }

    fun downloadAppApk(context: Context, onComplete: (String?) -> Unit = {}) {
        viewModelScope.launch {
            val savedPath = MediaExportHelper.exportAppApkToDownloads(context)
            if (savedPath != null) {
                _toastEvent.emit("✓ APK saved to $savedPath")
            } else {
                _toastEvent.emit("Could not export APK on this device")
            }
            onComplete(savedPath)
        }
    }

    fun shareAppWithFriends(context: Context) {
        viewModelScope.launch {
            val shared = MediaExportHelper.shareAppApk(context)
            if (!shared) {
                try {
                    val shareText = """
                        🌟 GA AI - Next-Gen Creative AI Studio 🌟
                        بنائیں شاندار تصاویر، ویڈیوز اور اردو پرامپٹس مصنوعی ذہانت کے ساتھ!
                        
                        Created by Gulfam Ahmad Rajpoot (Founder & Master Admin)
                        
                        ✨ Features:
                        • 🎨 AI Image Generator (Photorealistic, Mughal Art, Cyberpunk, 3D)
                        • 🎬 AI Video Generator & Image to Video Animation
                        • ✍️ Urdu Prompt Generator (اردو سے اسمارٹ پرامپٹس)
                        • 📂 Offline 'My Creations' Studio Gallery
                    """.trimIndent()

                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "GA AI Creative Studio - Android App")
                        putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    val chooser = android.content.Intent.createChooser(intent, "Share GA AI with Friends")
                    chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooser)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
