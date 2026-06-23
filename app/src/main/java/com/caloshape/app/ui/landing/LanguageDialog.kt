@file:OptIn(ExperimentalMaterial3Api::class)

package com.caloshape.app.ui.landing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.caloshape.app.R
import com.caloshape.app.i18n.LanguageManager
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.common.haptic.caloShapeClickable
import com.caloshape.app.ui.common.haptic.rememberClickWithHaptic

data class LangItem(val label: String, val tag: String, val flag: String)

val LANGS = listOf(
    LangItem("English (United States)", "en-US", "🇺🇸"),
    LangItem("简体中文", "zh-CN", "🇨🇳"),
    LangItem("繁體中文", "zh-HK", "🇭🇰"),
    LangItem("日本語", "ja", "🇯🇵"),
    LangItem("Deutsch", "de", "🇩🇪"),
    LangItem("Français", "fr", "🇫🇷"),
    LangItem("한국어", "ko", "🇰🇷"),
    LangItem("Español (Latinoamérica)", "es-419", "🌎"),
    LangItem("Español (España)", "es", "🇪🇸"),
    LangItem("Português (Brasil)", "pt-BR", "🇧🇷")
)

@Composable
fun LanguageDialog(
    title: String,
    currentTag: String,
    onPick: (LangItem) -> Unit,
    onDismiss: () -> Unit,
    lang: List<LangItem> = LANGS,
    widthFraction: Float = 0.92f,
    maxHeightFraction: Float = 0.60f,
    useDarkStyle: Boolean = false
) {
    val screenH = LocalConfiguration.current.screenHeightDp.dp
    val maxHeight = screenH * maxHeightFraction
    val themeColors = CaloShapeColors.current()
    val isDark = useDarkStyle && themeColors.background == CaloShapeColors.Dark.background
    val surface = if (isDark) Color(0xFF18151F) else Color.White
    val rowSurface = if (isDark) Color(0xFF24212D) else surface
    val onSurface = if (isDark) Color(0xFFF7F5FF) else Color(0xFF111114)
    val unselectedContent = if (isDark) Color(0xFFC9C4D4) else onSurface
    val selectedContainer = if (isDark) Color(0xFF2A2633) else Color(0xFF111114)
    val selectedContent = if (isDark) Color(0xFFF7F5FF) else Color.White
    val outline = if (isDark) Color(0xFF34303D) else Color(0xFFE5E7EB)
    val selectedOutline = if (isDark) Color(0xFF6F687C) else Color.Transparent

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxWidth(widthFraction)
                .requiredHeightIn(max = maxHeight),
            shape = RoundedCornerShape(22.dp),
            color = surface,
            border = if (isDark) BorderStroke(1.2.dp, outline) else null,
            tonalElevation = 0.dp,
            shadowElevation = if (isDark) 16.dp else 8.dp
        ) {
            Column(Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 10.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurface,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = rememberClickWithHaptic(onClick = onDismiss),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(y = 2.dp)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.common_close),
                            tint = if (isDark) unselectedContent else onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(lang) { langItem ->
                        val supported = LanguageManager.isSupported(langItem.tag)
                        val selected = LanguageManager.isSelectedOption(
                            optionTag = langItem.tag,
                            currentTag = currentTag
                        )
                        val bg = if (selected) selectedContainer else rowSurface
                        val fg = if (selected) selectedContent else unselectedContent
                        val border = if (selected) selectedOutline else outline

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .border(BorderStroke(1.dp, border), RoundedCornerShape(16.dp))
                                .caloShapeClickable(enabled = supported) {
                                    onPick(langItem)
                                }
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = langItem.flag, fontSize = 18.sp)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = langItem.label,
                                color = fg,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(percent = 50))
                                        .background(selectedContent)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
