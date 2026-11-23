package com.example.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class GeneratedPromptResult(
    val englishPrompt: String,
    val urduPrompt: String,
    val suggestedStyles: List<String>,
    val negativePrompt: String
)

class PromptGenerationService {

    suspend fun enhanceOrTranslatePrompt(
        input: String,
        customApiKey: String? = null
    ): GeneratedPromptResult = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getEffectiveApiKey(customApiKey)
        if (apiKey.isNotEmpty()) {
            try {
                val sysInstruction = """
                    You are GA AI Studio's master creative director and bilingual prompt engineering expert (Urdu & English).
                    Given any user idea (in Urdu, Roman Urdu, or English), output a valid JSON object strictly with these fields:
                    - "englishPrompt": An award-winning, photorealistic or artistic prompt with cinematic lighting, camera lens (e.g. 85mm f/1.4), volumetric atmosphere, 8K resolution details.
                    - "urduPrompt": A rich, poetic, descriptive Urdu translation/adaptation of the visual scene in authentic Urdu script (نستعلیق انداز).
                    - "suggestedStyles": A list of 3-4 style tags (e.g. ["Photorealistic", "Cyberpunk Neon", "Mughal Art", "Cinematic Film"]).
                    - "negativePrompt": Suggested negative prompt keywords to avoid artifacts (e.g. "blurry, low quality, distorted, extra limbs").
                    Output ONLY raw JSON without markdown code fences.
                """.trimIndent()

                val prompt = "User creative idea: $input"
                val responseJson = GeminiClient.callGenerateContent(
                    model = "gemini-2.5-flash",
                    apiKey = apiKey,
                    prompt = prompt,
                    systemInstruction = sysInstruction
                )

                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")?.trim() ?: ""

                    // Clean JSON markers if present
                    val cleanJsonStr = text.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val parsed = JSONObject(cleanJsonStr)

                    val en = parsed.optString("englishPrompt", input)
                    val ur = parsed.optString("urduPrompt", "")
                    val neg = parsed.optString("negativePrompt", "blurry, distorted, low resolution, bad anatomy")
                    val styles = mutableListOf<String>()
                    val stylesArr = parsed.optJSONArray("suggestedStyles")
                    if (stylesArr != null) {
                        for (i in 0 until stylesArr.length()) {
                            styles.add(stylesArr.getString(i))
                        }
                    }
                    if (styles.isEmpty()) styles.addAll(listOf("Photorealistic", "Cinematic Film", "3D Render"))

                    return@withContext GeneratedPromptResult(
                        englishPrompt = en,
                        urduPrompt = ur,
                        suggestedStyles = styles,
                        negativePrompt = neg
                    )
                }
            } catch (e: Exception) {
                // Fallback to local intelligent prompt builder
            }
        }

        // Offline / Fast Template Enhancer
        val isUrduInput = input.any { it in '\u0600'..'\u06FF' }
        val enhancedEn = if (isUrduInput) {
            "Masterpiece cinematic scene inspired by '$input', golden hour sunlight, hyper-detailed textures, volumetric god rays, Hasselblad 80mm lens, ray-traced reflections, 8K UHD"
        } else {
            "Cinematic masterwork of $input, intricate details, volumetric atmosphere, octane render, vivid color grading, studio lighting, depth of field bokeh, photorealistic 8K"
        }

        val urduTranslation = if (isUrduInput) {
            input
        } else {
            "شاہکار سنیماٹک منظر: $input، طلسماتی سنہری روشنی، انتہا درجے کی باریکیاں، ایٹ کے الٹرا ایچ ڈی"
        }

        GeneratedPromptResult(
            englishPrompt = enhancedEn,
            urduPrompt = urduTranslation,
            suggestedStyles = listOf("Photorealistic", "Cyberpunk Neon", "Mughal Art", "Cinematic Film"),
            negativePrompt = "blurry, duplicate, deformed, disfigured, poor quality, bad anatomy"
        )
    }
}
