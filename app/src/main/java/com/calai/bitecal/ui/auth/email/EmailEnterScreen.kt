package com.calai.bitecal.ui.auth.email

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calai.bitecal.R
import com.calai.bitecal.ui.common.haptic.rememberBiteCalHaptics
import com.calai.bitecal.ui.common.design.BiteCalOnboardingBottomBar
import com.calai.bitecal.ui.common.design.BiteCalOnboardingColors
import com.calai.bitecal.ui.common.design.BiteCalPlainBackTopBar
import com.calai.bitecal.ui.common.design.BiteCalScreenFrame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailEnterScreen(
    vm: EmailSignInViewModel,
    onBack: () -> Unit,
    onSent: (String) -> Unit
) {
    val ui by vm.enter.collectAsState()
    val emailFieldHaptics = rememberBiteCalHaptics()
    var emailFieldFocused by remember { mutableStateOf(false) }
    val isDark = BiteCalOnboardingColors.isDark()
    val screenBackground = if (isDark) BiteCalOnboardingColors.background() else Color.White
    val titleColor = if (isDark) BiteCalOnboardingColors.title() else Color(0xFF111114)
    val fieldContainer = if (isDark) BiteCalOnboardingColors.inputSurface() else Color.White
    val focusedBorder = if (isDark) BiteCalOnboardingColors.title() else Color.Black
    val unfocusedBorder = if (isDark) BiteCalOnboardingColors.softBorder() else Color(0xFFDDDDDD)
    val labelColor = if (isDark) BiteCalOnboardingColors.subtitle() else Color(0xFF666666)
    val inputTextColor = if (isDark) BiteCalOnboardingColors.title() else Color(0xFF111114)

    Scaffold(
        containerColor = screenBackground,
        topBar = {
            BiteCalPlainBackTopBar(
                onBack = onBack
            )
        },
        bottomBar = {
            BiteCalOnboardingBottomBar(
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
                .padding(horizontal = BiteCalScreenFrame.onboardingHorizontal)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.email_sign_in_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 34.sp),
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
