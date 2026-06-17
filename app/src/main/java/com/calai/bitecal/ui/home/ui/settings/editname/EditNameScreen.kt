package com.calai.bitecal.ui.home.ui.settings.editname

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calai.bitecal.R
import com.calai.bitecal.ui.common.design.BiteCalColors
import com.calai.bitecal.ui.common.design.BiteCalTopBar
import com.calai.bitecal.ui.common.haptic.hapticOnFocus
import com.calai.bitecal.ui.common.design.BiteCalScreenFrame
import com.calai.bitecal.ui.common.design.BiteCalEditBottomActionBar
import com.calai.bitecal.ui.home.components.HomeBackground
import com.calai.bitecal.ui.home.components.HomeCardStyles

@Composable
fun EditNameScreen(
    input: String,
    canSave: Boolean,
    isSaving: Boolean,
    errorText: String?,
    onBack: () -> Unit,
    onInputChange: (String) -> Unit,
    onSaved: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val colors = BiteCalColors.current()
    val isDark = colors.background == BiteCalColors.Dark.background
    val screenBackground = if (isDark) Color.Transparent else colors.background
    val errorColor = if (isDark) HomeCardStyles.Status.dangerText() else colors.error

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) {
            HomeBackground(
                modifier = Modifier.matchParentSize(),
                darkTheme = true,
                enableNoise = false
            )
        }

        Scaffold(
            containerColor = screenBackground,
            topBar = {
                BiteCalTopBar(
                    title = stringResource(R.string.settings_edit_your_name_title),
                    onBack = onBack
                )
            },
            bottomBar = {
                BiteCalEditBottomActionBar(
                    primaryText = stringResource(R.string.common_save),
                    onPrimaryClick = onSaved,
                    primaryEnabled = canSave && !isSaving,
                    primaryLoading = isSaving,
                    useImePadding = true,
                    modifier = Modifier.semantics { testTag = "doneButton" }
                )
            }
        ) { inner ->
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = BiteCalScreenFrame.contentHorizontalComfort)
                    .padding(top = BiteCalScreenFrame.detailTop, bottom = BiteCalScreenFrame.detailBottom)
            ) {
                Spacer(Modifier.height(30.dp))

                NameField(
                    value = input,
                    onValueChange = onInputChange,
                    onImeDone = {
                        if (canSave && !isSaving) {
                            focusManager.clearFocus()
                            onSaved()
                        }
                    }
                )

                if (!errorText.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = errorText,
                        color = errorColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    onImeDone: () -> Unit
) {
    val colors = BiteCalColors.current()
    val isDark = colors.background == BiteCalColors.Dark.background
    val shape = RoundedCornerShape(14.dp)
    val fieldSurface = if (isDark) HomeCardStyles.Surface.raised() else Color.Transparent
    val fieldBorder = if (isDark) HomeCardStyles.Surface.borderColor() else colors.textPrimary
    val inputTextColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val placeholderColor = if (isDark) HomeCardStyles.Text.muted() else colors.textMuted

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(shape)
            .background(fieldSurface, shape)
            .border(width = if (isDark) 1.2.dp else 2.dp, color = fieldBorder, shape = shape)
            .padding(horizontal = BiteCalScreenFrame.contentHorizontalCompact),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 18.sp,
                color = inputTextColor,
                fontWeight = FontWeight.Normal
            ),
            cursorBrush = SolidColor(inputTextColor),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onImeDone() }),
            modifier = Modifier
                .fillMaxWidth()
                .hapticOnFocus()
                .semantics { testTag = "nameField" },
            decorationBox = { inner ->
                if (value.isBlank()) {
                    Text(
                        text = stringResource(R.string.edit_name_placeholder),
                        fontSize = 18.sp,
                        color = placeholderColor,
                        fontWeight = FontWeight.Normal
                    )
                }
                inner()
            }
        )
    }
}
