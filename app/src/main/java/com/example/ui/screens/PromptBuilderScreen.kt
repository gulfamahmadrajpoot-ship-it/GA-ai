package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BadgeChip
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.GaCyan
import com.example.ui.theme.GaGold
import com.example.ui.theme.GaViolet
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PromptBuilderScreen(
    viewModel: MainViewModel,
    onNavigateToImage: () -> Unit,
    onNavigateToVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val urduInput by viewModel.urduPromptInput.collectAsState()
    val promptResult by viewModel.promptResult.collectAsState()
    val isEnhancing by viewModel.isEnhancingPrompt.collectAsState()
    val activeChips by viewModel.activeChips.collectAsState()

    // Categorized builder items
    val subjects = listOf("Portrait", "Futuristic City", "Cyberpunk Samurai", "Mughal Palace", "Astronaut", "Mystic Dragon", "Hypercar")
    val artStyles = listOf("Photorealism 8K", "Cyberpunk Neon", "Baroque Oil Painting", "Studio Ghibli Anime", "Octane 3D Render", "Mughal Miniature Art")
    val lightings = listOf("Golden Hour Sunlight", "Volumetric God Rays", "Neon Cyberpunk Rim", "Moody Studio Softbox", "Cinematic Backlight", "Bioluminescent Glow")
    val cameras = listOf("85mm Portrait Lens f/1.4", "Wide-Angle 24mm", "Macro Close-up Detail", "Drone Aerial Bird's Eye", "Cinematic Bokeh", "Low-Angle Hero Shot")
    val engines = listOf("Unreal Engine 5", "Octane Render", "Ray Traced Reflections", "Global Illumination", "Hasselblad Ultra HD")
    val moods = listOf("Ethereal & Mystical", "High Octane Dynamic", "Serene & Peaceful", "Dark Gothic Elegance", "Vibrant Euphoria")

    // Dynamic combined prompt preview
    val combinedBuilderPrompt = remember(activeChips, promptResult) {
        val base = promptResult?.englishPrompt ?: urduInput
        if (activeChips.isEmpty()) base else "$base, ${activeChips.joinToString(", ")}"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = AppStrings.get(language, "prompt_builder_title"),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Urdu AI Prompt Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Urdu AI",
                        tint = GaGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get(language, "urdu_prompt_helper"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = urduInput,
                    onValueChange = { viewModel.urduPromptInput.value = it },
                    placeholder = {
                        Text(
                            text = AppStrings.get(language, "urdu_input_hint"),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("urdu_prompt_input"),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GaGold,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.enhanceUrduPrompt() },
                    enabled = !isEnhancing && urduInput.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("enhance_prompt_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GaViolet)
                ) {
                    if (isEnhancing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Expanding Prompt with AI...", fontSize = 13.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Enhance",
                            tint = GaGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = AppStrings.get(language, "enhance_btn"), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Generated Prompt Result Card
        if (promptResult != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GaGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BadgeChip(text = "AI ENHANCED PROMPT", color = GaGold)
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("GA AI Prompt", promptResult!!.englishPrompt))
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = GaGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = promptResult!!.englishPrompt,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )

                    if (promptResult!!.urduPrompt.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "اردو ترجمہ و منظر نگاری:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GaGold
                        )
                        Text(
                            text = promptResult!!.urduPrompt,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.sendPromptToImageGenerator(combinedBuilderPrompt)
                                onNavigateToImage()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GaViolet),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "To Image", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "To Image", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.sendPromptToVideoGenerator(combinedBuilderPrompt)
                                onNavigateToVideo()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GaGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = "To Video", tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "To Video", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Modular Category Chips Builder
        Text(
            text = "Interactive Modular Keyword Builder",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Tap keywords below to instantly compose high-impact prompt tags",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Categories
        CategorySection("Subjects & Themes", subjects, activeChips) { viewModel.toggleChip(it) }
        CategorySection("Art Styles", artStyles, activeChips) { viewModel.toggleChip(it) }
        CategorySection("Lighting & Atmosphere", lightings, activeChips) { viewModel.toggleChip(it) }
        CategorySection("Camera Angles & Optics", cameras, activeChips) { viewModel.toggleChip(it) }
        CategorySection("Render Engines & Resolution", engines, activeChips) { viewModel.toggleChip(it) }
        CategorySection("Mood & Aura", moods, activeChips) { viewModel.toggleChip(it) }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Combined Prompt Bar
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Live Composed Prompt (${activeChips.size} modifiers active)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GaCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = combinedBuilderPrompt,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("GA AI Prompt", combinedBuilderPrompt))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Copy", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.sendPromptToImageGenerator(combinedBuilderPrompt)
                            onNavigateToImage()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GaViolet),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Use in Generator", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySection(
    title: String,
    items: List<String>,
    activeChips: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { chip ->
                val isActive = activeChips.contains(chip)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isActive) GaViolet else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (isActive) GaGold else Color.Transparent),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onToggle(chip) }
                ) {
                    Text(
                        text = chip,
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
