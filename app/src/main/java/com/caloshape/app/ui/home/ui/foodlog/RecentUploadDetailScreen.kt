package com.caloshape.app.ui.home.ui.foodlog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.caloshape.app.R
import com.caloshape.app.ui.common.haptic.caloShapeClickable
import com.caloshape.app.data.foodlog.model.FoodLogEnvelopeDto
import com.caloshape.app.data.foodlog.model.FoodLogStatus
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.common.design.CaloShapeEditBottomActionBar
import com.caloshape.app.ui.common.design.CaloShapeFoodLogDetailTokens
import com.caloshape.app.ui.common.design.CaloShapeShape
import com.caloshape.app.ui.common.design.CaloShapeSize
import com.caloshape.app.core.time.UtcTimeFormatter
import com.caloshape.app.ui.common.design.CaloShapeSpacing
import com.caloshape.app.ui.home.ui.foodlog.dialog.DeleteFoodLogDialog
import com.caloshape.app.ui.home.ui.foodlog.model.FoodLogFlowViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import com.caloshape.app.ui.common.haptic.rememberClickWithHaptic
import com.caloshape.app.ui.common.design.CaloShapeScreenFrame
import com.caloshape.app.ui.home.components.HomeBackground
import com.caloshape.app.ui.home.components.HomeCardStyles


private val recentUploadDetailDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMMM d · HH:mm", Locale.US)

private val recentUploadDetailTimeOnlyFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.US)


private data class ScaledNutrients(
    val kcal: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val sugar: Double,
    val sodium: Double
)

private val FoodLogEnvelopeDto.safePortionMultiplier: Int
    get() = portionMultiplier.coerceAtLeast(1)

/**
 * 將目前 envelope 中「已保存」的 nutritionResult，先反推回 base(1x)，
 * 再依 editingMultiplier 做即時預覽。
 */
private fun FoodLogEnvelopeDto.previewScaledNutrients(
    editingMultiplier: Int
): ScaledNutrients {
    val persistedMultiplier = safePortionMultiplier.toDouble()
    val n = nutritionResult?.nutrients

    fun rescale(value: Double?): Double {
        val current = value ?: 0.0
        val base = if (persistedMultiplier <= 0.0) current else current / persistedMultiplier
        return base * editingMultiplier
    }

    return ScaledNutrients(
        kcal = rescale(n?.kcal),
        protein = rescale(n?.protein),
        carbs = rescale(n?.carbs),
        fat = rescale(n?.fat),
        fiber = rescale(n?.fiber),
        sugar = rescale(n?.sugar),
        sodium = rescale(n?.sodium)
    )
}

private fun resolveFoodLogDisplayTime(
    env: FoodLogEnvelopeDto,
    fallbackTimeText: String = ""
): String {
    val zoneId = ZoneId.systemDefault()

    val utcInstant = parseUtcInstantOrNull(env.createdAtUtc)
        ?: parseUtcInstantOrNull(env.serverReceivedAtUtc)
        ?: parseUtcInstantOrNull(env.capturedAtUtc)

    if (utcInstant != null) {
        return utcInstant
            .atZone(zoneId)
            .format(recentUploadDetailDateTimeFormatter)
    }

    return FoodLogTimeResolver.resolveDisplayTimeText(
        zoneId = zoneId,
        createdAtUtc = env.createdAtUtc,
        serverReceivedAtUtc = env.serverReceivedAtUtc,
        capturedAtUtc = env.capturedAtUtc,
        capturedLocalDate = env.capturedLocalDate
    ).ifBlank { fallbackTimeText }
}

private fun parseUtcInstantOrNull(value: Any?): Instant? {
    return when (value) {
        null -> null
        is Instant -> value
        is OffsetDateTime -> value.toInstant()
        is ZonedDateTime -> value.toInstant()
        is LocalDateTime -> value.toInstant(ZoneOffset.UTC)
        is String -> parseUtcInstantTextOrNull(value)
        else -> parseUtcInstantTextOrNull(value.toString())
    }
}

private fun parseUtcInstantTextOrNull(raw: String): Instant? =
    UtcTimeFormatter.parseBackendUtcInstantOrNull(raw)

