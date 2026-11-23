package com.example.ui.localization

enum class AppLanguage {
    ENGLISH,
    URDU
}

object AppStrings {
    fun get(lang: AppLanguage, key: String): String {
        val isUrdu = lang == AppLanguage.URDU
        return when (key) {
            // App Branding
            "app_name" -> if (isUrdu) "جی اے اے آئی" else "GA AI"
            "studio_title" -> if (isUrdu) "جی اے تخلیقی اسٹوڈیو" else "GA Creative Studio"
            "tagline" -> if (isUrdu) "اگلی نسل کا مصنوعی ذہانت اسٹوڈیو" else "Next-Gen AI Creative Studio"

            // Navigation
            "nav_image" -> if (isUrdu) "تصویر جنریٹر" else "AI Image"
            "nav_video" -> if (isUrdu) "ویڈیو جنریٹر" else "AI Video"
            "nav_builder" -> if (isUrdu) "پرامپٹ بلڈر" else "Prompt Builder"
            "nav_creations" -> if (isUrdu) "میری تخلیقات" else "My Creations"
            "nav_profile" -> if (isUrdu) "پروفائل اور ترتیبات" else "Profile"

            // Image Generator
            "image_generator_title" -> if (isUrdu) "اے آئی تصویر ساز" else "AI Image Generator"
            "prompt_hint" -> if (isUrdu) "اپنا تخلیقی خیال اردو یا انگریزی میں لکھیں..." else "Describe what you want to create in English or Urdu..."
            "negative_prompt" -> if (isUrdu) "منفی پرامپٹ (وہ چیزیں جو نہ ہوں)" else "Negative Prompt (Things to avoid)"
            "negative_prompt_hint" -> if (isUrdu) "دھندلا پن، بگڑا ہوا چہرہ، کم معیار..." else "blurry, distorted, low quality, artifacts..."
            "aspect_ratio" -> if (isUrdu) "پہلو تناسب (Aspect Ratio)" else "Aspect Ratio"
            "art_style" -> if (isUrdu) "تخلیقی انداز (Style Preset)" else "Style Preset"
            "ref_image" -> if (isUrdu) "حوالہ تصویر (Reference Image)" else "Reference Image"
            "upload_image" -> if (isUrdu) "گیلری سے تصویر منتخب کریں" else "Pick from Gallery"
            "take_photo" -> if (isUrdu) "کیمرہ کھولیں" else "Take Photo"
            "remove_ref" -> if (isUrdu) "حوالہ ہٹائیں" else "Remove"
            "generate_image_btn" -> if (isUrdu) "تصویر تخلیق کریں" else "Generate Image"
            "generating" -> if (isUrdu) "تخلیق کا عمل جاری ہے..." else "Creating Masterpiece..."
            "generating_hint" -> if (isUrdu) "نیورل ماڈل ایچ ڈی پکسلز تشکیل دے رہا ہے..." else "Neural engine is synthesizing HD pixels..."

            // Video Generator & Img2Vid
            "video_generator_title" -> if (isUrdu) "اے آئی ویڈیو ساز" else "AI Video Generator"
            "tab_text_to_video" -> if (isUrdu) "تحریر سے ویڈیو" else "Text to Video"
            "tab_image_to_video" -> if (isUrdu) "تصویر سے ویڈیو" else "Image to Video"
            "motion_type" -> if (isUrdu) "کیمرہ و موشن اسٹائل" else "Camera & Motion Style"
            "duration" -> if (isUrdu) "دورانیہ (سیکنڈز)" else "Duration"
            "generate_video_btn" -> if (isUrdu) "ویڈیو تیار کریں" else "Generate Video"
            "animate_image_btn" -> if (isUrdu) "تصویر کو حرکت دیں" else "Animate to Video"
            "select_image_first" -> if (isUrdu) "پہلے تصویر منتخب کریں" else "Please select an image first"

            // Prompt Builder & Urdu Prompt Generator
            "prompt_builder_title" -> if (isUrdu) "پرامپٹ اسٹوڈیو اور اردو جنریٹر" else "Prompt Studio & Urdu Generator"
            "urdu_prompt_helper" -> if (isUrdu) "اردو سے اسمارٹ پرامپٹ بنائیں" else "Urdu AI Prompt Enhancer"
            "urdu_input_hint" -> if (isUrdu) "مثال: لاہور کی بارش میں نیون لائٹس اور بادشاہی مسجد..." else "e.g. Neon rain reflections at Badshahi Mosque in Lahore..."
            "enhance_btn" -> if (isUrdu) "پرامپٹ کو شاندار بنائیں" else "Enhance with Gemini AI"
            "use_in_image" -> if (isUrdu) "تصویر جنریٹر میں بھیجیں" else "Send to Image Generator"
            "use_in_video" -> if (isUrdu) "ویڈیو جنریٹر میں بھیجیں" else "Send to Video Generator"
            "copy_prompt" -> if (isUrdu) "پرامپٹ کاپی کریں" else "Copy Prompt"
            "copied" -> if (isUrdu) "پرامپٹ کاپی ہو گیا!" else "Prompt copied to clipboard!"
            "categories" -> if (isUrdu) "اقسام" else "Categories"
            "subject" -> if (isUrdu) "موضوع" else "Subject"
            "lighting" -> if (isUrdu) "روشنی" else "Lighting"
            "camera" -> if (isUrdu) "کیمرہ و اینگل" else "Camera & Lens"
            "render_engine" -> if (isUrdu) "رینڈر انجن" else "Render Engine"
            "mood" -> if (isUrdu) "ماحول و تاثر" else "Mood & Atmosphere"

            // My Creations
            "creations_title" -> if (isUrdu) "میری تخلیقات" else "My Creations"
            "all" -> if (isUrdu) "تمام" else "All"
            "images" -> if (isUrdu) "تصاویر" else "Images"
            "videos" -> if (isUrdu) "ویڈیوز" else "Videos"
            "favorites" -> if (isUrdu) "پسندیدہ" else "Favorites"
            "no_creations_yet" -> if (isUrdu) "ابھی تک کوئی تخلیق موجود نہیں" else "No creations yet"
            "start_creating" -> if (isUrdu) "تصویر یا ویڈیو جنریٹر سے شروع کریں" else "Start generating amazing AI art above!"
            "download" -> if (isUrdu) "ڈاؤن لوڈ" else "Download"
            "share" -> if (isUrdu) "شیئر کریں" else "Share"
            "regenerate" -> if (isUrdu) "دوبارہ بنائیں" else "Regenerate"
            "delete" -> if (isUrdu) "حذف کریں" else "Delete"
            "download_success" -> if (isUrdu) "گیلری میں کامیابی سے محفوظ ہو گیا!" else "Saved successfully to device gallery!"

            // User Profile
            "profile_title" -> if (isUrdu) "میرا پروفائل" else "User Profile"
            "member_tier" -> if (isUrdu) "جی اے پرو اسٹوڈیو ممبر" else "GA AI Pro Studio Member"
            "daily_credits" -> if (isUrdu) "روزانہ کے کریڈٹس" else "Daily AI Credits"
            "remaining" -> if (isUrdu) "باقی ہیں" else "Remaining"
            "total_creations" -> if (isUrdu) "کل تخلیقات" else "Total Creations"
            "favorite_items" -> if (isUrdu) "پسندیدہ اشیاء" else "Favorites"
            "high_res_renders" -> if (isUrdu) "ہائی ریزولوشن رینڈرز" else "HD Renders"
            "bio" -> if (isUrdu) "تعارف" else "Bio"
            "user_name_label" -> if (isUrdu) "صارف کا نام" else "Display Name"
            "save_profile" -> if (isUrdu) "پروفائل محفوظ کریں" else "Save Profile"

            // Settings
            "settings_title" -> if (isUrdu) "ترتیبات (Settings)" else "Settings"
            "language" -> if (isUrdu) "زبان (Language)" else "Language"
            "theme" -> if (isUrdu) "تھیم (Theme)" else "Theme"
            "dark_mode" -> if (isUrdu) "ڈارک موڈ (تاریک)" else "Dark Mode"
            "light_mode" -> if (isUrdu) "لائٹ موڈ (روشن)" else "Light Mode"
            "system_mode" -> if (isUrdu) "سسٹم کے مطابق" else "System Default"
            "api_key_section" -> if (isUrdu) "جیمینائی اے آئی کی (Gemini Key)" else "Gemini AI API Key"
            "api_key_status_active" -> if (isUrdu) "اے آئی کنکشن فعال ہے ✓" else "Active via AI Studio ✓"
            "api_key_custom_hint" -> if (isUrdu) "اختیاری: کسٹم جیمینائی کی درج کریں" else "Optional: Enter custom Gemini API Key"
            "save_key" -> if (isUrdu) "کی محفوظ کریں" else "Save Key"
            "clear_cache" -> if (isUrdu) "اسٹوڈیو کیشے صاف کریں" else "Clear Studio Cache"
            "cache_cleared" -> if (isUrdu) "کیشے صاف ہو گیا" else "Cache cleared successfully"
            "about_title" -> if (isUrdu) "جی اے اے آئی کے بارے میں" else "About GA AI"
            "version" -> if (isUrdu) "ورژن ۲.۰ نیٹِو ایڈیشن" else "Version 2.0 Native Edition"

            // Admin Panel & App Sharing
            "admin_panel" -> if (isUrdu) "ماسٹر ایڈمن کنٹرول پینل" else "Master Admin Panel"
            "admin_founder" -> if (isUrdu) "گلفام احمد راجپوت (بانی و ماسٹر ایڈمن)" else "Gulfam Ahmad Rajpoot (Founder & Master Admin)"
            "unlimited_credits" -> if (isUrdu) "لامحدود کریڈٹس فعال کریں (∞)" else "Grant Unlimited Credits (∞)"
            "add_500_credits" -> if (isUrdu) "500 کریڈٹس شامل کریں" else "Add 500 Credits"
            "share_app_btn" -> if (isUrdu) "دوستوں کے ساتھ ایپ شیئر کریں" else "Share App with Friends"
            "download_app_apk" -> if (isUrdu) "اینڈرائیڈ ایپ ڈاؤن لوڈ کریں (APK)" else "Download Android App (APK)"
            "admin_badge_text" -> if (isUrdu) "ماسٹر ایڈمن رسائی حاصل ہے" else "Master Admin Access Active"
            "app_distribution" -> if (isUrdu) "ایپ ڈاؤن لوڈ و شیئر سینٹر" else "App Download & Share Center"
            "admin_quota_boost" -> if (isUrdu) "کریڈٹس بوسٹر" else "Quota Booster"
            "credits_boosted" -> if (isUrdu) "کریڈٹس کامیابی سے بڑھا دیے گئے!" else "Credits boosted successfully!"

            else -> key
        }
    }
}
