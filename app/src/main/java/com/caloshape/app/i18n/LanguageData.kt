package com.caloshape.app.i18n

data class LangOption(
    val tag: String,
    val name: String,
    val flag: String,
    val label: String
)

val LANGS: List<LangOption> = listOf(
    LangOption("en-US", "English", "🇺🇸", "EN"),
    LangOption("ja", "日本語", "🇯🇵", "JP"),
    LangOption("de", "Deutsch", "🇩🇪", "DE"),
    LangOption("fr", "Français", "🇫🇷", "FR"),
    LangOption("ko", "한국어", "🇰🇷", "KR"),
    LangOption("es", "Español", "🇪🇸", "ES"),
    LangOption("zh-CN", "简体中文", "🇨🇳", "CH"),
    LangOption("zh-HK", "繁體中文", "🇭🇰", "HK"),
    LangOption("pt-BR", "Português", "🇧🇷", "BR")
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
