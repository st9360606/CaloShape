package com.calai.bitecal.ui.home.ui.settings.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.calai.bitecal.ui.common.design.BiteCalColors
import com.calai.bitecal.ui.common.haptic.rememberClickWithHaptic
import com.calai.bitecal.ui.home.components.HomeCardStyles

@Composable
fun DeleteAccountDialog(
    visible: Boolean,
    title: String,
    body: String,
    cancelText: String,
    deleteText: String,
    deletingText: String,
    closeContentDescription: String,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    deleting: Boolean = false
) {
    if (!visible) return

    val dismissClick = rememberClickWithHaptic(enabled = !deleting, onClick = onDismiss)
    val cancelClick = rememberClickWithHaptic(enabled = !deleting, onClick = onCancel)
    val deleteClick = rememberClickWithHaptic(enabled = !deleting, onClick = onDelete)
    val colors = BiteCalColors.current()
    val isDark = colors.background == BiteCalColors.Dark.background

    Dialog(
        onDismissRequest = { if (!deleting) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !deleting,
            dismissOnClickOutside = !deleting,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) HomeCardStyles.Dialog.surface() else colors.surface,
                border = if (isDark) BorderStroke(1.2.dp, HomeCardStyles.Dialog.border()) else null,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 26.dp, vertical = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .size(33.dp)
                                .background(
                                    if (isDark) HomeCardStyles.Dialog.panel() else colors.surfaceMuted,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { if (!deleting) dismissClick() },
                                enabled = !deleting,
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = closeContentDescription,
                                    tint = colors.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = body,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (!deleting) cancelClick() },
                            enabled = !deleting,
                            shape = RoundedCornerShape(999.dp),
                            border = BorderStroke(0.8.dp, colors.textPrimary.copy(alpha = 0.62f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isDark) HomeCardStyles.Dialog.panel() else colors.surface,
                                contentColor = colors.textPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(47.dp)
                        ) {
                            Text(
                                text = cancelText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { if (!deleting) deleteClick() },
                            enabled = !deleting,
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE46A6A),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(47.dp)
                        ) {
                            Text(
                                text = if (deleting) deletingText else deleteText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
