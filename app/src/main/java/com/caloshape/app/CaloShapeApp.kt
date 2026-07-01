package com.caloshape.app

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.caloshape.app.i18n.LanguageManager
import com.caloshape.app.i18n.LanguageStore
import com.caloshape.app.i18n.ProvideComposeLocale
import com.caloshape.app.ui.appearance.AppearanceMode
import com.caloshape.app.ui.appearance.AppearanceStore
import com.caloshape.app.ui.nav.CaloShapeNavHost
import com.caloshape.app.ui.theme.CaloShapeTheme
import com.caloshape.app.widget.CaloShapeHomeWidgetUpdater
import com.caloshape.app.widget.CaloShapeWidgetNavigationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun CaloShapeApp(
    hostActivity: ComponentActivity,
    widgetNavigationRequest: CaloShapeWidgetNavigationRequest? = null
) {
    val context = LocalContext.current
    val store = remember(context) { LanguageStore(context) }
    val appearanceStore = remember(context) { AppearanceStore(context.applicationContext) }
    val appearanceMode by appearanceStore.modeFlow.collectAsState(initial = AppearanceMode.LIGHT)
    val appearanceScope = rememberCoroutineScope()

    val savedTagOrNull: String? by store.langFlow.collectAsState(initial = null)

    if (savedTagOrNull == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Black)
        }
        return
    }

    val initialTag = LanguageManager.normalizeTag(when {
        !savedTagOrNull.isNullOrBlank() -> savedTagOrNull!!
        AppCompatDelegate.getApplicationLocales().toLanguageTags().isNotBlank() ->
            AppCompatDelegate.getApplicationLocales().toLanguageTags()
        else -> Locale.getDefault().toLanguageTag()
    })

    var composeLocale by remember(initialTag) { mutableStateOf(initialTag) }

    LaunchedEffect(composeLocale) {
        if (composeLocale.isNotBlank()) {
            store.save(composeLocale)
            withContext(Dispatchers.IO) {
                CaloShapeHomeWidgetUpdater.updateAll(context.applicationContext)
            }
        }
    }

    ProvideComposeLocale(composeLocale) {
        CaloShapeTheme(darkTheme = appearanceMode == AppearanceMode.DARK) {
            CaloShapeNavHost(
                hostActivity = hostActivity,
                widgetNavigationRequest = widgetNavigationRequest,
                appearanceMode = appearanceMode,
                onSetAppearanceMode = { mode ->
                    appearanceScope.launch {
                        appearanceStore.setMode(mode)
                    }
                },
                onSetLocale = { tag -> composeLocale = tag }
            )
        }
    }
}
