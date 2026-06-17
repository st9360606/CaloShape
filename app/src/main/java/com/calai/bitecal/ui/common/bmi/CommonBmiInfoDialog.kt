package com.calai.bitecal.ui.common.bmi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.calai.bitecal.ui.common.design.BiteCalColors
import com.calai.bitecal.ui.common.haptic.rememberClickWithHaptic
import com.calai.bitecal.ui.home.components.HomeCardStyles

data class CommonBmiInfoDialogModel(
    val title: String,
    val subtitle: String,
    val formulaTitle: String,
    val formulaValue: String,
    val meaningTitle: String,
    val meaningBody: String,
    val underweightText: String,
    val healthyText: String,
    val overweightText: String,
    val obeseText: String,
    val noteTitle: String,
    val noteBody: String,
    val ctaText: String
)

private val BmiBarBlue = Color(0xFF2D9CDB)
private val BmiBarGreen = Color(0xFF35C36C)
private val BmiBarYellow = Color(0xFFF2C94C)
private val BmiBarRed = Color(0xFFEB5757)

@Composable
fun CommonBmiInfoDialog(
    model: CommonBmiInfoDialogModel,
    onDismiss: () -> Unit
) {
    val dismissClick = rememberClickWithHaptic(onClick = onDismiss)
    val colors = BiteCalColors.current()
    val isDark = colors.background == BiteCalColors.Dark.background
    val dialogSurface = if (isDark) HomeCardStyles.Chart.surface() else colors.surface
    val dialogBorder = if (isDark) HomeCardStyles.Chart.border() else colors.border
    val sectionBg = if (isDark) HomeCardStyles.Chart.insetSurface() else colors.surfaceMuted
    val primaryText = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val secondaryText = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary
    val noteBg = Color(0xFFFFF7E8).copy(
        alpha = if (isDark) 0.16f else 1f
    )
    val noteText = if (isDark) {
        Color(0xFFFFD991)
    } else {
        Color(0xFF7A5A12)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = dialogSurface,
                modifier = Modifier
                    .offset(y = (-60).dp)
                    .fillMaxWidth(0.92f)
                    .widthIn(min = 280.dp, max = 420.dp)
                    .border(1.dp, dialogBorder, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 690.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = sectionBg,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = null,
                            tint = secondaryText,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = model.title,
                        color = primaryText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = model.subtitle,
                        color = secondaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = sectionBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
                        ) {
                            BmiInfoSectionTitle(model.formulaTitle)

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = model.formulaValue,
                                color = primaryText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    BmiInfoSectionTitle(model.meaningTitle)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = model.meaningBody,
                        color = primaryText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BmiInfoChip(
                            text = model.underweightText,
                            bg = BmiBarBlue,
                            modifier = Modifier.weight(1f)
                        )
                        BmiInfoChip(
                            text = model.healthyText,
                            bg = BmiBarGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BmiInfoChip(
                            text = model.overweightText,
                            bg = BmiBarYellow,
                            modifier = Modifier.weight(1f)
                        )
                        BmiInfoChip(
                            text = model.obeseText,
                            bg = BmiBarRed,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = noteBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = model.noteTitle,
                                color = noteText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = model.noteBody,
                                color = noteText,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = dismissClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp),
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primaryButtonContainer,
                            contentColor = colors.primaryButtonContent
                        )
                    ) {
                        Text(
                            text = model.ctaText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BmiInfoSectionTitle(
    text: String
) {
    val colors = BiteCalColors.current()
    val isDark = colors.background == BiteCalColors.Dark.background

    Text(
        text = text,
        color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
    )
}

@Composable
private fun BmiInfoChip(
    text: String,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
