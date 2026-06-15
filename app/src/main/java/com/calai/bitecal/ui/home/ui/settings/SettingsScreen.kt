package com.calai.bitecal.ui.home.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.StringRes
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.calai.bitecal.BuildConfig
import com.calai.bitecal.R
import com.calai.bitecal.data.foodlog.repo.HomeTodayNutritionSummary
import com.calai.bitecal.data.home.repo.HomeSummary
import com.calai.bitecal.i18n.currentLocaleKey
import com.calai.bitecal.ui.appearance.AppearanceMode
import com.calai.bitecal.ui.home.HomeTab
import com.calai.bitecal.ui.home.components.CardStyles
import com.calai.bitecal.ui.home.components.GaugeRing
import com.calai.bitecal.ui.home.components.HomeBackground
import com.calai.bitecal.ui.home.components.HomeCardStyles
import com.calai.bitecal.ui.home.components.MainBottomBar
import com.calai.bitecal.ui.home.ui.camera.menu.HomeQuickActionMenu
import com.calai.bitecal.ui.home.ui.camera.scan.ScanFab
import com.calai.bitecal.ui.common.haptic.biteCalClickable
import com.calai.bitecal.ui.common.haptic.rememberClickWithHaptic
import com.calai.bitecal.ui.home.ui.camera.components.CameraPermissionPrefs
import com.calai.bitecal.ui.home.ui.camera.components.CameraPermissionProxyActivity
import com.calai.bitecal.ui.home.ui.camera.components.openCameraPermissionSettings
import com.calai.bitecal.ui.common.design.BiteCalColors
import com.calai.bitecal.ui.common.design.BiteCalTopBar
import com.calai.bitecal.ui.home.ui.membership.MembershipDisplayKind
import com.calai.bitecal.ui.home.ui.settings.dialog.DeleteAccountDialog
import com.calai.bitecal.ui.home.ui.settings.dialog.PaymentIssueDialog
import com.calai.bitecal.ui.home.ui.settings.dialog.RestoreSubscriptionDialog
import com.calai.bitecal.ui.home.ui.settings.model.RestoreSubscriptionDialogState
import com.calai.bitecal.ui.home.ui.settings.model.RestoreSubscriptionUiState
import com.calai.bitecal.ui.landing.LanguageDialog
import kotlinx.coroutines.launch
import java.util.Locale
import com.calai.bitecal.ui.common.design.BiteCalScreenFrame
import com.calai.bitecal.ui.common.design.BiteCalSecondaryOutlinedButton
import com.calai.bitecal.widget.BiteCalHomeWidgetUpdater
import com.calai.bitecal.widget.BiteCalWidgetSnapshotStore
import androidx.compose.foundation.Image
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.ui.res.painterResource
/**
 * ✅ Personal => Settings（你圖上的那個）
 * - 內容可捲動
 * - BottomBar + FAB 固定（Scaffold）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    avatarUrl: Uri?,
    profileName: String,
    ageText: String,
    homeSummary: HomeSummary? = null,
    todayNutrition: HomeTodayNutritionSummary = HomeTodayNutritionSummary(),
    currentTab: HomeTab = HomeTab.Personal,
    onOpenTab: (HomeTab) -> Unit,
    onBack: () -> Unit = {},
    onOpenCamera: () -> Unit,
    onOpenPersonalDetails: () -> Unit = {},
    onOpenEditName: () -> Unit = {},
    onOpenAdjustMacros: () -> Unit = {},
    onOpenWeightHistory: () -> Unit = {},
    onOpenRingColorsExplained: () -> Unit = {},
    premiumStatusSubtitle: String = "Upgrade",
    premiumStatusKind: MembershipDisplayKind = MembershipDisplayKind.FREE,
    canUseScan: Boolean = false,
    onOpenSubscription: () -> Unit = {},
    onFixPaymentIssue: () -> Unit = onOpenSubscription,
    onCheckCanUseScan: suspend () -> Boolean = { canUseScan },
    onOpenSavedFoods: () -> Unit = {},
    onOpenReferral: () -> Unit = {},
    onOpenNotificationInbox: () -> Unit = {},
    onOpenWidgetGuide: () -> Unit = {},
    currentLanguageTag: String = "",
    appearanceMode: AppearanceMode = AppearanceMode.LIGHT,
    onAppearanceModeSelected: (AppearanceMode) -> Unit = {},
    onLanguageSelected: (String) -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {},
    onOpenSupportEmail: () -> Unit = {},
    restoreSubscriptionUiState: RestoreSubscriptionUiState = RestoreSubscriptionUiState(),
    onOpenRestoreSubscription: () -> Unit = {},
    onRestoreSubscription: () -> Unit = {},
    onDismissRestoreSubscription: () -> Unit = {},
    onMaybeLaterRestoreSubscription: () -> Unit = {},
    onDeleteAccount: (subscriptionWarningAcknowledged: Boolean) -> Unit = {},
    logoutLoading: Boolean = false,
    logoutErrorVisible: Boolean = false,
    onLogout: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val registryOwner = LocalActivityResultRegistryOwner.current
    val scope = rememberCoroutineScope()

    var showQuickAddMenu by rememberSaveable { mutableStateOf(false) }
    var scanFabGateInFlight by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showAppearanceDialog by rememberSaveable { mutableStateOf(false) }
    var languageSwitching by rememberSaveable { mutableStateOf(false) }

    val latestOnOpenCamera = rememberUpdatedState(onOpenCamera)
    val latestOnOpenSubscription = rememberUpdatedState(onOpenSubscription)
    val latestOnCheckCanUseScan = rememberUpdatedState(onCheckCanUseScan)
    val latestOnLanguageSelected = rememberUpdatedState(onLanguageSelected)

    val localeKey = currentLocaleKey()

    val effectiveLanguageTag = currentLanguageTag.ifBlank {
        ctx.resources.configuration.locales[0].toLanguageTag()
            .ifBlank { Locale.getDefault().toLanguageTag() }
    }

    val requestCameraPermLauncher =
        if (registryOwner != null) {
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    CameraPermissionPrefs.resetCameraDeniedCount(ctx)
                    latestOnOpenCamera.value.invoke()
                } else {
                    CameraPermissionPrefs.incrementCameraDeniedCount(ctx)
                }
            }
        } else {
            null
        }

    fun openScanFoodWithPermissionGate() {
        val grantedNow =
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED

        if (grantedNow) {
            CameraPermissionPrefs.resetCameraDeniedCount(ctx)
            latestOnOpenCamera.value.invoke()
            return
        }

        val deniedCount = CameraPermissionPrefs.getCameraDeniedCount(ctx)

        if (deniedCount >= 2) {
            openCameraPermissionSettings(ctx)
        } else {
            requestCameraPermLauncher?.launch(Manifest.permission.CAMERA)
                ?: CameraPermissionProxyActivity.start(ctx)
        }
    }

    fun handleScanFabClick() {
        if (scanFabGateInFlight) return

        scope.launch {
            scanFabGateInFlight = true

            val hasActiveAccess = runCatching {
                latestOnCheckCanUseScan.value.invoke()
            }.getOrDefault(false)

            scanFabGateInFlight = false

            if (hasActiveAccess) {
                showQuickAddMenu = true
            } else {
                latestOnOpenSubscription.value.invoke()
            }
        }
    }

    Box(Modifier.fillMaxSize()) { HomeBackground() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            BiteCalTopBar(
                title = stringResource(R.string.settings_title),
                onBack = onBack
            )
        },
        floatingActionButton = {
            ScanFab(
                onClick = {
                    handleScanFabClick()
                }
            )
        },
        bottomBar = { MainBottomBar(current = currentTab, onOpenTab = onOpenTab) }
    ) { inner ->
        key(localeKey) {
            SettingsContent(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                avatarUrl = avatarUrl,
                profileName = profileName,
                ageText = ageText,
                homeSummary = homeSummary,
                todayNutrition = todayNutrition,
                onOpenPersonalDetails = onOpenPersonalDetails,
                onOpenEditName = onOpenEditName,
                onOpenAdjustMacros = onOpenAdjustMacros,
                onOpenWeightHistory = onOpenWeightHistory,
                onOpenRingColorsExplained = onOpenRingColorsExplained,
                premiumStatusKind = premiumStatusKind,
                premiumStatusSubtitle = premiumStatusSubtitle,
                onOpenSubscription = onOpenSubscription,
                onFixPaymentIssue = onFixPaymentIssue,
                onOpenReferral = onOpenReferral,
                onOpenNotificationInbox = onOpenNotificationInbox,
                onOpenWidgetGuide = onOpenWidgetGuide,
                appearanceMode = appearanceMode,
                onOpenAppearance = { showAppearanceDialog = true },
                onOpenLanguage = { if (!languageSwitching) showLanguageDialog = true },
                onOpenTerms = onOpenTerms,
                onOpenPrivacy = onOpenPrivacy,
                onOpenSupportEmail = onOpenSupportEmail,
                restoreSubscriptionUiState = restoreSubscriptionUiState,
                onOpenRestoreSubscription = onOpenRestoreSubscription,
                onRestoreSubscription = onRestoreSubscription,
                onDismissRestoreSubscription = onDismissRestoreSubscription,
                onMaybeLaterRestoreSubscription = onMaybeLaterRestoreSubscription,
                onDeleteAccount = onDeleteAccount,
                logoutLoading = logoutLoading,
                logoutErrorVisible = logoutErrorVisible,
                onLogout = onLogout
            )
        }
    }

    HomeQuickActionMenu(
        visible = showQuickAddMenu,
        onDismiss = { showQuickAddMenu = false },
        onSavedFoodsClick = {
            showQuickAddMenu = false
            onOpenSavedFoods()
        },
        onScanFoodClick = {
            showQuickAddMenu = false
            openScanFoodWithPermissionGate()
        }
    )

    if (showAppearanceDialog) {
        AppearanceModeDialog(
            currentMode = appearanceMode,
            onPick = { mode ->
                showAppearanceDialog = false
                onAppearanceModeSelected(mode)
            },
            onDismiss = { showAppearanceDialog = false }
        )
    }

    if (showLanguageDialog) {
        LanguageDialog(
            title = stringResource(R.string.settings_choose_language),
            currentTag = effectiveLanguageTag,
            onPick = { picked ->
                if (languageSwitching) return@LanguageDialog
                languageSwitching = true
                showLanguageDialog = false
                if (!picked.tag.equals(effectiveLanguageTag, ignoreCase = true)) {
                    latestOnLanguageSelected.value(picked.tag)
                }
                languageSwitching = false
            },
            onDismiss = {
                if (!languageSwitching) showLanguageDialog = false
            },
            widthFraction = 0.92f,
            maxHeightFraction = 0.60f
        )
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    avatarUrl: Uri?,
    profileName: String,
    ageText: String,
    homeSummary: HomeSummary?,
    todayNutrition: HomeTodayNutritionSummary,
    onOpenPersonalDetails: () -> Unit,
    onOpenEditName: () -> Unit,
    onOpenAdjustMacros: () -> Unit,
    onOpenWeightHistory: () -> Unit,
    onOpenRingColorsExplained: () -> Unit,
    premiumStatusKind: MembershipDisplayKind,
    premiumStatusSubtitle: String,
    onOpenSubscription: () -> Unit,
    onFixPaymentIssue: () -> Unit,
    onOpenReferral: () -> Unit,
    onOpenNotificationInbox: () -> Unit,
    onOpenWidgetGuide: () -> Unit,
    appearanceMode: AppearanceMode,
    onOpenAppearance: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenSupportEmail: () -> Unit,
    restoreSubscriptionUiState: RestoreSubscriptionUiState,
    onOpenRestoreSubscription: () -> Unit,
    onRestoreSubscription: () -> Unit,
    onDismissRestoreSubscription: () -> Unit,
    onMaybeLaterRestoreSubscription: () -> Unit,
    onDeleteAccount: (subscriptionWarningAcknowledged: Boolean) -> Unit,
    logoutLoading: Boolean,
    logoutErrorVisible: Boolean,
    onLogout: () -> Unit
) {
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPaymentIssueDialog by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf(false) }

// ✅ 不要讓 Dialog 內部自己抓 stringResource。
// 先在 SettingsContent 的 locale scope 解析文字，再傳給 Dialog。
    val localeKey = currentLocaleKey()
    val deleteDialogTitle = stringResource(R.string.delete_account_dialog_title)
    val deleteDialogBody = stringResource(R.string.delete_account_dialog_body)
    val deleteDialogCancelText = stringResource(R.string.common_cancel)
    val deleteDialogDeleteText = stringResource(R.string.common_delete)
    val deleteDialogDeletingText = stringResource(R.string.common_deleting)
    val deleteDialogCloseText = stringResource(R.string.common_close)

    val restoreDialogTitle = when (restoreSubscriptionUiState.dialogState) {
        RestoreSubscriptionDialogState.Restored ->
            stringResource(R.string.restore_subscription_dialog_success_title)

        RestoreSubscriptionDialogState.RestoredWithPaymentIssue ->
            stringResource(R.string.restore_subscription_dialog_payment_issue_title)

        RestoreSubscriptionDialogState.NoActivePurchase ->
            stringResource(R.string.restore_subscription_dialog_no_active_title)

        RestoreSubscriptionDialogState.Failed ->
            stringResource(R.string.restore_subscription_dialog_failed_title)

        RestoreSubscriptionDialogState.BoundToAnotherAccount ->
            stringResource(R.string.restore_subscription_dialog_bound_title)

        RestoreSubscriptionDialogState.Hidden,
        RestoreSubscriptionDialogState.CandidateFound,
        RestoreSubscriptionDialogState.Restoring ->
            stringResource(R.string.restore_subscription_dialog_title)
    }

    val restoreDialogBody = when (restoreSubscriptionUiState.dialogState) {
        RestoreSubscriptionDialogState.Restored ->
            stringResource(R.string.restore_subscription_dialog_success_body)

        RestoreSubscriptionDialogState.RestoredWithPaymentIssue ->
            stringResource(R.string.restore_subscription_dialog_payment_issue_body)

        RestoreSubscriptionDialogState.NoActivePurchase ->
            stringResource(R.string.restore_subscription_dialog_no_active_body)

        RestoreSubscriptionDialogState.Failed ->
            stringResource(R.string.restore_subscription_dialog_failed_body)

        RestoreSubscriptionDialogState.BoundToAnotherAccount ->
            stringResource(R.string.restore_subscription_dialog_bound_body)

        RestoreSubscriptionDialogState.Hidden,
        RestoreSubscriptionDialogState.CandidateFound,
        RestoreSubscriptionDialogState.Restoring ->
            stringResource(R.string.restore_subscription_dialog_body)
    }

    val restoreDialogCloseText = stringResource(R.string.common_close)
    val restoreDialogRestoreText = stringResource(R.string.settings_restore_subscription)
    val restoreDialogRestoringText = stringResource(R.string.restore_subscription_dialog_restoring)
    val restoreDialogMaybeLaterText = stringResource(R.string.common_maybe_later)
    val paymentIssueTitle = stringResource(R.string.payment_issue_dialog_title)
    val paymentIssueBody = stringResource(R.string.payment_issue_dialog_body)
    val paymentIssueSupportingBody = stringResource(R.string.payment_issue_dialog_supporting_body)
    val paymentIssueUpdatePaymentText = stringResource(R.string.payment_issue_dialog_update_payment)
    val paymentIssueBadgeText = stringResource(R.string.payment_issue_dialog_badge)
    val paymentIssuePremiumAccessText = stringResource(R.string.payment_issue_dialog_premium_access)
    val paymentIssueActiveForNowText = stringResource(R.string.payment_issue_dialog_active_for_now)
    val paymentIssueNextStepText = stringResource(R.string.payment_issue_dialog_next_step)
    val paymentIssueUpdatePaymentShortText = stringResource(R.string.payment_issue_dialog_update_payment_short)

    // ✅ Dialog 放外層（不受 scroll 影響）
    key(localeKey) {
        DeleteAccountDialog(
            visible = showDeleteDialog,
            title = deleteDialogTitle,
            body = deleteDialogBody,
            cancelText = deleteDialogCancelText,
            deleteText = deleteDialogDeleteText,
            deletingText = deleteDialogDeletingText,
            closeContentDescription = deleteDialogCloseText,
            deleting = deleting,
            onDismiss = { if (!deleting) showDeleteDialog = false },
            onCancel = { if (!deleting) showDeleteDialog = false },
            onDelete = {
                if (deleting) return@DeleteAccountDialog

                // 先鎖 UI + 關 dialog，避免使用者連點
                deleting = true
                showDeleteDialog = false

                scope.launch {
                    try {
                        // 後端目前仍收 subscriptionWarningAcknowledged 參數。
                        // Dialog 已不顯示訂閱提示，所以這裡固定 true，避免 PREMIUM/TRIAL 用戶刪帳被後端擋下。
                        onDeleteAccount(true)
                    } finally {
                        deleting = false
                    }
                }
            }
        )
    }

    key(localeKey) {
        PaymentIssueDialog(
            visible = showPaymentIssueDialog,
            title = paymentIssueTitle,
            body = paymentIssueBody,
            supportingBody = paymentIssueSupportingBody,
            updatePaymentText = paymentIssueUpdatePaymentText,
            maybeLaterText = restoreDialogMaybeLaterText,
            closeText = restoreDialogCloseText,
            badgeText = paymentIssueBadgeText,
            premiumAccessText = paymentIssuePremiumAccessText,
            activeForNowText = paymentIssueActiveForNowText,
            nextStepText = paymentIssueNextStepText,
            updatePaymentShortText = paymentIssueUpdatePaymentShortText,
            onDismiss = {
                showPaymentIssueDialog = false
            },
            onUpdatePaymentMethod = {
                showPaymentIssueDialog = false
                onFixPaymentIssue()
            }
        )
    }

    key(localeKey) {
        RestoreSubscriptionDialog(
            uiState = restoreSubscriptionUiState,
            title = restoreDialogTitle,
            body = restoreDialogBody,
            closeText = restoreDialogCloseText,
            restoreText = restoreDialogRestoreText,
            restoringText = restoreDialogRestoringText,
            maybeLaterText = restoreDialogMaybeLaterText,
            onDismiss = onDismissRestoreSubscription,
            onMaybeLater = onMaybeLaterRestoreSubscription,
            onRestore = onRestoreSubscription
        )
    }

    Column(
        modifier = modifier
            .verticalScroll(scroll)
            .padding(horizontal = BiteCalScreenFrame.contentHorizontalMedium)
            .padding(top = BiteCalScreenFrame.settingsTop, bottom = BiteCalScreenFrame.settingsBottom)
    ) {
        ProfileCard(
            avatarUrl = avatarUrl,
            name = profileName,
            subtitle = ageText,
            premiumStatusKind = premiumStatusKind,
            premiumSubtitle = premiumStatusSubtitle,
            onProfileClick = onOpenEditName,
            onSubscriptionClick = onOpenSubscription,
            onPaymentIssueClick = { showPaymentIssueDialog = true }
        )

        Spacer(Modifier.height(14.dp))
        InviteFriendsCard(onClick = onOpenReferral)
        Spacer(Modifier.height(16.dp))

        SettingsListCard {
            SettingsRow(icon = Icons.Outlined.Person, title = stringResource(R.string.settings_personal_details), onClick = onOpenPersonalDetails)
            DividerThin()
            SettingsRow(icon = Icons.Outlined.Tune, title = stringResource(R.string.settings_adjust_macronutrients), onClick = onOpenAdjustMacros)
            DividerThin()
            SettingsRow(icon = Icons.Outlined.Widgets, title = stringResource(R.string.settings_weight_history), onClick = onOpenWeightHistory)
            DividerThin()
            SettingsRow(icon = Icons.Outlined.CalendarMonth, title = stringResource(R.string.settings_ring_colors_explained), onClick = onOpenRingColorsExplained
            )
            DividerThin()
            SettingsRow(icon = Icons.Outlined.Notifications, title = stringResource(R.string.settings_inbox), onClick = onOpenNotificationInbox)
        }

        Spacer(Modifier.height(18.dp))
        PreferencesCard(
            appearanceMode = appearanceMode,
            onOpenAppearance = onOpenAppearance,
            onOpenLanguage = onOpenLanguage
        )
        Spacer(Modifier.height(22.dp))
        WidgetsSection(
            summary = homeSummary,
            todayNutrition = todayNutrition,
            appearanceMode = appearanceMode,
            onOpenWidgetGuide = onOpenWidgetGuide
        )
        Spacer(Modifier.height(28.dp))

        SettingsListCard {
            SettingsRow(
                icon = Icons.Outlined.Description,
                title = stringResource(R.string.settings_terms_conditions),
                onClick = onOpenTerms
            )
            DividerThin()
            SettingsRow(
                icon = Icons.Outlined.PrivacyTip,
                title = stringResource(R.string.settings_privacy_policy),
                onClick = onOpenPrivacy
            )
            DividerThin()
            SettingsRow(
                icon = Icons.Outlined.Email,
                title = stringResource(R.string.settings_support_email),
                onClick = onOpenSupportEmail
            )
            DividerThin()
            SettingsRow(
                icon = Icons.Outlined.Restore,
                title = stringResource(R.string.settings_restore_subscription),
                onClick = onOpenRestoreSubscription
            )
            DividerThin()
            SettingsRow(
                icon = Icons.Outlined.Person,
                title = stringResource(R.string.settings_delete_account),
                onClick = { if (!deleting) showDeleteDialog = true }
            )
        }

        Spacer(Modifier.height(22.dp))

        LogoutButton(
            loading = logoutLoading,
            onLogout = onLogout
        )

        if (logoutErrorVisible) {
            Spacer(Modifier.height(10.dp))
            LogoutErrorMessage(
                retryEnabled = !logoutLoading,
                onRetry = onLogout
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF9CA3AF)),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
private fun ProfileCard(
    avatarUrl: Uri?,
    name: String,
    subtitle: String,
    premiumStatusKind: MembershipDisplayKind,
    premiumSubtitle: String,
    onProfileClick: () -> Unit,
    onSubscriptionClick: () -> Unit,
    onPaymentIssueClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val displayName = remember(name) { name.trim().ifBlank { "Guest" } }
    val subscriptionBadgeClickableModifier =
        when (premiumStatusKind) {
            MembershipDisplayKind.FREE -> {
                Modifier.biteCalClickable(onClick = onSubscriptionClick)
            }

            MembershipDisplayKind.PAYMENT_ISSUE -> {
                Modifier.biteCalClickable(onClick = onPaymentIssueClick)
            }

            MembershipDisplayKind.TRIAL,
            MembershipDisplayKind.PREMIUM -> {
                Modifier
            }
        }
    val colors = BiteCalColors.current()

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = settingsHomeAlignedCardContainer()),
        border = settingsHomeAlignedCardBorder(lightWidth = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .biteCalClickable(onClick = onProfileClick),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(url = avatarUrl)

                    Spacer(Modifier.size(12.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 2.dp, end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = displayName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(weight = 1f, fill = false),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary,
                                    fontSize = 19.sp,
                                    lineHeight = 23.sp
                                )
                            )

                            Spacer(Modifier.size(9.dp))

                            Box(
                                modifier = Modifier
                                    .offset(y = (0).dp)
                                    .size(22.dp)
                                    .biteCalClickable(onClick = onProfileClick),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit your name",
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = subtitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = (-6).dp, y = (-2).dp)
                    .padding(start = 8.dp, end = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                ProfileSubscriptionBadge(
                    kind = premiumStatusKind,
                    subtitle = premiumSubtitle,
                    modifier = Modifier.then(subscriptionBadgeClickableModifier)
                )
            }
        }
    }
}

@Composable
private fun ProfileSubscriptionBadge(
    kind: MembershipDisplayKind,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val colors = BiteCalColors.current()
    val isDark = HomeCardStyles.isDark()
    val visual = remember(kind) {
        ProfileSubscriptionVisual.from(kind)
    }
    val badgeBorderColor = if (isDark) visual.darkBorderColor else visual.borderColor
    val subtitleColor = if (isDark) colors.textPrimary else visual.subtitleColor

    val dotSize = 8.dp
    val dotLabelGap = 7.dp
    val horizontalPadding = 11.dp

    Column(
        modifier = modifier.padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .height(30.dp)
                .widthIn(min = 58.dp, max = 132.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    brush = Brush.linearGradient(visual.backgroundColors)
                )
                .border(
                    width = 1.dp,
                    color = badgeBorderColor,
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (visual.showDot) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(visual.dotColor)
                )

                Spacer(Modifier.size(dotLabelGap))
            }

            Text(
                text = stringResource(visual.labelRes),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = visual.textColor,
                    fontSize = 13.sp,
                    lineHeight = 15.5.sp,
                    letterSpacing = 0.24.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(5.dp))

        Text(
            text = subtitle.ifBlank { stringResource(visual.fallbackSubtitleRes) },
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall.copy(
                color = subtitleColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.5.sp,
                lineHeight = 14.5.sp
            )
        )
    }
}

private data class ProfileSubscriptionVisual(
    @StringRes val labelRes: Int,
    @StringRes val fallbackSubtitleRes: Int,
    val backgroundColors: List<Color>,
    val borderColor: Color,
    val darkBorderColor: Color,
    val dotColor: Color,
    val textColor: Color,
    val subtitleColor: Color,
    val showDot: Boolean = true
) {
    companion object {
        fun from(kind: MembershipDisplayKind): ProfileSubscriptionVisual {
            return when (kind) {
                MembershipDisplayKind.PAYMENT_ISSUE -> {
                    ProfileSubscriptionVisual(
                        labelRes = R.string.settings_membership_payment,
                        fallbackSubtitleRes = R.string.settings_membership_update_payment,
                        backgroundColors = listOf(
                            Color(0xFFFFF7F7),
                            Color(0xFFFFF1F2)
                        ),
                        borderColor = Color(0xFFFFD6DD),
                        darkBorderColor = Color(0xFFFF8A8A).copy(alpha = 0.64f),
                        dotColor = Color(0xFFE85D75),
                        textColor = Color(0xFFA94A58),
                        subtitleColor = Color(0xFFC06F7B)
                    )
                }

                MembershipDisplayKind.PREMIUM -> {
                    ProfileSubscriptionVisual(
                        labelRes = R.string.settings_membership_premium,
                        fallbackSubtitleRes = R.string.settings_membership_active_member,
                        backgroundColors = listOf(
                            Color(0xFF111114),
                            Color(0xFF18181B)
                        ),
                        borderColor = Color(0xFF111114),
                        darkBorderColor = Color(0xFFE7C873).copy(alpha = 0.58f),
                        dotColor = Color(0xFFE7C873),
                        textColor = Color.White,
                        subtitleColor = Color(0xFF71717A)
                    )
                }

                MembershipDisplayKind.TRIAL -> {
                    ProfileSubscriptionVisual(
                        labelRes = R.string.settings_membership_trial,
                        fallbackSubtitleRes = R.string.settings_membership_access_active,
                        backgroundColors = listOf(
                            Color(0xFFF0FDF4),
                            Color(0xFFDCFCE7)
                        ),
                        borderColor = Color(0xFFBBF7D0),
                        darkBorderColor = Color(0xFF5ECB7A).copy(alpha = 0.58f),
                        dotColor = Color.Transparent,
                        textColor = Color(0xFF15803D),
                        subtitleColor = Color(0xFF2F9E5E),

                        // TRIAL 不顯示 dot，讓 pill 寬度更自然，文字也更容易跟副文字置中
                        showDot = false
                    )
                }
                MembershipDisplayKind.FREE -> {
                    ProfileSubscriptionVisual(
                        labelRes = R.string.settings_membership_free,
                        fallbackSubtitleRes = R.string.settings_membership_upgrade,
                        backgroundColors = listOf(
                            Color(0xFFF4F4F5),
                            Color(0xFFEFEFF1)
                        ),
                        borderColor = Color(0xFFE4E4E7),
                        darkBorderColor = Color(0xFF4A4558),
                        dotColor = Color.Transparent,
                        textColor = Color(0xFF3F3F46),
                        subtitleColor = Color(0xFF52525B),
                        // FREE 不是 active 狀態，不顯示 dot，避免 FREE 文字跟 Upgrade 視覺不對齊
                        showDot = false
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar(url: Uri?) {
    val ctx = LocalContext.current

    val avatarModifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(Color(0xFFF1F2F4))

    if (url == null) {
        DefaultProfileAvatarPlaceholder(
            modifier = avatarModifier
        )
        return
    }

    val req = remember(url) {
        ImageRequest.Builder(ctx)
            .data(url)
            .crossfade(false)
            .allowHardware(true)
            .build()
    }

    SubcomposeAsyncImage(
        model = req,
        contentDescription = "頭像",
        modifier = avatarModifier,
        contentScale = ContentScale.Crop,
        loading = {
            DefaultProfileAvatarPlaceholder(
                modifier = Modifier.fillMaxSize()
            )
        },
        error = {
            DefaultProfileAvatarPlaceholder(
                modifier = Modifier.fillMaxSize()
            )
        },
        success = {
            SubcomposeAsyncImageContent()
        }
    )
}

@Composable
private fun DefaultProfileAvatarPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFFF1F2F4)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "預設頭像",
            tint = Color(0xFF111114),
            modifier = Modifier.size(25.dp)
        )
    }
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun InviteFriendsCard(
    onClick: () -> Unit
) {
    val outerShape = RoundedCornerShape(24.dp)
    val panelShape = RoundedCornerShape(22.dp)

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = spring(
            dampingRatio = 0.78f,
            stiffness = 520f
        ),
        label = "InviteFriendsCardScale"
    )
    val colors = BiteCalColors.current()

    Card(
        shape = outerShape,
        colors = CardDefaults.cardColors(containerColor = settingsHomeAlignedCardContainer()),
        border = settingsHomeAlignedCardBorder(lightWidth = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .biteCalClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    tint = colors.textPrimary,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(Modifier.size(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.settings_invite_friends),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                            fontSize = 17.sp,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(panelShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF252B55),
                                Color(0xFF3A2B55),
                                Color(0xFF633A4B),
                                Color(0xFF603844)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.14f),
                        shape = panelShape
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎁",
                                    fontSize = 19.sp,
                                    lineHeight = 23.sp
                                )

                                Spacer(Modifier.size(8.dp))

                                Text(
                                    text = stringResource(R.string.settings_premium_reward),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color(0xFFFFE7A3),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        lineHeight = 15.sp
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color.White.copy(alpha = 0.13f))
                                        .border(
                                            width = 1.dp,
                                            color = Color.White.copy(alpha = 0.16f),
                                            shape = RoundedCornerShape(999.dp)
                                        )
                                        .padding(horizontal = 9.dp, vertical = 5.dp)
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = stringResource(R.string.settings_share_bitecal_with_friends),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    lineHeight = 26.sp
                                )
                            )
                        }

                        Spacer(Modifier.size(12.dp))

                        InviteRewardVisual(
                            modifier = Modifier.size(86.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val rewardDescFontSize = when {
                            maxWidth < 300.dp -> 10.sp
                            maxWidth < 340.dp -> 11.sp
                            else -> 12.sp
                        }

                        Text(
                            text = stringResource(R.string.settings_referral_reward_content),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.84f),
                                fontWeight = FontWeight.Medium,
                                fontSize = rewardDescFontSize,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (HomeCardStyles.isDark()) {
                                    HomeCardStyles.Action.secondaryContainer()
                                } else {
                                    Color.White
                                }
                            )
                            .border(
                                width = if (HomeCardStyles.isDark()) 1.dp else 0.dp,
                                color = if (HomeCardStyles.isDark()) {
                                    HomeCardStyles.Action.secondaryBorder()
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(999.dp)
                            )
                            .padding(horizontal = BiteCalScreenFrame.contentHorizontalMedium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.settings_get_30_days_free),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (HomeCardStyles.isDark()) {
                                    HomeCardStyles.Action.secondaryContent()
                                } else {
                                    Color(0xFF111114)
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )

                        Spacer(Modifier.size(8.dp))

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (HomeCardStyles.isDark()) {
                                        HomeCardStyles.Action.addContainer()
                                    } else {
                                        Color(0xFF111114)
                                    }
                                )
                                .border(
                                    width = if (HomeCardStyles.isDark()) 0.8.dp else 0.dp,
                                    color = if (HomeCardStyles.isDark()) {
                                        HomeCardStyles.Action.addBorder()
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = if (HomeCardStyles.isDark()) {
                                    HomeCardStyles.Action.addContent()
                                } else {
                                    Color.White
                                },
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun InviteRewardVisual(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.28f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF111114))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.18f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "30",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 29.sp,
                            lineHeight = 30.sp
                        )
                    )

                    Text(
                        text = stringResource(R.string.settings_days_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.92f),
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            lineHeight = 11.sp
                        )
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(27.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 1.dp, y = 1.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE7A3))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.75f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎁",
                fontSize = 14.sp,
                lineHeight = 16.sp
            )
        }

        Box(
            modifier = Modifier
                .size(10.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-5).dp, y = 8.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.34f))
        )
    }
}

@Composable
private fun PreferencesCard(
    appearanceMode: AppearanceMode,
    onOpenAppearance: () -> Unit,
    onOpenLanguage: () -> Unit
) {
    val colors = BiteCalColors.current()
    val appearance = stringResource(
        when (appearanceMode) {
            AppearanceMode.LIGHT -> R.string.settings_appearance_light
            AppearanceMode.DARK -> R.string.settings_appearance_dark
        }
    )

    SettingsListCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                tint = colors.textPrimary
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = stringResource(R.string.settings_preferences),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        DividerThin()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .biteCalClickable(onClick = onOpenAppearance)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Palette,
                contentDescription = null,
                tint = colors.textPrimary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.size(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_appearance),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (HomeCardStyles.isDark()) HomeCardStyles.Text.primary() else colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Text(
                text = appearance,
                style = MaterialTheme.typography.titleMedium.copy(color = colors.textPrimary)
            )

            PreferencesExpandArrow(
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        DividerThin()

        SettingsRow(
            icon = Icons.Outlined.Language,
            title = stringResource(R.string.settings_language),
            onClick = onOpenLanguage
        )
    }
}

@Composable
private fun PreferencesExpandArrow(
    modifier: Modifier = Modifier
) {
    val colors = BiteCalColors.current()

    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(
                if (HomeCardStyles.isDark()) {
                    HomeCardStyles.Surface.raisedAlt()
                } else {
                    colors.surfaceMuted.copy(alpha = 0.78f)
                }
            )
            .border(
                width = 1.dp,
                color = if (HomeCardStyles.isDark()) {
                    HomeCardStyles.Surface.borderColor()
                } else {
                    colors.border.copy(alpha = 0.72f)
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowDown,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun AppearanceModeDialog(
    currentMode: AppearanceMode,
    onPick: (AppearanceMode) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = BiteCalColors.current()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            tonalElevation = 0.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_appearance_mode_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 25.sp
                    )
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.settings_appearance_mode_body),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colors.textSecondary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(Modifier.height(16.dp))

                AppearanceModeOption(
                    titleRes = R.string.settings_appearance_light,
                    descriptionRes = R.string.settings_appearance_light_description,
                    selected = currentMode == AppearanceMode.LIGHT,
                    onClick = { onPick(AppearanceMode.LIGHT) }
                )

                Spacer(Modifier.height(10.dp))

                AppearanceModeOption(
                    titleRes = R.string.settings_appearance_dark,
                    descriptionRes = R.string.settings_appearance_dark_description,
                    selected = currentMode == AppearanceMode.DARK,
                    onClick = { onPick(AppearanceMode.DARK) }
                )
            }
        }
    }
}

@Composable
private fun AppearanceModeOption(
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = BiteCalColors.current()
    val accent = if (selected) Color(0xFFFF9F43) else colors.textMuted
    val container = if (selected) {
        Color(0xFFFF9F43).copy(alpha = if (colors.background == BiteCalColors.Dark.background) 0.18f else 0.10f)
    } else {
        colors.surfaceMuted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(container)
            .border(
                width = 1.dp,
                color = if (selected) accent.copy(alpha = 0.50f) else colors.border,
                shape = RoundedCornerShape(18.dp)
            )
            .biteCalClickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (selected) accent.copy(alpha = 0.18f) else colors.surface)
                .border(
                    width = 1.dp,
                    color = if (selected) accent.copy(alpha = 0.48f) else colors.border,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(if (selected) 14.dp else 8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 21.sp
                )
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(descriptionRes),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
private fun WidgetsSection(
    summary: HomeSummary?,
    todayNutrition: HomeTodayNutritionSummary,
    appearanceMode: AppearanceMode,
    onOpenWidgetGuide: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val isDark = HomeCardStyles.isDark()

    LaunchedEffect(summary, todayNutrition, appearanceMode) {
        val widgetSnapshotChanged = BiteCalWidgetSnapshotStore.saveFrom(
            context = context,
            summary = summary,
            todayNutrition = todayNutrition,
            isDarkAppearance = appearanceMode == AppearanceMode.DARK
        )
        if (widgetSnapshotChanged) {
            BiteCalHomeWidgetUpdater.updateAll(context)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_widgets_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium,
                color = if (isDark) HomeCardStyles.Text.primary() else Color(0xFF5B6472),
                fontSize = 19.sp,
                lineHeight = 24.sp
            )
        )

        Text(
            text = stringResource(R.string.settings_widgets_how_to_add),
            style = MaterialTheme.typography.titleMedium.copy(
                color = if (isDark) HomeCardStyles.Text.primary() else Color(0xFF2F3137),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            modifier = Modifier.biteCalClickable(onClick = onOpenWidgetGuide)
        )
    }

    Spacer(Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        CaloriesWidgetPreviewCard(
            summary = summary,
            todayNutrition = todayNutrition
        )
        MacroActionsWidgetPreviewCard(
            summary = summary,
            todayNutrition = todayNutrition
        )
    }
}

@Composable
private fun CaloriesWidgetPreviewCard(
    summary: HomeSummary?,
    todayNutrition: HomeTodayNutritionSummary,
    modifier: Modifier = Modifier
) {
    val dash = stringResource(R.string.common_dash)
    val logFoodTextYOffset = if (currentLocaleKey().startsWith("zh", ignoreCase = true)) {
        (-1).dp
    } else {
        0.dp
    }
    val goalKcal = summary?.tdee?.coerceAtLeast(0)
    val valueText = goalKcal
        ?.let { remainingWidgetValue(goal = it, eaten = todayNutrition.eatenKcal).toString() }
        ?: dash
    val progress = widgetNutritionProgress(
        current = todayNutrition.eatenKcal,
        goal = goalKcal
    )
    val isDark = HomeCardStyles.isDark()
    val logFoodShape = RoundedCornerShape(999.dp)

    Card(
        modifier = modifier
            .size(width = 148.dp, height = 155.dp),
        shape = CardStyles.Corner,
        colors = CardDefaults.cardColors(containerColor = settingsHomeAlignedCardContainer()),
        border = settingsHomeAlignedCardBorder(lightWidth = 1.2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .padding(top = 10.dp, bottom = 12.dp)
        ) {
            WidgetCaloriesRing(
                value = valueText,
                label = stringResource(R.string.settings_calories_goal_label),
                progress = progress,
                ringSize = 94.dp,
                modifier = Modifier
                    .size(94.dp)
                    .align(Alignment.TopCenter)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(30.dp)
                    .clip(logFoodShape)
                    .background(
                        if (isDark) {
                            HomeCardStyles.Action.secondaryContainer()
                        } else {
                            Color(0xFF111114)
                        }
                    )
                    .border(
                        width = if (isDark) 1.dp else 0.dp,
                        color = if (isDark) HomeCardStyles.Action.secondaryBorder() else Color.Transparent,
                        shape = logFoodShape
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 5.dp)
                        .size(21.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDark) {
                                Color(0xFF111114)
                            } else {
                                Color.White
                            }
                        )
                        .border(width = 0.dp, color = Color.Transparent, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val plusColor = if (isDark) Color.White else Color(0xFF111114)
                    Box(
                        modifier = Modifier.size(11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(11.dp)
                                .height(2.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(plusColor)
                        )
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(11.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(plusColor)
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.log_your_food),
                    color = if (isDark) HomeCardStyles.Action.secondaryContent() else Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 15.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = logFoodTextYOffset)
                        .fillMaxWidth()
                        .padding(start = 28.dp, end = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun MacroActionsWidgetPreviewCard(
    summary: HomeSummary?,
    todayNutrition: HomeTodayNutritionSummary,
    modifier: Modifier = Modifier
) {
    val dash = stringResource(R.string.common_dash)
    val goalKcal = summary?.tdee?.coerceAtLeast(0)
    val proteinGoal = summary?.proteinG?.coerceAtLeast(0)
    val carbsGoal = summary?.carbsG?.coerceAtLeast(0)
    val fatsGoal = summary?.fatG?.coerceAtLeast(0)
    val caloriesLeft = goalKcal?.let { remainingWidgetValue(it, todayNutrition.eatenKcal) }
    val proteinLeft = proteinGoal?.let { remainingWidgetValue(it, todayNutrition.eatenProteinG) }
    val carbsLeft = carbsGoal?.let { remainingWidgetValue(it, todayNutrition.eatenCarbsG) }
    val fatsLeft = fatsGoal?.let { remainingWidgetValue(it, todayNutrition.eatenFatsG) }
    val isDark = HomeCardStyles.isDark()

    Card(
        modifier = modifier
            .size(width = 364.dp, height = 155.dp),
        shape = CardStyles.Corner,
        colors = CardDefaults.cardColors(containerColor = settingsHomeAlignedCardContainer()),
        border = settingsHomeAlignedCardBorder(lightWidth = 1.2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WidgetCaloriesRing(
                    value = caloriesLeft?.toString() ?: dash,
                    label = stringResource(R.string.settings_calories_goal_label),
                    progress = widgetNutritionProgress(
                        current = todayNutrition.eatenKcal,
                        goal = goalKcal
                    ),
                    ringSize = 108.dp,
                    modifier = Modifier.size(108.dp)
                )

                Spacer(Modifier.size(18.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    WidgetMacroStatRow(
                        iconRes = R.drawable.ic_widget_protein,
                        iconTint = if (isDark) HomeCardStyles.Palette.protein() else Color(0xFFE56C6C),
                        iconBackground = if (isDark) HomeCardStyles.Ring.centerFill() else Color(0xFFF7F5F7),
                        value = proteinLeft?.let { "${it}g" } ?: dash,
                        label = stringResource(R.string.settings_protein_goal_label),
                        progress = widgetNutritionProgress(
                            current = todayNutrition.eatenProteinG,
                            goal = proteinGoal
                        ),
                        iconSize = 20.dp
                    )
                    WidgetMacroStatRow(
                        iconRes = R.drawable.ic_widget_carbs,
                        iconTint = HomeCardStyles.Palette.Carbs,
                        iconBackground = if (isDark) HomeCardStyles.Ring.centerFill() else Color(0xFFF8F6F3),
                        value = carbsLeft?.let { "${it}g" } ?: dash,
                        label = stringResource(R.string.settings_carbs_goal_label),
                        progress = widgetNutritionProgress(
                            current = todayNutrition.eatenCarbsG,
                            goal = carbsGoal
                        ),
                        iconSize = 20.dp
                    )
                    WidgetMacroStatRow(
                        iconRes = R.drawable.ic_widget_fats,
                        iconTint = if (isDark) HomeCardStyles.Palette.fats() else Color(0xFF6C93D8),
                        iconBackground = if (isDark) HomeCardStyles.Ring.centerFill() else Color(0xFFF3F6FB),
                        value = fatsLeft?.let { "${it}g" } ?: dash,
                        label = stringResource(R.string.settings_fats_goal_label),
                        progress = widgetNutritionProgress(
                            current = todayNutrition.eatenFatsG,
                            goal = fatsGoal
                        ),
                        iconSize = 16.dp
                    )
                }
            }

            Spacer(Modifier.size(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(
                        if (isDark) {
                            HomeCardStyles.Surface.borderColor()
                        } else {
                            Color(0xFFE9EAEE)
                        }
                    )
            )

            Spacer(Modifier.size(14.dp))

            Column(
                modifier = Modifier.width(88.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WidgetActionTile(
                    label = stringResource(R.string.settings_widget_scan_food),
                    icon = { ScanFocusGlyph() }
                )
                WidgetActionTile(
                    label = stringResource(R.string.settings_widget_barcode),
                    icon = { BarcodeGlyph() }
                )
            }
        }
    }
}

@Composable
private fun WidgetCaloriesRing(
    value: String,
    label: String,
    progress: Float,
    ringSize: Dp,
    modifier: Modifier = Modifier
) {
    val isDark = HomeCardStyles.isDark()
    val ringTrackColor = if (isDark) HomeCardStyles.Ring.track() else Color(0xFFEAEAED)
    val ringProgressColor = HomeCardStyles.Palette.caloriesIcon()
    val valueColor = HomeCardStyles.Text.metricPrimary()
    val labelColor = if (isDark) HomeCardStyles.Text.label() else Color(0xFF3F3F46)
    val labelEmphasisColor = if (isDark) HomeCardStyles.Text.secondary() else Color(0xFF18181B)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        GaugeRing(
            progress = progress,
            sizeDp = ringSize,
            strokeDp = 7.3.dp,
            trackColor = ringTrackColor,
            progressColor = ringProgressColor,
            drawTopTick = true,
            tickColor = ringProgressColor
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = valueColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 26.sp
                )
            )
            Spacer(Modifier.height(2.dp))
            WidgetRemainingLabel(
                text = label,
                color = labelColor,
                emphasisColor = labelEmphasisColor,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )
            )
        }
    }
}

@Composable
private fun WidgetMacroStatRow(
    iconRes: Int,
    iconTint: Color,
    iconBackground: Color,
    value: String,
    label: String,
    progress: Float,
    iconSize: Dp = 16.dp
) {
    val isDark = HomeCardStyles.isDark()
    val ringTrackColor = if (isDark) HomeCardStyles.Ring.track() else Color(0xFFEAEAED)
    val valueColor = HomeCardStyles.Text.metricPrimary()
    val labelColor = if (isDark) HomeCardStyles.Text.label() else Color(0xFF3F3F46)
    val labelEmphasisColor = if (isDark) HomeCardStyles.Text.secondary() else Color(0xFF18181B)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(30.dp),
            contentAlignment = Alignment.Center
        ) {
            GaugeRing(
                progress = progress,
                sizeDp = 30.dp,
                strokeDp = 2.8.dp,
                trackColor = ringTrackColor,
                progressColor = iconTint,
                drawTopTick = true,
                tickColor = iconTint,
                tickRadiusScale = 0.55f
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        Spacer(Modifier.size(10.dp))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = valueColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 15.sp
                )
            )
            Spacer(Modifier.height(1.dp))
            WidgetRemainingLabel(
                text = label,
                color = labelColor,
                emphasisColor = labelEmphasisColor,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )
            )
        }
    }
}

@Composable
private fun WidgetRemainingLabel(
    text: String,
    color: Color,
    emphasisColor: Color,
    style: androidx.compose.ui.text.TextStyle
) {
    val splitIndex = text.lastIndexOf(' ')
    if (splitIndex <= 0 || splitIndex >= text.lastIndex) {
        Text(
            text = text,
            color = emphasisColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = style.copy(fontWeight = FontWeight.SemiBold)
        )
        return
    }

    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append(text.take(splitIndex))
                append(" ")
            }
            withStyle(
                SpanStyle(
                    color = emphasisColor,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append(text.drop(splitIndex + 1))
            }
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = style
    )
}

private fun widgetNutritionProgress(current: Int?, goal: Int?): Float {
    val c = (current ?: 0).coerceAtLeast(0)
    val g = (goal ?: 0).coerceAtLeast(0)
    if (g <= 0) return 0f
    return (c.toFloat() / g.toFloat()).coerceIn(0f, 1f)
}

private fun remainingWidgetValue(goal: Int, eaten: Int): Int {
    return goal.coerceAtLeast(0) - eaten.coerceAtLeast(0)
}

@Composable
private fun WidgetActionTile(
    label: String,
    icon: @Composable () -> Unit
) {
    val isDark = HomeCardStyles.isDark()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isDark) {
                    HomeCardStyles.Surface.raised()
                } else {
                    Color(0xFFF7F7F8)
                }
            )
            .border(
                width = if (isDark) 0.8.dp else 0.dp,
                color = if (isDark) HomeCardStyles.Surface.borderColor() else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(vertical = 6.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isDark) {
                        HomeCardStyles.Ring.centerFill()
                    } else {
                        Color(0xFFF1F2F4)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(Modifier.height(2.dp))

        Text(
            text = label,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isDark) HomeCardStyles.Text.secondary() else Color(0xFF111114),
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 13.sp
            )
        )
    }
}
@Composable
private fun ScanFocusGlyph() {
    val ink = if (HomeCardStyles.isDark()) HomeCardStyles.Action.addContent() else Color(0xFF111114)

    Box(
        modifier = Modifier.size(16.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(width = 5.8.dp, height = 1.8.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(width = 1.8.dp, height = 5.8.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(width = 5.8.dp, height = 1.8.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(width = 1.8.dp, height = 5.8.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(width = 5.8.dp, height = 1.8.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(width = 1.8.dp, height = 5.8.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(width = 5.8.dp, height = 1.8.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(width = 1.8.dp, height = 5.8.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .size(4.6.dp)
                .clip(CircleShape)
                .background(ink)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun BarcodeGlyph() {
    val ink = if (HomeCardStyles.isDark()) HomeCardStyles.Action.addContent() else Color(0xFF111114)

    Row(
        modifier = Modifier.size(width = 18.dp, height = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(1.2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 1.7.dp, height = 10.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .size(width = 2.2.dp, height = 14.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .size(width = 1.7.dp, height = 10.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .size(width = 2.2.dp, height = 13.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .size(width = 1.7.dp, height = 10.dp)
                .background(ink)
        )
        Box(
            modifier = Modifier
                .size(width = 2.2.dp, height = 14.dp)
                .background(ink)
        )
    }
}


@Composable
private fun SettingsListCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = settingsHomeAlignedCardContainer()),
        border = settingsHomeAlignedCardBorder(lightWidth = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) { content() }
}

@Composable
private fun settingsHomeAlignedCardContainer(): Color {
    val colors = BiteCalColors.current()
    return if (HomeCardStyles.isDark()) HomeCardStyles.Surface.card() else colors.surface
}

@Composable
private fun settingsHomeAlignedCardBorder(lightWidth: Dp = 1.dp): BorderStroke {
    val colors = BiteCalColors.current()
    return if (HomeCardStyles.isDark()) {
        HomeCardStyles.Surface.border()
    } else {
        BorderStroke(width = lightWidth, color = colors.border)
    }
}

@Composable
private fun settingsHomeAlignedDividerColor(): Color {
    val colors = BiteCalColors.current()
    return if (HomeCardStyles.isDark()) HomeCardStyles.Surface.borderColor() else colors.border
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .biteCalClickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val colors = BiteCalColors.current()

        Icon(icon, contentDescription = null, tint = colors.textPrimary)
        Spacer(Modifier.size(12.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun DividerThin() {
    HorizontalDivider(color = settingsHomeAlignedDividerColor(), thickness = 1.dp)
}

@Composable
private fun LogoutButton(
    loading: Boolean,
    onLogout: () -> Unit
) {
    val enabled = !loading
    val colors = BiteCalColors.current()
    val isDark = HomeCardStyles.isDark()
    val containerColor = if (isDark) HomeCardStyles.Surface.card() else colors.surface
    val borderColor = if (isDark) HomeCardStyles.Surface.borderColor() else colors.border
    val contentColor = if (enabled) {
        if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    } else {
        if (isDark) HomeCardStyles.Text.muted() else colors.textMuted
    }

    BiteCalSecondaryOutlinedButton(
        text = stringResource(
            if (loading) {
                R.string.settings_logout_loading
            } else {
                R.string.settings_logout
            }
        ),
        onClick = onLogout,
        enabled = enabled,
        height = 56.dp,
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (enabled) borderColor else borderColor.copy(alpha = 0.62f),
        contentColor = contentColor,
        containerColor = containerColor,
        disabledContainerColor = containerColor,
    )
}

@Composable
private fun LogoutErrorMessage(
    retryEnabled: Boolean,
    onRetry: () -> Unit
) {
    val retryClick = rememberClickWithHaptic(enabled = retryEnabled, onClick = onRetry)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_logout_failed),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.weight(1f)
        )
        BiteCalSecondaryOutlinedButton(
            text = stringResource(R.string.common_retry),
            onClick = onRetry,
            enabled = retryEnabled,
            height = 40.dp,
        )
    }
}
