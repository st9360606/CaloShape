package com.caloshape.app.ui.home.ui.settings.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.caloshape.app.R
import com.caloshape.app.ui.common.design.CaloShapeLoadingScreen
import com.caloshape.app.ui.home.ui.settings.details.model.AutoGenEvent
import com.caloshape.app.ui.home.ui.settings.details.model.AutoGenerateGoalsCalcViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AutoGenerateGoalsCalcScreen(
    onDone: (String) -> Unit,
    onFailToast: (String) -> Unit,
    vm: AutoGenerateGoalsCalcViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 進頁就 commit + collect（合併成一個 effect，少掉重複）
    LaunchedEffect(vm, context) {
        vm.startCommitOnce()
        vm.events.collectLatest { ev ->
            when (ev) {
                AutoGenEvent.Success -> onDone(
                    context.getString(R.string.auto_generate_goals_success)
                )
                is AutoGenEvent.HttpError -> onFailToast(
                    context.getString(R.string.auto_generate_goals_failed_http_format, ev.code)
                )
                AutoGenEvent.NetworkError -> onFailToast(
                    context.getString(R.string.auto_generate_goals_network_error)
                )
                AutoGenEvent.Error -> onFailToast(
                    context.getString(R.string.auto_generate_goals_failed)
                )
            }
        }
    }

    CaloShapeLoadingScreen()
}
