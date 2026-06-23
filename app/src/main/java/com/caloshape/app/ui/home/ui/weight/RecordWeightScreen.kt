package com.caloshape.app.ui.home.ui.weight

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.caloshape.app.R
import com.caloshape.app.ui.common.haptic.caloShapeClickable
import com.caloshape.app.data.profile.repo.UserProfileStore
import com.caloshape.app.data.profile.repo.kgToLbs1
import com.caloshape.app.data.profile.repo.lbsToKg1
import androidx.compose.material3.CircularProgressIndicator
import com.caloshape.app.ui.common.design.CaloShapeTopBar
import com.caloshape.app.ui.home.ui.weight.model.WeightViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.min
import com.caloshape.app.ui.common.haptic.HapticWheelTickEffect
import com.caloshape.app.ui.common.haptic.rememberClickWithHaptic
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.common.design.CaloShapeScreenFrame
import com.caloshape.app.ui.home.components.HomeBackground
import com.caloshape.app.ui.home.components.HomeCardStyles

/* =========================================================
 * 外層 wrapper：負責找 ActivityResultRegistryOwner 並塞進 Local
 * ========================================================= */

@Composable
fun RecordWeightScreen(
    vm: WeightViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    rowHeight: Dp = 56.dp
) {
    val outerContext = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val localOwner = LocalActivityResultRegistryOwner.current
    val ownerFromContext = remember(outerContext) {
        outerContext.findActivityResultRegistryOwner()
    }

    val effectiveOwner = localOwner ?: ownerFromContext
    val canUseActivityResult = !isPreview && effectiveOwner != null

    if (effectiveOwner != null && localOwner == null) {
        CompositionLocalProvider(LocalActivityResultRegistryOwner provides effectiveOwner) {
            RecordWeightScreenContent(
                vm = vm,
                onBack = onBack,
                onSaved = onSaved,
                rowHeight = rowHeight,
                canUseActivityResult = canUseActivityResult
            )
        }
    } else {
        RecordWeightScreenContent(
            vm = vm,
            onBack = onBack,
            onSaved = onSaved,
            rowHeight = rowHeight,
            canUseActivityResult = canUseActivityResult
        )
    }
}

