package com.caloshape.app.i18n

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageManager {
    const val DEFAULT_LANGUAGE_TAG = "en-US"

    private val canonicalTags = mapOf(
        "en" to "en-US",
        "en-us" to "en-US",

        "zh-cn" to "zh-CN",
        "zh-hans" to "zh-CN",
        "zh-hans-cn" to "zh-CN",
        "zh-sg" to "zh-CN",

        "zh-hk" to "zh-HK",
        "zh-tw" to "zh-HK",
        "zh-mo" to "zh-HK",
        "zh-hant" to "zh-HK",
        "zh-hant-hk" to "zh-HK",
        "zh-hant-tw" to "zh-HK",

        "ja" to "ja",
        "ja-jp" to "ja",
        "de" to "de",
        "de-de" to "de",
        "de-at" to "de",
        "de-ch" to "de",
        "fr" to "fr",
        "fr-fr" to "fr",
        "fr-be" to "fr",
        "fr-ca" to "fr",
        "ko" to "ko",
        "ko-kr" to "ko",
        "es" to "es",
        "es-es" to "es",
        "pt" to "pt-BR",
        "pt-br" to "pt-BR"
    )

    fun normalizeTag(raw: String?): String = canonicalTagOrNull(raw) ?: DEFAULT_LANGUAGE_TAG

    fun isSupported(raw: String?): Boolean = canonicalTagOrNull(raw) != null

    fun isSelectedOption(optionTag: String?, currentTag: String?): Boolean {
        return normalizeTag(optionTag).equals(normalizeTag(currentTag), ignoreCase = true)
    }

    fun applyLanguage(tag: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(normalizeTag(tag))
        )
    }

    private fun canonicalTagOrNull(raw: String?): String? {
        val input = raw
            ?.trim()
            ?.replace('_', '-')
            .orEmpty()

        if (input.isBlank()) return null

        canonicalTags[input.lowercase(Locale.ROOT)]?.let { return it }

        val locale = runCatching { Locale.forLanguageTag(input) }.getOrNull() ?: return null
        if (locale.language.isBlank() || locale.language == "und") return null

        val language = locale.language.lowercase(Locale.ROOT)
        val region = locale.country.uppercase(Locale.ROOT)
        val script = locale.script.lowercase(Locale.ROOT)

        return when (language) {
            "en" -> "en-US"
            "zh" -> if (script == "hans" || region == "CN" || region == "SG") {
                "zh-CN"
            } else {
                "zh-HK"
            }
            "ja" -> "ja"
            "de" -> "de"
            "fr" -> "fr"
            "ko" -> "ko"
            "es" -> "es"
            "pt" -> if (region.isBlank() || region == "BR") "pt-BR" else null
            else -> null
        }
    }
}
