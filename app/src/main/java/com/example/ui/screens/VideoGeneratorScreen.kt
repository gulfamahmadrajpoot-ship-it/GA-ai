package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.BadgeChip
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.GaGold
import com.example.ui.theme.GaViolet
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun VideoGeneratorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val videoPrompt by viewModel.videoPrompt.collectAsState()
    val selectedMotion by viewModel.videoMotionType.collectAsState()
    val selectedDuration by viewModel.videoDuration.collectAsState()
    val selectedAspectRatio by viewModel.videoAspectRatio.collectAsState()
    val img2vidBitmap by viewModel.img2vidBitmap.collectAsState()
    val isGenerating by viewModel.isVideoGenerating.collectAsState()
    val lastVideo by viewModel.lastGeneratedVideo.collectAsState()
    val activeFrames by viewModel.activeVideoFrames.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Text to Video, 1: Image to Video
    var isPlaying by remember { mutableStateOf(true) }
    var currentFrameIndex by remember { mutableIntStateOf(0) }

    // Video Player Frame Cycling Engine
    LaunchedEffect(isPlaying, activeFrames) {
        if (isPlaying && activeFrames.isNotEmpty()) {
            while (true) {
                delay(120) // ~8 fps playback
                currentFrameIndex = (currentFrameIndex + 1) % activeFrames.size
            }
        }
    }

    // Photo picker for Image to Video
    val img2vidPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                viewModel.img2vidBitmap.value = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val motions = listOf(
        "Cinematic Drone",
        "Slow Motion",
        "Hyperlapse",
        "Sci-Fi VFX",
        "Dynamic Anime",
        "Zoom In",
        "Pan Right",
        "Orbit 360"
    )
    val durations = listOf(3, 5, 8)
    val ratios = listOf("16:9", "9:16")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Screen Title
        Text(
            text = AppStrings.get(language, "video_generator_title"),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mode Switcher Tabs (Text to Video vs Image to Video)
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = GaGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = GaGold
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Text(
                        text = AppStrings.get(language, "tab_text_to_video"),
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Text(
                        text = AppStrings.get(language, "tab_image_to_video"),
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Image to Video Photo Picker Area
        if (activeTab == 1) {
            Text(
                text = "Select Photo to Animate",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (img2vidBitmap != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                bitmap = img2vidBitmap!!.asImageBitmap(),
                                contentDescription = "To Animate",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Ready to Animate",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Select camera motion & duration below",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.img2vidBitmap.value = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFFEF4444))
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = {
                        img2vidPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("pick_img2vid_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Select Image")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Choose Photo from Gallery", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Video Prompt Input
        Text(
            text = if (activeTab == 0) "Video Scene Prompt" else "Motion Description / Camera Directions",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = videoPrompt,
            onValueChange = { viewModel.videoPrompt.value = it },
            placeholder = {
                Text(
                    text = if (activeTab == 0) "Describe motion, camera flight, atmosphere in Urdu or English..." else "e.g., Gentle camera zoom in, wind blowing, dramatic lighting...",
                    fontSize = 13.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("video_prompt_input"),
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GaViolet,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Motion Styles
        Text(
            text = AppStrings.get(language, "motion_type"),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            motions.forEach { motion ->
                val isSelected = selectedMotion == motion
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) GaGold else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { viewModel.videoMotionType.value = motion }
                        .testTag("motion_$motion")
                ) {
                    Text(
                        text = motion,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Duration & Aspect Ratio Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Duration selector
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = AppStrings.get(language, "duration"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    durations.forEach { dur ->
                        val isSelected = selectedDuration == dur
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GaViolet else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.videoDuration.value = dur }
                        ) {
                            Text(
                                text = "${dur}s",
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Aspect Ratio selector
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = AppStrings.get(language, "aspect_ratio"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ratios.forEach { rat ->
                        val isSelected = selectedAspectRatio == rat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GaViolet else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.videoAspectRatio.value = rat }
                        ) {
                            Text(
                                text = rat,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Generate Action CTA
        Button(
            onClick = {
                if (activeTab == 0) {
                    viewModel.generateVideo()
                } else {
                    viewModel.animateImageToVideo()
                }
            },
            enabled = !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_video_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GaGold)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Rendering Neural Video...",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (activeTab == 0) AppStrings.get(language, "generate_video_btn") + " (2 Credits)"
                    else AppStrings.get(language, "animate_image_btn") + " (2 Credits)",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Generated Video Player Showcase
        if (lastVideo != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Rendered Video Studio",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GaGold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Display active cycling frame if available, or static main
                        val displayFile = if (activeFrames.isNotEmpty()) {
                            activeFrames[currentFrameIndex.coerceIn(0, activeFrames.size - 1)]
                        } else {
                            File(lastVideo!!.mediaUrl)
                        }

                        AsyncImage(
                            model = displayFile,
                            contentDescription = lastVideo!!.prompt,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Play/Pause Overlay Pill
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { isPlaying = !isPlaying }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = GaGold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        // Bottom timeline info
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "00:0${(currentFrameIndex * lastVideo!!.durationSeconds) / (activeFrames.size.coerceAtLeast(1))} / 00:0${lastVideo!!.durationSeconds}s",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                                BadgeChip(text = lastVideo!!.motionType, color = GaGold)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = lastVideo!!.prompt,
                            fontSize = 13.sp,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.saveCreationToGallery(context, lastVideo!!) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_video_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = AppStrings.get(language, "download"), fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.shareCreation(context, lastVideo!!) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("share_video_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = AppStrings.get(language, "share"), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
