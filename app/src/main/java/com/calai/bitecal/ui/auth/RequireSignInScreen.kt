package com.calai.bitecal.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calai.bitecal.R
import com.calai.bitecal.ui.common.haptic.rememberClickWithHaptic
import com.calai.bitecal.ui.common.design.BiteCalOnboardingColors
import com.calai.bitecal.ui.common.design.BiteCalOnboardingTopBar
import com.calai.bitecal.ui.common.design.BiteCalScreenFrame

private enum class PrimaryAuthMethod { Google, Email }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequireSignInScreen(
    onBack: () -> Unit,
    onGoogleClick: () -> Unit,
    onEmailClick: () -> Unit,
    snackBarHostState: SnackbarHostState,
    ctaVerticalOffset: Dp = (-24).dp
) {
    val isDark = BiteCalOnboardingColors.isDark()
    val ink = if (isDark) BiteCalOnboardingColors.title() else Color(0xFF111114)
    val screenBackground = if (isDark) BiteCalOnboardingColors.background() else Color.White
    val selectedContainer = if (isDark) Color.White else ink
    val selectedContent = if (isDark) Color.Black else Color.White
    val outlinedContainer = if (isDark) BiteCalOnboardingColors.inputSurface() else Color.Transparent
    val outlinedBorder = if (isDark) BiteCalOnboardingColors.softBorder() else Color(0xFFE5E5EA)
    val outlinedButtonBorder = if (isDark) {
        BorderStroke(1.2.dp, outlinedBorder)
    } else {
        ButtonDefaults.outlinedButtonBorder(enabled = true)
    }
    val googleCircleBg = if (isDark) Color.White else Color(0xFFF1F3F7)
    var primaryAuth by remember { mutableStateOf(PrimaryAuthMethod.Google) }

    BackHandler { onBack() }

    Scaffold(
        containerColor = screenBackground,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            BiteCalOnboardingTopBar(
                stepIndex = 12,
                totalSteps = 12,
                onBack = onBack
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.auth_sign_in_account_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 40.sp
                ),
                color = ink,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(
                        horizontal = BiteCalScreenFrame.contentHorizontalWide,
                        vertical = BiteCalScreenFrame.authTitleVertical)
                    .fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = BiteCalScreenFrame.contentHorizontalWide)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = ctaVerticalOffset)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Google ---
                    if (primaryAuth == PrimaryAuthMethod.Google) {
                        Button(
                            onClick = rememberClickWithHaptic {
                                primaryAuth = PrimaryAuthMethod.Google
                                onGoogleClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = selectedContainer,
                                contentColor = selectedContent
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.google),
                                        contentDescription = "Google",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.auth_sign_in_with_google),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = rememberClickWithHaptic {
                                primaryAuth = PrimaryAuthMethod.Google
                                onGoogleClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(100.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            border = outlinedButtonBorder,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = outlinedContainer,
                                contentColor = ink
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(googleCircleBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.google),
                                        contentDescription = "Google",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.auth_sign_in_with_google),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // --- Email ---
                    if (primaryAuth == PrimaryAuthMethod.Email) {
                        Button(
                            onClick = rememberClickWithHaptic {
                                primaryAuth = PrimaryAuthMethod.Email
                                onEmailClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = selectedContainer,
                                contentColor = selectedContent
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = "Email",
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.auth_signin_with_email),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = rememberClickWithHaptic {
                                primaryAuth = PrimaryAuthMethod.Email
                                onEmailClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(100.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            border = outlinedButtonBorder,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = outlinedContainer,
                                contentColor = ink
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = "Email",
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.auth_signin_with_email),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                }
            }
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}
