package com.aryaxzell.aurabrowser.data.model

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val subtitle: String
) {
    ID(
        code = "id",
        displayName = "Indonesia",
        nativeName = "Bahasa Indonesia",
        subtitle = "Bahasa Indonesia"
    ),
    EN(
        code = "en",
        displayName = "Inggris (US)",
        nativeName = "English (US)",
        subtitle = "English (United States)"
    ),
    RU(
        code = "ru",
        displayName = "Rusia",
        nativeName = "Русский",
        subtitle = "Русский язык"
    );

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: EN
        }
    }
}
