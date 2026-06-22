package com.calai.bitecal.i18n

data class LangOption(
    val tag: String,
    val name: String,
    val flag: String,
    val label: String
)

val LANGS: List<LangOption> = listOf(
    LangOption("en-US", "English (United States)", "🇺🇸", "EN"),
    LangOption("zh-CN", "简体中文", "🇨🇳", "简"),
    LangOption("zh-HK", "繁體中文", "🇭🇰", "繁"),
    LangOption("ja", "日本語", "🇯🇵", "JP"),
    LangOption("de", "Deutsch", "🇩🇪", "DE"),
    LangOption("fr", "Français", "🇫🇷", "FR"),
    LangOption("ko", "한국어", "🇰🇷", "KR"),
    LangOption("es-419", "Español (Latinoamérica)", "🌎", "LA"),
    LangOption("es", "Español (España)", "🇪🇸", "ES"),
    LangOption("pt-BR", "Português (Brasil)", "🇧🇷", "BR")
)

fun langShortLabelFromTag(tag: String): String {
    val normalized = LanguageManager.normalizeTag(tag)
    return LANGS.firstOrNull { it.tag.equals(normalized, ignoreCase = true) }?.label ?: "EN"
}

fun flagAndLabelFromTag(tag: String): Pair<String, String> {
    val normalized = LanguageManager.normalizeTag(tag)
    val option = LANGS.firstOrNull { it.tag.equals(normalized, ignoreCase = true) }
        ?: LANGS.first()
    return option.flag to option.label
}