/* =========================================================
 * 內層真正的畫面：只看 canUseActivityResult
 * ========================================================= */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun RecordWeightScreenContent(
    vm: WeightViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    rowHeight: Dp,
    canUseActivityResult: Boolean
) {
    val ui by vm.ui.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val screenBackground = if (isDark) Color.Transparent else colors.background
    val helperTextColor = if (isDark) HomeCardStyles.Text.muted() else colors.textMuted
    val wheelAccentTextColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val buttonContainerColor = if (isDark) Color.White.copy(alpha = 0.92f) else colors.primaryButtonContainer
    val buttonContentColor = if (isDark) Color(0xFF111114) else colors.primaryButtonContent

    // 1) KG / LBS 範圍
    val kgMin = 20.0
    val kgMax = 800.0
    val lbsTenthsMin = kgToLbsTenthsRecord(kgMin)
    val lbsTenthsMax = kgToLbsTenthsRecord(kgMax)
    val lbsIntMin = lbsTenthsMin / 10
    val lbsIntMax = lbsTenthsMax / 10

    // 2) 日期狀態（預設今天）
    val today = remember { LocalDate.now() }
    var selectedDate by rememberSaveable { mutableStateOf(today) }
    val dateFormatterDisplay = remember { DateTimeFormatter.ofPattern("yyyy/MM/dd") }
    var showDateSheet by remember { mutableStateOf(false) }

    // 3) ⚠️ 初始值只做一次（避免 ui refresh 時輪盤跳回）
    var initialized by rememberSaveable { mutableStateOf(false) }
    var useMetric by rememberSaveable { mutableStateOf(true) }
    var valueKg by rememberSaveable { mutableDoubleStateOf(70.0) }
    var valueLbsTenths by rememberSaveable { mutableIntStateOf(kgToLbsTenthsRecord(70.0)) }

    LaunchedEffect(ui.unit, ui.current, ui.profileWeightKg) {
        if (initialized) return@LaunchedEffect
        initialized = true

        useMetric = (ui.unit == UserProfileStore.WeightUnit.KG)

        val initialKgRaw = ui.current ?: ui.profileWeightKg ?: 70.0
        val initialKg = initialKgRaw.coerceIn(kgMin, kgMax)
        valueKg = initialKg
        valueLbsTenths = kgToLbsTenthsRecord(initialKg).coerceIn(lbsTenthsMin, lbsTenthsMax)
    }

    // 4) 從 value 推 wheel 選中值
    val kgTenths = (valueKg * 10.0).toInt()
        .coerceIn((kgMin * 10).toInt(), (kgMax * 10).toInt())
    val kgIntSel = kgTenths / 10
    val kgDecSel = kgTenths % 10

    val lbsTenthsClamped = valueLbsTenths.coerceIn(lbsTenthsMin, lbsTenthsMax)
    val lbsIntSel = lbsTenthsClamped / 10
    val lbsDecSel = lbsTenthsClamped % 10

    // 5) 照片：Uri string（可 saveable）
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }

    // ✅ 新增：相機權限「拒絕次數」計數（拒絕兩次後，第三次點擊導設定）
    var cameraDenyCount by rememberSaveable { mutableIntStateOf(0) }

    // ========= ✅ Camera permission helpers（新增） =========
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    // 拍照（回傳 Bitmap，再存成 cache 檔）
    val takePhotoLauncher =
        if (canUseActivityResult) {
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { bitmap: Bitmap? ->
                if (bitmap != null) {
                    val file = bitmapToCacheFile(context, bitmap)
                    // 會是 file://...
                    photoUriString = Uri.fromFile(file).toString()
                    Log.d("RecordWeightScreen", "camera photo file=$file")
                } else {
                    Log.d("RecordWeightScreen", "camera returned null bitmap")
                }
            }
        } else null

    // ✅ Request CAMERA runtime permission（新增）
    val requestCameraPermissionLauncher =
        if (canUseActivityResult) {
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    // ✅ 授權成功：歸零 + 立刻開相機
                    cameraDenyCount = 0
                    runCatching { takePhotoLauncher?.launch(null) }
                        .onFailure { e ->
                            Log.e("RecordWeightScreen", "Camera launch after permission failed", e)
                            Toast.makeText(
                                context,
                                context.getString(R.string.record_weight_camera_failed_to_open),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    // ✅ 拒絕：累加（但不在這裡導設定，除非已經「不再詢問」）
                    cameraDenyCount += 1
                    Toast.makeText(
                        context,
                        context.getString(R.string.record_weight_camera_permission_required),
                        Toast.LENGTH_SHORT
                    ).show()

                    val act = context.findActivity()
                    val dontAskAgain = act != null &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(
                                act,
                                Manifest.permission.CAMERA
                            )

                    // ✅ 若已勾「不再詢問」：系統不會再彈窗，直接導設定頁
                    if (dontAskAgain) {
                        openAppSettings()
                    }
                }
            }
        } else null

    fun launchTakePhoto() {
        if (takePhotoLauncher == null) {
            val msg = if (isPreview) {
                context.getString(R.string.record_weight_camera_preview_unavailable)
            } else {
                context.getString(R.string.record_weight_camera_screen_unavailable)
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ 已有權限：順便歸零（例如從設定頁回來後）
        if (hasCameraPermission()) {
            if (cameraDenyCount != 0) cameraDenyCount = 0
            try {
                takePhotoLauncher.launch(null)
            } catch (e: ActivityNotFoundException) {
                Log.e("RecordWeightScreen", "No camera app can handle ACTION_IMAGE_CAPTURE", e)
                Toast.makeText(
                    context,
                    context.getString(R.string.record_weight_no_camera_app_found),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: SecurityException) {
                Log.e("RecordWeightScreen", "Camera launch blocked by SecurityException", e)
                Toast.makeText(
                    context,
                    context.getString(R.string.record_weight_camera_permission_required),
                    Toast.LENGTH_SHORT
                ).show()
                openAppSettings()
            } catch (e: Throwable) {
                Log.e("RecordWeightScreen", "Camera launch failed", e)
                Toast.makeText(
                    context,
                    context.getString(R.string.record_weight_camera_failed_to_open),
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        // ✅ 沒權限：拒絕兩次後，第三次點擊直接導設定（不再彈權限）
        if (cameraDenyCount >= 2) {
            Toast.makeText(
                context,
                context.getString(R.string.record_weight_enable_camera_permission_settings),
                Toast.LENGTH_SHORT
            ).show()
            openAppSettings()
            return
        }

        // ✅ 第 1~2 次：仍彈權限視窗
        requestCameraPermissionLauncher?.launch(Manifest.permission.CAMERA)
    }

    // 日期 BottomSheet
    WeighingDateSheet(
        visible = showDateSheet,
        currentDate = selectedDate,
        maxDate = today,
        onDismiss = { showDateSheet = false },
        onConfirm = { newDate ->
            selectedDate = newDate
            showDateSheet = false
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) {
            HomeBackground(
                modifier = Modifier.matchParentSize(),
                darkTheme = true,
                enableNoise = false
            )
        }

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = screenBackground,
        topBar = {
            CaloShapeTopBar(
                title = stringResource(R.string.record_weight_title),
                onBack = onBack
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = CaloShapeScreenFrame.contentHorizontal, end = CaloShapeScreenFrame.contentHorizontal, bottom = CaloShapeScreenFrame.bottomActionSingle)
            ) {
                Button(
                    onClick = rememberClickWithHaptic(enabled = !ui.saving) {
                        val unitUsed = if (useMetric) {
                            UserProfileStore.WeightUnit.KG
                        } else {
                            UserProfileStore.WeightUnit.LBS
                        }

                        val (kgToSave, lbsToSave) = if (useMetric) {
                            val kgClamped = valueKg.coerceIn(kgMin, kgMax)
                            val kgRounded = roundToOneDecimal(kgClamped)
                            kgRounded to kgToLbs1(kgRounded)
                        } else {
                            val rawLbs = (valueLbsTenths.coerceIn(lbsTenthsMin, lbsTenthsMax)) / 10.0
                            val lbsRounded = roundToOneDecimal(rawLbs)
                            val kg = lbsToKg1(lbsRounded).coerceIn(kgMin, kgMax)
                            kg to lbsRounded
                        }

                        val photoFile = uriStringToCacheFile(context, photoUriString)

                        vm.save(
                            weightKg = kgToSave,
                            weightLbs = lbsToSave,
                            date = selectedDate,
                            photo = photoFile,
                            unitUsedToPersist = unitUsed
                        ) { result ->
                            result.onSuccess {
                                onSaved()
                            }.onFailure { e ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = e.message ?: context.getString(R.string.record_weight_save_failed)
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = valueKg > 0.0 && !ui.saving,
                    shape = RoundedCornerShape(28.dp),
                    colors = if (isDark) {
                        ButtonDefaults.buttonColors(
                            containerColor = buttonContainerColor,
                            contentColor = buttonContentColor,
                            disabledContainerColor = buttonContainerColor.copy(alpha = 0.72f),
                            disabledContentColor = buttonContentColor.copy(alpha = 0.62f)
                        )
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = colors.primaryButtonContainer,
                            contentColor = colors.primaryButtonContent
                        )
                    }
                ) {
                    if (ui.saving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = buttonContentColor
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.common_save),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.1.sp,
                            )
                        )
                    }
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            Spacer(Modifier.height(2.dp))

            DateHeader(
                dateText = selectedDate.format(dateFormatterDisplay),
                onClick = { showDateSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CaloShapeScreenFrame.contentHorizontalWide)
            )

            Spacer(Modifier.height(2.dp))

            PhotoPickerBlock(
                photoUriString = photoUriString,
                cameraAvailable = canUseActivityResult,
                onPickPhoto = { launchTakePhoto() }
            )

            Spacer(Modifier.height(22.dp))

            WeightUnitSegmentedRecord(
                useMetric = useMetric,
                onChange = { useMetric = it },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(10.dp))

            if (useMetric) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberWheelRecord(
                        range = kgMin.toInt()..kgMax.toInt(),
                        value = kgIntSel,
                        onValueChange = { newInt ->
                            val newTenths = (newInt * 10 + kgDecSel)
                                .coerceIn((kgMin * 10).toInt(), (kgMax * 10).toInt())
                            val newKg = newTenths / 10.0
                            valueKg = newKg
                            valueLbsTenths = kgToLbsTenthsRecord(newKg)
                        },
                        rowHeight = rowHeight,
                        centerTextSize = 26.sp,
                        textSize = 22.sp,
                        sideAlpha = 0.35f,
                        modifier = Modifier
                            .width(120.dp)
                            .padding(start = 31.dp)
                    )
                    Text(
                        text = ".",
                        fontSize = 34.sp,
                        color = wheelAccentTextColor,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    NumberWheelRecord(
                        range = 0..9,
                        value = kgDecSel,
                        onValueChange = { newDec ->
                            val newTenths = (kgIntSel * 10 + newDec)
                                .coerceIn((kgMin * 10).toInt(), (kgMax * 10).toInt())
                            val newKg = newTenths / 10.0
                            valueKg = newKg
                            valueLbsTenths = kgToLbsTenthsRecord(newKg)
                        },
                        rowHeight = rowHeight,
                        centerTextSize = 26.sp,
                        textSize = 22.sp,
                        sideAlpha = 0.35f,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(end = 7.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "kg",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = wheelAccentTextColor
                    )
                }
            } else {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberWheelRecord(
                        range = lbsIntMin..lbsIntMax,
                        value = lbsIntSel,
                        onValueChange = { newInt ->
                            val newTenths = (newInt * 10 + lbsDecSel)
                                .coerceIn(lbsTenthsMin, lbsTenthsMax)
                            valueLbsTenths = newTenths
                            val newKg = lbsToKg1(newTenths / 10.0).coerceIn(kgMin, kgMax)
                            valueKg = newKg
                        },
                        rowHeight = rowHeight,
                        centerTextSize = 26.sp,
                        textSize = 22.sp,
                        sideAlpha = 0.35f,
                        modifier = Modifier
                            .width(120.dp)
                            .padding(start = 35.dp)
                    )
                    Text(
                        text = ".",
                        fontSize = 34.sp,
                        color = wheelAccentTextColor,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    NumberWheelRecord(
                        range = 0..9,
                        value = lbsDecSel,
                        onValueChange = { newDec ->
                            val newTenths = (lbsIntSel * 10 + newDec)
                                .coerceIn(lbsTenthsMin, lbsTenthsMax)
                            valueLbsTenths = newTenths
                            val newKg = lbsToKg1(newTenths / 10.0).coerceIn(kgMin, kgMax)
                            valueKg = newKg
                        },
                        rowHeight = rowHeight,
                        centerTextSize = 26.sp,
                        textSize = 22.sp,
                        sideAlpha = 0.35f,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(end = 2.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "lbs",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = wheelAccentTextColor
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(
                        R.string.record_weight_date_note,
                        selectedDate.format(dateFormatterDisplay)
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = helperTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }
    }
}

/* ---------------------------- 日期 Header ---------------------------- */

@Composable
private fun DateHeader(
    dateText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val dateTextColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val iconTint = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .offset(x = 10.dp)
                .clip(RoundedCornerShape(999.dp))
                .caloShapeClickable(onClick = rememberClickWithHaptic(onClick = onClick))
                .defaultMinSize(minHeight = 44.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = dateTextColor
            )

            Spacer(Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit date icon",
                tint = iconTint,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

/* ---------------------------- 單位 Segmented ---------------------------- */

@Composable
private fun WeightUnitSegmentedRecord(
    useMetric: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val containerColor = if (isDark) HomeCardStyles.Surface.raisedAlt() else colors.surfaceMuted
    val containerBorder = if (isDark) HomeCardStyles.Surface.borderColor() else Color.Transparent
    val selectedColor = if (isDark) HomeCardStyles.Camera.selectedTile() else colors.primaryButtonContainer
    val selectedContentColor = if (isDark) HomeCardStyles.Camera.selectedTileContent() else colors.primaryButtonContent
    val idleContentColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary

    Surface(
        shape = RoundedCornerShape(40.dp),
        color = containerColor,
        border = if (isDark) BorderStroke(1.dp, containerBorder) else null,
        modifier = modifier
            .fillMaxWidth(if (isDark) 0.55f else 0.51f)
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            SegItemRecord(
                text = "lbs",
                selected = !useMetric,
                onClick = { onChange(false) },
                selectedColor = selectedColor,
                selectedContentColor = selectedContentColor,
                idleContentColor = idleContentColor,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            Spacer(Modifier.width(6.dp))
            SegItemRecord(
                text = "kg",
                selected = useMetric,
                onClick = { onChange(true) },
                selectedColor = selectedColor,
                selectedContentColor = selectedContentColor,
                idleContentColor = idleContentColor,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun SegItemRecord(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    selectedContentColor: Color,
    idleContentColor: Color,
    modifier: Modifier = Modifier
) {
    val corner = 22.dp
    val fontSize = 18.sp

    Surface(
        onClick = rememberClickWithHaptic(onClick = onClick),
        shape = RoundedCornerShape(corner),
        color = if (selected) selectedColor else Color.Transparent,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 48.dp)
                .padding(horizontal = CaloShapeScreenFrame.contentHorizontalMedium),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) selectedContentColor else idleContentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/* ---------------------------- 數字輪盤（含 label） ---------------------------- */

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NumberWheelRecord(
    range: IntRange,
    value: Int,
    onValueChange: (Int) -> Unit,
    rowHeight: Dp,
    centerTextSize: TextUnit,
    textSize: TextUnit,
    sideAlpha: Float,
    modifier: Modifier = Modifier,
    label: (Int) -> String = { it.toString() },
    showSelectionLines: Boolean = true,
    selectedTextColor: Color? = null,
    unselectedTextColor: Color? = null,
    centerFontWeight: FontWeight = FontWeight.SemiBold,
    sideFontWeight: FontWeight = FontWeight.Normal
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val numberTextColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val centerLineColor = if (isDark) {
        HomeCardStyles.Surface.borderColor().copy(alpha = 0.88f)
    } else {
        colors.border.copy(alpha = 0.72f)
    }
    val resolvedSelectedTextColor = selectedTextColor ?: numberTextColor
    val resolvedUnselectedTextColor = unselectedTextColor ?: numberTextColor.copy(alpha = sideAlpha)
    val visibleCount = 5
    val mid = visibleCount / 2
    val items = remember(range) { range.toList() }
    val selectedIdx = (value - range.first).coerceIn(0, items.lastIndex)

    val state = rememberLazyListState()
    val fling = rememberSnapFlingBehavior(lazyListState = state)

    var initialized by remember(range) { mutableStateOf(false) }
    LaunchedEffect(range, value) {
        if (!initialized) {
            state.scrollToItem(selectedIdx)
            initialized = true
        }
    }

    val centerIndex by remember {
        derivedStateOf {
            val li = state.layoutInfo
            if (li.visibleItemsInfo.isEmpty()) return@derivedStateOf selectedIdx
            val viewportCenter = (li.viewportStartOffset + li.viewportEndOffset) / 2
            li.visibleItemsInfo.minByOrNull { info ->
                abs((info.offset + info.size / 2) - viewportCenter)
            }?.index ?: selectedIdx
        }
    }

    LaunchedEffect(centerIndex, initialized, items) {
        val centeredItem = items.getOrNull(centerIndex)
        if (initialized && centeredItem != null) {
            onValueChange(centeredItem)
        }
    }

    HapticWheelTickEffect(
        tickKey = centerIndex,
        enabled = initialized && state.isScrollInProgress
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight * visibleCount)
    ) {
        LazyColumn(
            state = state,
            flingBehavior = fling,
            contentPadding = PaddingValues(vertical = rowHeight * mid),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, num ->
                val isCenter = index == centerIndex
                val size = if (isCenter) centerTextSize else textSize
                val weight = if (isCenter) centerFontWeight else sideFontWeight
                val textColor = if (isCenter) resolvedSelectedTextColor else resolvedUnselectedTextColor

                Row(
                    modifier = Modifier
                        .height(rowHeight)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label(num),
                        fontSize = size,
                        fontWeight = weight,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (showSelectionLines) {
            val lineColor = centerLineColor
            val half = rowHeight / 2
            val lineThickness = 1.dp
            Box(
                Modifier
                    .align(Alignment.Center)
                    .offset(y = -half)
                    .fillMaxWidth()
                    .height(lineThickness)
                    .background(lineColor)
            )
            Box(
                Modifier
                    .align(Alignment.Center)
                    .offset(y = half - lineThickness)
                    .fillMaxWidth()
                    .height(lineThickness)
                    .background(lineColor)
            )
        }
    }
}

/* ---------------------------- 日期 BottomSheet ---------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeighingDateSheet(
    visible: Boolean,
    currentDate: LocalDate,
    maxDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    if (!visible) return
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val sheetSurfaceColor = if (isDark) HomeCardStyles.Sheet.surface() else colors.surface
    val sheetHandleColor = if (isDark) HomeCardStyles.Sheet.handle() else colors.border
    val sheetTitleColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val sheetSubtitleColor = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary
    val sheetPrimaryButtonContainer = if (isDark) Color.White.copy(alpha = 0.92f) else colors.primaryButtonContainer
    val sheetPrimaryButtonContent = if (isDark) Color(0xFF111114) else colors.primaryButtonContent
    val sheetCancelButtonContainer = if (isDark) HomeCardStyles.Surface.raised() else colors.surfaceMuted
    val sheetCancelButtonContent = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { targetValue -> targetValue != SheetValue.Hidden }
    )

    val today = remember(maxDate) { maxDate }
    val yearRange = remember(today) { (today.year - 5)..today.year }

    var year by rememberSaveable(currentDate) { mutableIntStateOf(currentDate.year) }
    var month by rememberSaveable(currentDate) { mutableIntStateOf(currentDate.monthValue) }
    var day by rememberSaveable(currentDate) { mutableIntStateOf(currentDate.dayOfMonth) }

    val months = listOf(
        stringResource(R.string.weight_date_sheet_month_january),
        stringResource(R.string.weight_date_sheet_month_february),
        stringResource(R.string.weight_date_sheet_month_march),
        stringResource(R.string.weight_date_sheet_month_april),
        stringResource(R.string.weight_date_sheet_month_may),
        stringResource(R.string.weight_date_sheet_month_june),
        stringResource(R.string.weight_date_sheet_month_july),
        stringResource(R.string.weight_date_sheet_month_august),
        stringResource(R.string.weight_date_sheet_month_september),
        stringResource(R.string.weight_date_sheet_month_october),
        stringResource(R.string.weight_date_sheet_month_november),
        stringResource(R.string.weight_date_sheet_month_december)
    )

    val titleText = stringResource(R.string.weight_date_sheet_title)
    val subtitleText = stringResource(R.string.weight_date_sheet_subtitle)
    val saveText = stringResource(R.string.common_save)
    val cancelText = stringResource(R.string.common_cancel)

    val monthRange: IntRange = remember(year, today) {
        if (year >= today.year) 1..today.monthValue else 1..12
    }

    val dayRange: IntRange = remember(year, month, today) {
        val maxDayOfMonth = Month.of(month).length(Year.of(year).isLeap)
        val maxDay = if (year == today.year && month == today.monthValue) {
            min(maxDayOfMonth, today.dayOfMonth)
        } else {
            maxDayOfMonth
        }
        1..maxDay
    }

    LaunchedEffect(monthRange) {
        if (month !in monthRange) {
            month = month.coerceIn(monthRange.first, monthRange.last)
        }
    }

    LaunchedEffect(dayRange) {
        if (day !in dayRange) {
            day = day.coerceIn(dayRange.first, dayRange.last)
        }
    }

    fun clampDay(y: Int, m: Int, d: Int): Int {
        val maxDayOfMonth = Month.of(m).length(Year.of(y).isLeap)
        val maxDay = if (y == today.year && m == today.monthValue) {
            min(maxDayOfMonth, today.dayOfMonth)
        } else {
            maxDayOfMonth
        }
        return d.coerceIn(1, maxDay)
    }

    val confirmClick = rememberClickWithHaptic {
        val safeYear = year.coerceIn(yearRange)
        val safeMonth = if (safeYear == today.year) {
            month.coerceIn(1, today.monthValue)
        } else {
            month.coerceIn(1, 12)
        }
        val safeDay = clampDay(safeYear, safeMonth, day)
        val raw = LocalDate.of(safeYear, safeMonth, safeDay)
        val finalDate = if (raw.isAfter(today)) today else raw

        onConfirm(finalDate)
    }
    val cancelClick = rememberClickWithHaptic(onClick = onDismiss)

    val sheetHeight = 546.dp
    val rowItemHeight = 44.dp
    val wheelAreaHeight = 260.dp

    ModalBottomSheet(
        onDismissRequest = { /* 只有 Cancel 才關 */ },
        sheetState = sheetState,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = sheetSurfaceColor,
        tonalElevation = 0.dp,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetHeight)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 170.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .width(42.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(sheetHandleColor)
                )
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = sheetTitleColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = sheetSubtitleColor,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(wheelAreaHeight),
                    contentAlignment = Alignment.Center
                ) {
                    DateSelectionBandBehind(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .height(rowItemHeight)
                    )
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NumberWheelRecord(
                            range = dayRange,
                            value = day,
                            onValueChange = { day = it },
                            rowHeight = rowItemHeight,
                            centerTextSize = 22.sp,
                            textSize = 21.sp,
                            sideAlpha = 1f,
                            modifier = Modifier.width(64.dp),
                            showSelectionLines = false,
                            selectedTextColor = sheetTitleColor,
                            unselectedTextColor = if (isDark) HomeCardStyles.Text.muted() else colors.textMuted,
                            centerFontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(10.dp))
                        NumberWheelRecord(
                            range = monthRange,
                            value = month,
                            onValueChange = { month = it },
                            rowHeight = rowItemHeight,
                            centerTextSize = 20.sp,
                            textSize = 19.sp,
                            sideAlpha = 1f,
                            modifier = Modifier.width(142.dp),
                            label = { idx -> months[idx - 1] },
                            showSelectionLines = false,
                            selectedTextColor = sheetTitleColor,
                            unselectedTextColor = if (isDark) HomeCardStyles.Text.muted() else colors.textMuted,
                            centerFontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(10.dp))
                        NumberWheelRecord(
                            range = yearRange,
                            value = year,
                            onValueChange = { year = it },
                            rowHeight = rowItemHeight,
                            centerTextSize = 22.sp,
                            textSize = 21.sp,
                            sideAlpha = 1f,
                            modifier = Modifier.width(86.dp),
                            showSelectionLines = false,
                            selectedTextColor = sheetTitleColor,
                            unselectedTextColor = if (isDark) HomeCardStyles.Text.muted() else colors.textMuted,
                            centerFontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = confirmClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = sheetPrimaryButtonContainer,
                        contentColor = sheetPrimaryButtonContent
                    )
                ) {
                    Text(
                        text = saveText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = cancelClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = sheetCancelButtonContainer,
                        contentColor = sheetCancelButtonContent
                    )
                ) {
                    Text(
                        text = cancelText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun DateSelectionBandBehind(
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val bandHeight = 44.dp
    val bandRadius = 10.dp
    val bandColor = if (isDark) HomeCardStyles.Surface.raisedAlt() else colors.surfaceMuted
    val lineColor = if (isDark) HomeCardStyles.Surface.borderColor().copy(alpha = 0.88f) else colors.border

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.88f)
                .height(bandHeight)
                .clip(RoundedCornerShape(bandRadius))
                .background(bandColor)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -bandHeight / 2)
                .fillMaxWidth(0.92f)
                .height(1.dp)
                .background(lineColor)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = bandHeight / 2)
                .fillMaxWidth(0.92f)
                .height(1.dp)
                .background(lineColor)
        )
    }
}

/* ---------------------------- 照片區塊（只用相機） ---------------------------- */

@Composable
private fun PhotoPickerBlock(
    photoUriString: String?,
    cameraAvailable: Boolean,
    onPickPhoto: () -> Unit
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val photoContainerColor = if (isDark) HomeCardStyles.Surface.raisedAlt() else colors.surfaceMuted
    val photoBorderColor = if (isDark) HomeCardStyles.Surface.borderColor() else Color.Transparent
    val photoIconTint = if (isDark) HomeCardStyles.Text.muted() else colors.textMuted
    val photoTextColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val photoDisabledTextColor = if (isDark) HomeCardStyles.Text.muted() else colors.textMuted
    val uri = photoUriString?.takeIf { it.isNotBlank() }?.toUri()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(156.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(photoContainerColor)
                .then(if (isDark) Modifier.border(1.dp, photoBorderColor, RoundedCornerShape(26.dp)) else Modifier)
                .caloShapeClickable(onClick = onPickPhoto),
            contentAlignment = Alignment.Center
        ) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Weight photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.weight_image_2),
                    contentDescription = "Add weight photo",
                    tint = photoIconTint,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = when {
                !cameraAvailable -> stringResource(R.string.record_weight_camera_not_available)
                uri == null -> stringResource(R.string.record_weight_take_photo)
                else -> stringResource(R.string.record_weight_retake_photo)
            },
            color = if (cameraAvailable) photoTextColor else photoDisabledTextColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/* ---------------------------- Uri / Bitmap → 暫存 File ---------------------------- */

private fun uriStringToCacheFile(context: Context, uriString: String?): File? {
    if (uriString.isNullOrBlank()) return null
    return runCatching {
        val uri = uriString.toUri()

        // ✅ file:// 直接轉 File（你拍照是 Uri.fromFile(file) 產生的）
        if (uri.scheme == "file") {
            val path = uri.path ?: return null
            return File(path).takeIf { it.exists() }
        }

        // content:// 才用 contentResolver 複製成 temp file
        if (uri.scheme == "content") {
            val input = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("weight_photo_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { out ->
                input.use { it.copyTo(out) }
            }
            return tempFile
        }

        null
    }.getOrNull()
}

private fun bitmapToCacheFile(context: Context, bitmap: Bitmap): File {
    val tempFile = File.createTempFile("weight_camera_", ".jpg", context.cacheDir)
    tempFile.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return tempFile
}

/* ---------------------------- 換算工具 ---------------------------- */

private fun kgToLbsTenthsRecord(kg: Double): Int =
    (kgToLbs1(kg) * 10.0).toInt()

private fun roundToOneDecimal(value: Double): Double =
    BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).toDouble()

/* ---------------------------- Context helpers ---------------------------- */

private tailrec fun Context.findActivityResultRegistryOwner(): ActivityResultRegistryOwner? {
    return when (this) {
        is ActivityResultRegistryOwner -> this
        is ContextWrapper -> baseContext.findActivityResultRegistryOwner()
        else -> null
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