@Composable
fun RecentUploadDetailScreen(
    foodLogId: String,
    previewUri: String?,
    timeText: String,
    vm: FoodLogFlowViewModel,
    onBack: () -> Unit,
    onSave: (FoodLogEnvelopeDto) -> Unit,
    onSavedStateChanged: (FoodLogEnvelopeDto) -> Unit = {},
    onDeleted: (String) -> Unit
) {
    val st by vm.state.collectAsState()
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val sheetSurface = if (isDark) HomeCardStyles.Chart.surface() else colors.surface
    val sheetBorder = if (isDark) HomeCardStyles.Chart.border() else Color.Transparent

    val liveEnv = st.envelope?.takeIf { it.foodLogId == foodLogId }

    var lastStableEnv by remember(foodLogId) {
        mutableStateOf<FoodLogEnvelopeDto?>(null)
    }

    val handleBack = remember(onBack, vm) {
        {
            vm.stopPolling()
            onBack()
        }
    }

    BackHandler(enabled = !st.loading) {
        handleBack()
    }

    LaunchedEffect(liveEnv) {
        val candidate = liveEnv
        if (
            candidate != null &&
            candidate.status != FoodLogStatus.DELETED &&
            candidate.nutritionResult != null
        ) {
            lastStableEnv = candidate
        }
    }

    val env = when {
        liveEnv != null &&
                liveEnv.status != FoodLogStatus.DELETED &&
                liveEnv.nutritionResult != null -> liveEnv

        else -> lastStableEnv
    }

    LaunchedEffect(foodLogId) {
        vm.clearTransient()
        vm.startPolling(foodLogId)
    }

    DisposableEffect(foodLogId) {
        onDispose { vm.stopPolling() }
    }

    var stablePreviewUri by rememberSaveable(foodLogId) { mutableStateOf(previewUri) }
    var stableTimeText by rememberSaveable(foodLogId) { mutableStateOf(timeText) }
    var showDeleteDialog by rememberSaveable(foodLogId) { mutableStateOf(false) }
    var deleteRequested by rememberSaveable(foodLogId) { mutableStateOf(false) }

    LaunchedEffect(
        deleteRequested,
        st.loading,
        st.error,
        st.apiError,
        st.cooldown,
        st.refused
    ) {
        if (!deleteRequested || st.loading) return@LaunchedEffect

        if (
            st.error != null ||
            st.apiError != null ||
            st.cooldown != null ||
            st.refused != null
        ) {
            deleteRequested = false
        }
    }

    if (st.loading && env == null) {
        RecentUploadDetailLoadingFrame(previewUri = stablePreviewUri)
        return
    }

    if (env == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color.Transparent else colors.background),
            contentAlignment = Alignment.Center
        ) {
            if (isDark) {
                HomeBackground(
                    modifier = Modifier.matchParentSize(),
                    darkTheme = true,
                    enableNoise = false
                )
            }
            Text(
                text = st.error ?: stringResource(R.string.foodlog_detail_load_failed),
                color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
            )
        }
        return
    }

    val persistedMultiplier = env.safePortionMultiplier

    var multiplier by rememberSaveable(foodLogId) {
        mutableIntStateOf(persistedMultiplier)
    }

    LaunchedEffect(foodLogId, persistedMultiplier) {
        multiplier = persistedMultiplier
    }

    val scaled = remember(env, multiplier) {
        env.previewScaledNutrients(multiplier)
    }

    val persistedSaved = env.status == FoodLogStatus.SAVED

    var editingSaved by rememberSaveable(foodLogId) {
        mutableStateOf(persistedSaved)
    }

    var saveBadgeBusy by rememberSaveable(foodLogId) {
        mutableStateOf(false)
    }
    var footerSaveBusy by rememberSaveable(foodLogId) {
        mutableStateOf(false)
    }

    LaunchedEffect(foodLogId, persistedSaved) {
        editingSaved = persistedSaved
    }

    val displayName = env.nutritionResult?.foodName
        ?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.foodlog_unknown_food)

    val healthScore = env.nutritionResult?.healthScore ?: 0

    val detailTimeText = remember(env, stableTimeText) {
        resolveFoodLogDisplayTime(
            env = env,
            fallbackTimeText = stableTimeText
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color.Transparent else colors.background)
    ) {
        if (isDark) {
            HomeBackground(
                modifier = Modifier.matchParentSize(),
                darkTheme = true,
                enableNoise = false
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.46f)
        ) {
            if (!stablePreviewUri.isNullOrBlank()) {
                AsyncImage(
                    model = stablePreviewUri,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(CaloShapeFoodLogDetailTokens.HeroFallback),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.foodlog_no_image),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(CaloShapeFoodLogDetailTokens.Scrim)
            )

            RecentUploadDetailTopBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                enabled = !st.loading,
                onBack = handleBack,
                onDeleteClick = {
                    showDeleteDialog = true
                }
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = sheetSurface,
            border = if (isDark) BorderStroke(1.dp, sheetBorder) else null,
            shadowElevation = if (isDark) 0.dp else 10.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = CaloShapeScreenFrame.contentHorizontalMedium)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SaveBadge(
                            isSaved = editingSaved,
                            enabled = !saveBadgeBusy &&
                                    !st.loading &&
                                    env.status != FoodLogStatus.PENDING &&
                                    env.status != FoodLogStatus.FAILED &&
                                    env.status != FoodLogStatus.DELETED,
                            onClick = {
                                if (!saveBadgeBusy) {
                                    saveBadgeBusy = true
                                    val targetSaved = !editingSaved
                                    vm.commitDetailChanges(
                                        foodLogId = foodLogId,
                                        baseEnv = env,
                                        multiplier = multiplier,
                                        targetSaved = targetSaved,
                                        previewUri = stablePreviewUri,
                                        timeText = stableTimeText,
                                        moveRecentUploadToTop = false,
                                        showLoading = false,
                                        onSuccess = { updatedEnv ->
                                            multiplier = updatedEnv.portionMultiplier.coerceAtLeast(1)
                                            editingSaved = updatedEnv.status == FoodLogStatus.SAVED
                                            stableTimeText = resolveFoodLogDisplayTime(
                                                env = updatedEnv,
                                                fallbackTimeText = stableTimeText
                                            )
                                            onSavedStateChanged(updatedEnv)
                                        },
                                        onFinished = {
                                            saveBadgeBusy = false
                                        }
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        TimeChip(timeText = detailTimeText)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = displayName,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                lineHeight = 26.sp
                            ),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
                        )

                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp, top = 1.dp)
                        ) {
                            Stepper(
                                value = multiplier,
                                enabled = !st.loading,
                                onMinus = { multiplier = (multiplier - 1).coerceAtLeast(1) },
                                onPlus = { multiplier += 1 }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CaloriesHeroCard(kcal = scaled.kcal.roundToInt())

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MacroCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.foodlog_detail_protein),
                            value = "${scaled.protein.roundToInt()}g",
                            tone = CaloShapeFoodLogDetailTokens.ProteinTone,
                            emoji = "🥩"
                        )
                        MacroCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.foodlog_detail_carbs),
                            value = "${scaled.carbs.roundToInt()}g",
                            tone = CaloShapeFoodLogDetailTokens.CarbsTone,
                            emoji = "🌾"
                        )
                        MacroCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.foodlog_detail_fats),
                            value = "${scaled.fat.roundToInt()}g",
                            tone = CaloShapeFoodLogDetailTokens.FatTone,
                            emoji = "🥑"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MacroCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.foodlog_detail_fiber),
                            value = "${scaled.fiber.roundToInt()}g",
                            tone = CaloShapeFoodLogDetailTokens.FiberTone,
                            emoji = "🌿"
                        )
                        MacroCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.foodlog_detail_sugar),
                            value = "${scaled.sugar.roundToInt()}g",
                            tone = CaloShapeFoodLogDetailTokens.SugarTone,
                            emoji = "🍯"
                        )
                        MacroCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.foodlog_detail_sodium),
                            value = "${scaled.sodium.roundToInt()}mg",
                            tone = CaloShapeFoodLogDetailTokens.SodiumTone,
                            emoji = "🧂"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HealthScoreCard(score = healthScore)

                    Spacer(modifier = Modifier.height(38.dp))
                }

                val hasMultiplierChange = multiplier != persistedMultiplier
                val hasSavedChange = editingSaved != persistedSaved

                FooterSaveBar(
                    enabled = !st.loading,
                    onSave = {
                        if (footerSaveBusy) return@FooterSaveBar

                        if (hasMultiplierChange || hasSavedChange) {
                            footerSaveBusy = true
                            vm.commitDetailChanges(
                                foodLogId = foodLogId,
                                baseEnv = env,
                                multiplier = multiplier,
                                targetSaved = editingSaved,
                                previewUri = stablePreviewUri,
                                timeText = stableTimeText,
                                moveRecentUploadToTop = false,
                                showLoading = false,
                                onSuccess = { updatedEnv ->
                                    multiplier = updatedEnv.portionMultiplier.coerceAtLeast(1)
                                    stableTimeText = resolveFoodLogDisplayTime(
                                        env = updatedEnv,
                                        fallbackTimeText = stableTimeText
                                    )
                                    onSave(updatedEnv)
                                },
                                onFinished = {
                                    footerSaveBusy = false
                                }
                            )
                        } else {
                            onSave(env)
                        }
                    }
                )
            }
        }

        DeleteFoodLogDialog(
            visible = showDeleteDialog,
            onDismiss = { showDeleteDialog = false },
            onCancel = { showDeleteDialog = false },
            onDelete = {
                if (!st.loading) {
                    showDeleteDialog = false
                    deleteRequested = true
                    vm.delete(foodLogId) {
                        deleteRequested = false
                        onDeleted(foodLogId)
                    }
                }
            },
            deleting = deleteRequested && st.loading
        )
    }
}

