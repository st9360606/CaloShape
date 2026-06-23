package com.caloshape.app.ui.home.ui.savedfood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.caloshape.app.R
import com.caloshape.app.ui.common.haptic.caloShapeClickable
import com.caloshape.app.ui.common.CaloShapeConfirmDialog
import com.caloshape.app.ui.home.components.CardStyles
import com.caloshape.app.ui.home.components.HomeBackground
import com.caloshape.app.ui.home.components.HomeCardStyles
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.common.design.CaloShapeSavedFoodTokens
import com.caloshape.app.ui.common.design.CaloShapeTopBar
import com.caloshape.app.ui.home.ui.savedfood.model.SavedFoodCardUi
import com.caloshape.app.ui.home.ui.savedfood.model.SavedFoodsViewModel
import com.caloshape.app.ui.common.haptic.rememberClickWithHaptic
import com.caloshape.app.ui.common.design.CaloShapeScreenFrame



@Composable
fun SavedFoodsScreen(
    onBack: () -> Unit,
    onOpenDetail: (foodLogId: String, previewUri: String?, timeText: String) -> Unit,
    vm: SavedFoodsViewModel,
    modifier: Modifier = Modifier
) {
    val ui by vm.ui.collectAsStateWithLifecycle()

    var pendingUnsaveFoodLogId by rememberSaveable { mutableStateOf<String?>(null) }
    var unsaveSubmitting by rememberSaveable { mutableStateOf(false) }
    val colors = CaloShapeColors.current()

    LaunchedEffect(Unit) {
        vm.loadIfNeeded()
    }

    val isDark = colors.background == CaloShapeColors.Dark.background
    val screenBackground = if (isDark) Color.Transparent else colors.background
    val hintTextColor = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary

    Box(modifier = modifier.fillMaxSize()) {
        if (isDark) {
            HomeBackground(
                modifier = Modifier.matchParentSize(),
                darkTheme = true,
                enableNoise = false
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackground)
                .testTag("saved_foods_screen")
        ) {
            SavedFoodsTopBar(onBack = onBack)

            Text(
                text = stringResource(R.string.saved_foods_keep_15_days_hint),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-8).dp)
                    .padding(start = CaloShapeScreenFrame.contentHorizontal, end = CaloShapeScreenFrame.contentHorizontal, top = 0.dp, bottom = 2.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = hintTextColor,
                textAlign = TextAlign.Center
            )

        when {
            ui.loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (isDark) {
                        CircularProgressIndicator(color = HomeCardStyles.Text.primary())
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }

            !ui.error.isNullOrBlank() -> {
                SavedFoodsErrorState(
                    message = ui.error!!,
                    onRetry = vm::refresh
                )
            }

            ui.items.isEmpty() -> Unit

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 10.dp,
                        bottom = 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = ui.items,
                        key = { it.foodLogId }
                    ) { item ->
                        SavedFoodCard(
                            item = item,
                            onRemove = {
                                pendingUnsaveFoodLogId = item.foodLogId
                            },
                            onOpenDetail = {
                                onOpenDetail(
                                    item.foodLogId,
                                    item.previewUri,
                                    item.timeText
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    }

    CaloShapeConfirmDialog(
        visible = pendingUnsaveFoodLogId != null,
        onDismiss = {
            if (!unsaveSubmitting) {
                pendingUnsaveFoodLogId = null
            }
        },
        onCancel = {
            if (!unsaveSubmitting) {
                pendingUnsaveFoodLogId = null
            }
        },
        onConfirm = {
            val targetId = pendingUnsaveFoodLogId ?: return@CaloShapeConfirmDialog
            if (unsaveSubmitting) return@CaloShapeConfirmDialog

            unsaveSubmitting = true
            vm.unsave(
                foodLogId = targetId,
                onSuccess = {
                    unsaveSubmitting = false
                    pendingUnsaveFoodLogId = null
                },
                onFailure = {
                    unsaveSubmitting = false
                }
            )
        },
        loading = unsaveSubmitting,
        title = stringResource(R.string.saved_foods_unsave_dialog_title),
        message = stringResource(R.string.saved_foods_unsave_dialog_message),
        confirmText = stringResource(R.string.saved_foods_unsave_dialog_confirm),
        cancelText = stringResource(R.string.common_cancel)
    )
}

@Composable
private fun SavedFoodsTopBar(
    onBack: () -> Unit
) {
    CaloShapeTopBar(
        title = stringResource(R.string.saved_foods_title),
        onBack = onBack
    )
}

@Composable
private fun SavedFoodCard(
    item: SavedFoodCardUi,
    onRemove: () -> Unit,
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val cardContainer = if (isDark) HomeCardStyles.Surface.raised() else CardStyles.bg()
    val cardBorder = if (isDark) HomeCardStyles.Surface.border() else CardStyles.border()
    val primaryText = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val mutedText = if (isDark) HomeCardStyles.Text.muted() else colors.textSecondary
    val mediaSurface = if (isDark) HomeCardStyles.Surface.raisedAlt() else colors.surfaceMuted
    val removeSurface = if (isDark) HomeCardStyles.Surface.raisedAlt() else colors.surfaceMuted
    val detailContainer = if (isDark) Color.White.copy(alpha = 0.88f) else colors.primaryButtonContainer
    val detailContent = if (isDark) Color(0xFF111114) else colors.primaryButtonContent

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .testTag("saved_food_card")
            .caloShapeClickable(onClick = onOpenDetail),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardContainer),
        border = cardBorder,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 12.dp,
                        bottom = 10.dp
                    )
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = (-2).dp, y = (-2).dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(removeSurface)
                        .caloShapeClickable(onClick = onRemove)
                        .align(Alignment.TopStart)
                        .testTag("saved_food_remove"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove from saved foods icon",
                        modifier = Modifier.size(14.dp),
                        tint = mutedText
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(mediaSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!item.previewUri.isNullOrBlank()) {
                            AsyncImage(
                                model = item.previewUri,
                                contentDescription = item.displayTitle,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "🍽️",
                                fontSize = 26.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier.height(22.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.displayTitle,
                            style = CaloShapeSavedFoodTokens.TitleTextStyle.copy(color = primaryText),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier.height(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.saved_foods_kcal, item.kcal),
                            style = CaloShapeSavedFoodTokens.KcalTextStyle.copy(color = primaryText),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("recent_upload_kcal")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier.height(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MacroValue(
                                text = stringResource(
                                    R.string.saved_foods_protein,
                                    item.proteinG
                                )
                            )
                            MacroValue(
                                text = stringResource(
                                    R.string.saved_foods_carbs,
                                    item.carbsG
                                )
                            )
                            MacroValue(
                                text = stringResource(
                                    R.string.saved_foods_fat,
                                    item.fatG
                                )
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(detailContainer)
                    .caloShapeClickable(onClick = onOpenDetail)
                    .testTag("saved_food_detail"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.saved_foods_detail),
                        color = detailContent,
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .offset(y = (-1).dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "→",
                        color = detailContent,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .offset(y = (-1).dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroValue(text: String) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val textColor = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary

    Text(
        text = text,
        style = CaloShapeSavedFoodTokens.MacroTextStyle.copy(color = textColor),
        maxLines = 1,
        overflow = TextOverflow.Clip
    )
}

@Composable
private fun SavedFoodsErrorState(
    message: String,
    onRetry: () -> Unit
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val titleColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val messageColor = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.saved_foods_load_failed_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )

        Text(
            text = message,
            fontSize = 15.sp,
            color = messageColor
        )

        TextButton(onClick = rememberClickWithHaptic(onClick = onRetry)) {
            Text(text = stringResource(R.string.common_retry))
        }
    }
}
