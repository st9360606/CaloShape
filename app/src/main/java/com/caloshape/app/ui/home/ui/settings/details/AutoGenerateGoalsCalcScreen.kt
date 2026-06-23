package com.caloshape.app.ui.home.ui.settings.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
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
    // 進頁就 commit + collect（合併成一個 effect，少掉重複）
    LaunchedEffect(vm) {
        vm.startCommitOnce()
        vm.events.collectLatest { ev ->
            when (ev) {
                is AutoGenEvent.Success -> onDone(ev.message)
                is AutoGenEvent.Error -> onFailToast(ev.message)
            }
        }
    }

    CaloShapeLoadingScreen()
}