@Composable
private fun RecentUploadDetailLoadingFrame(
    previewUri: String?
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val sheetSurface = if (isDark) HomeCardStyles.Chart.surface() else colors.surface
    val sheetBorder = if (isDark) HomeCardStyles.Chart.border() else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color.Transparent else colors.background)
    ) {
        if (isDark) {
            HomeBackground(
                modifier = Modifier.matchParentSize(),
                darkTheme = true,
                enableNoise = false
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.46f)
        ) {
            if (!previewUri.isNullOrBlank()) {
                AsyncImage(
                    model = previewUri,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(CaloShapeFoodLogDetailTokens.HeroFallback)
                )
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(CaloShapeFoodLogDetailTokens.Scrim)
            )

            RecentUploadDetailTopBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                enabled = false,
                onBack = {},
                onDeleteClick = {}
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = sheetSurface,
            border = if (isDark) BorderStroke(1.dp, sheetBorder) else null,
            shadowElevation = if (isDark) 0.dp else 10.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecentUploadDetailTopBar(
    enabled: Boolean,
    onBack: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val legacyButtonBackground = if (isDark) {
        HomeCardStyles.Surface.raised().copy(alpha = 0.88f)
    } else {
        Color.White.copy(alpha = 0.6f)
    }
    val legacyIconColor = if (isDark) HomeCardStyles.Text.primary() else CaloShapeFoodLogDetailTokens.TextPrimary

    CenterAlignedTopAppBar(
        modifier = modifier.padding(
            start = CaloShapeSpacing.topBarHorizontal,
            end = CaloShapeSpacing.topBarHorizontal
        ),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = legacyIconColor,
            titleContentColor = Color.White,
            actionIconContentColor = legacyIconColor
        ),
        navigationIcon = {
            Box(
                modifier = Modifier
                    .size(CaloShapeSize.backButton)
                    .clip(CaloShapeShape.backButton)
                    .background(legacyButtonBackground)
                    .caloShapeClickable(enabled = enabled, onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = legacyIconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.foodlog_detail_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        },
        actions = {
            Box(
                modifier = Modifier
                    .offset(y = (1).dp)
                    .size(CaloShapeSize.backButtonCompact)
                    .clip(CircleShape)
                    .background(legacyButtonBackground)
                    .caloShapeClickable(enabled = enabled, onClick = onDeleteClick),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(legacyIconColor)
                )
            }
        }
    )
}

@Composable
private fun SaveBadge(
    isSaved: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val iconTint = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary

    Box(
        modifier = Modifier
            .size(27.dp)
            .caloShapeClickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "收藏標籤",
            tint = iconTint,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun formatDisplayTime(raw: String): String {
    val input = raw.trim()
    if (input.isBlank()) return "-- · --:--"

    parseUtcInstantTextOrNull(input)?.let { instant ->
        return instant
            .atZone(ZoneId.systemDefault())
            .format(recentUploadDetailDateTimeFormatter)
    }

    val timeOnlyCandidates = listOf(
        DateTimeFormatter.ofPattern("H:mm", Locale.US),
        DateTimeFormatter.ofPattern("HH:mm", Locale.US),
        DateTimeFormatter.ofPattern("h:mm a", Locale.US),
        DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
    )

    for (formatter in timeOnlyCandidates) {
        runCatching {
            return LocalTime
                .parse(input.uppercase(Locale.US), formatter)
                .format(recentUploadDetailTimeOnlyFormatter)
        }
    }

    return input
}

@Composable
private fun TimeChip(timeText: String) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val chipSurface = if (isDark) HomeCardStyles.Surface.raisedAlt() else colors.surfaceMuted
    val chipBorder = if (isDark) HomeCardStyles.Surface.borderColor() else Color.Transparent
    val chipText = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary

    Surface(
        color = chipSurface,
        shape = RoundedCornerShape(999.dp),
        border = if (isDark) BorderStroke(1.dp, chipBorder) else null
    ) {
        Text(
            text = formatDisplayTime(timeText),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall.merge(
                TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFeatureSettings = "tnum"
                )
            ),
            color = chipText,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun Stepper(
    value: Int,
    enabled: Boolean,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val borderColor = if (isDark) HomeCardStyles.Surface.borderColor() else colors.textPrimary
    val contentColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .height(43.dp)
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, borderColor, RoundedCornerShape(999.dp))
            .padding(horizontal = CaloShapeScreenFrame.contentHorizontalMedium)
    ) {
        IconButton(
            onClick = rememberClickWithHaptic(onClick = onMinus),
            enabled = enabled,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "減號按鈕",
                tint = contentColor
            )
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )

        IconButton(
            onClick = rememberClickWithHaptic(onClick = onPlus),
            enabled = enabled,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "加號按鈕",
                tint = contentColor
            )
        }
    }
}

@Composable
private fun CaloriesHeroCard(kcal: Int) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val cardSurface = if (isDark) HomeCardStyles.Surface.raised() else colors.surface
    val iconSurface = if (isDark) HomeCardStyles.Surface.raisedAlt() else colors.surfaceMuted
    val cardBorder = if (isDark) HomeCardStyles.Surface.borderColor() else colors.border
    val primaryText = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val secondaryText = if (isDark) HomeCardStyles.Text.secondary() else colors.textPrimary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        color = cardSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(18.dp),
                color = iconSurface,
                border = if (isDark) BorderStroke(1.dp, cardBorder) else null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🔥",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.foodlog_detail_calories),
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = kcal.toString(),
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
            }
        }
    }
}

