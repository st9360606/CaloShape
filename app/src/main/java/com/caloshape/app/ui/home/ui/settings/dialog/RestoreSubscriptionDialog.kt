package com.caloshape.app.ui.home.ui.settings.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.common.haptic.rememberClickWithHaptic
import com.caloshape.app.ui.home.components.HomeCardStyles
import com.caloshape.app.ui.home.ui.settings.model.RestoreSubscriptionDialogState
import com.caloshape.app.ui.home.ui.settings.model.RestoreSubscriptionUiState

@Composable
fun RestoreSubscriptionDialog(
    uiState: RestoreSubscriptionUiState,
    title: String,
    body: String,
    closeText: String,
    restoreText: String,
    restoringText: String,
    maybeLaterText: String,
    onDismiss: () -> Unit,
    onMaybeLater: () -> Unit,
    onRestore: () -> Unit
) {
    if (!uiState.visible) return

    val isRestoring = uiState.dialogState == RestoreSubscriptionDialogState.Restoring
    val dismissClick = rememberClickWithHaptic(enabled = !isRestoring, onClick = onDismiss)
    val restoreClick = rememberClickWithHaptic(enabled = !isRestoring, onClick = onRestore)
    val maybeLaterClick = rememberClickWithHaptic(enabled = !isRestoring, onClick = onMaybeLater)
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background

    val isResultState = when (uiState.dialogState) {
        RestoreSubscriptionDialogState.Restored,
        RestoreSubscriptionDialogState.RestoredWithPaymentIssue,
        RestoreSubscriptionDialogState.NoActivePurchase,
        RestoreSubscriptionDialogState.Failed,
        RestoreSubscriptionDialogState.BoundToAnotherAccount -> true

        RestoreSubscriptionDialogState.Hidden,
        RestoreSubscriptionDialogState.CandidateFound,
        RestoreSubscriptionDialogState.Restoring -> false
    }

    Dialog(
        onDismissRequest = { if (!isRestoring) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !isRestoring,
            dismissOnClickOutside = !isRestoring,
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
                    modifier = Modifier.padding(horizontal = 26.dp, vertical = 30.dp)
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
                                onClick = { if (!isRestoring) dismissClick() },
                                enabled = !isRestoring,
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = closeText,
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

                    if (isResultState) {
                        Button(
                            onClick = dismissClick,
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primaryButtonContainer,
                                contentColor = colors.primaryButtonContent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(47.dp)
                        ) {
                            Text(
                                text = closeText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { if (!isRestoring) restoreClick() },
                                enabled = !isRestoring,
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.primaryButtonContainer,
                                    contentColor = colors.primaryButtonContent,
                                    disabledContainerColor = colors.primaryButtonContainer.copy(alpha = 0.56f),
                                    disabledContentColor = colors.primaryButtonContent.copy(alpha = 0.86f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(53.dp)
                            ) {
                                Text(
                                    text = if (isRestoring) restoringText else restoreText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { if (!isRestoring) maybeLaterClick() },
                                enabled = !isRestoring,
                                shape = RoundedCornerShape(999.dp),
                                border = BorderStroke(0.8.dp, colors.textPrimary.copy(alpha = 0.62f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isDark) HomeCardStyles.Dialog.panel() else colors.surface,
                                    contentColor = colors.textPrimary,
                                    disabledContainerColor = if (isDark) HomeCardStyles.Dialog.panel() else colors.surface,
                                    disabledContentColor = colors.textPrimary.copy(alpha = 0.45f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(53.dp)
                            ) {
                                Text(
                                    text = maybeLaterText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
