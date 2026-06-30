package com.caloshape.app.ui.auth.email

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caloshape.app.R
import com.caloshape.app.ui.common.haptic.rememberCaloShapeHaptics
import com.caloshape.app.ui.common.design.CaloShapeOnboardingBottomBar
import com.caloshape.app.ui.common.design.CaloShapeOnboardingColors
import com.caloshape.app.ui.common.design.CaloShapePlainBackTopBar
import com.caloshape.app.ui.common.design.CaloShapeScreenFrame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailEnterScreen(
    vm: EmailSignInViewModel,
    onBack: () -> Unit,
    onSent: (String) -> Unit
) {
    val ui by vm.enter.collectAsState()
    val emailFieldHaptics = rememberCaloShapeHaptics()
    var emailFieldFocused by remember { mutableStateOf(false) }
    val isDark = CaloShapeOnboardingColors.isDark()
    val screenBackground = if (isDark) CaloShapeOnboardingColors.background() else Color.White
    val titleColor = if (isDark) CaloShapeOnboardingColors.title() else Color(0xFF111114)
    val fieldContainer = if (isDark) CaloShapeOnboardingColors.inputSurface() else Color.White
    val focusedBorder = if (isDark) CaloShapeOnboardingColors.title() else Color.Black
    val unfocusedBorder = if (isDark) CaloShapeOnboardingColors.softBorder() else Color(0xFFDDDDDD)
    val labelColor = if (isDark) CaloShapeOnboardingColors.subtitle() else Color(0xFF666666)
    val inputTextColor = if (isDark) CaloShapeOnboardingColors.title() else Color(0xFF111114)

    Scaffold(
        containerColor = screenBackground,
        topBar = {
            CaloShapePlainBackTopBar(
                onBack = onBack
            )
        },
        bottomBar = {
            CaloShapeOnboardingBottomBar(
                primaryText = stringResource(R.string.common_continue_btn),
                onPrimaryClick = { vm.sendCode(onSent) },
                primaryEnabled = ui.isValid && !ui.loading,
                primaryLoading = ui.loading,
                useImePadding = true
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(horizontal = CaloShapeScreenFrame.onboardingHorizontal)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.email_sign_in_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = titleColor
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text(stringResource(R.string.input_box_email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && !emailFieldFocused) {
                            emailFieldHaptics.click()
                        }
                        emailFieldFocused = focusState.isFocused
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = focusedBorder,
                    unfocusedBorderColor = unfocusedBorder,
                    errorBorderColor = Color(0xFFD32F2F),
                    cursorColor = focusedBorder,
                    focusedLabelColor = focusedBorder,
                    unfocusedLabelColor = labelColor,
                    focusedTextColor = inputTextColor,
                    unfocusedTextColor = inputTextColor,
                    focusedContainerColor = fieldContainer,
                    unfocusedContainerColor = fieldContainer,
                    errorContainerColor = fieldContainer,
                    disabledContainerColor = fieldContainer
                )
            )

            Spacer(Modifier.height(28.dp))

            

            ui.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