@Composable
private fun MacroCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    tone: Color,
    emoji: String
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val cardSurface = if (isDark) HomeCardStyles.Surface.raised() else colors.surface
    val cardBorder = if (isDark) HomeCardStyles.Surface.borderColor() else colors.border
    val titleColor = if (isDark) HomeCardStyles.Text.secondary() else colors.textPrimary
    val valueColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary

    Surface(
        modifier = modifier.height(62.dp),
        color = cardSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(22.dp)
                    .offset(y = (-8).dp),
                shape = CircleShape,
                color = tone.copy(alpha = if (isDark) 0.20f else 0.14f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = titleColor,
                    maxLines = 1
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = valueColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun HealthScoreCard(
    score: Int
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val safeScore = score.coerceIn(0, 10)
    val progress = safeScore / 10f
    val cardSurface = if (isDark) HomeCardStyles.Surface.raised() else colors.surface
    val cardBorder = if (isDark) HomeCardStyles.Surface.borderColor() else colors.border
    val primaryText = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val secondaryText = if (isDark) HomeCardStyles.Text.secondary() else colors.textPrimary
    val progressTrack = if (isDark) HomeCardStyles.Progress.track() else colors.surfaceMuted

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = cardSurface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = cardBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(1.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.apple_health),
                    contentDescription = "健康圖示",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.foodlog_detail_health_score),
                        fontSize = 15.sp,
                        color = secondaryText,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "$safeScore/10",
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryText,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(progressTrack)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFF6BCB77))
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterSaveBar(
    enabled: Boolean,
    onSave: () -> Unit
) {
    CaloShapeEditBottomActionBar(
        primaryText = stringResource(R.string.common_save),
        onPrimaryClick = onSave,
        primaryEnabled = enabled,
        primaryLoading = false,
        buttonHeight = 52.dp,
        bottomPadding = 24.dp
    )
}
