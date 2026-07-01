package com.caloshape.app.ui.home.ui.settings.details.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caloshape.app.data.profile.repo.AutoGoalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed class AutoGenEvent {
    data object Success : AutoGenEvent()
    data class HttpError(val code: Int) : AutoGenEvent()
    data object NetworkError : AutoGenEvent()
    data object Error : AutoGenEvent()
}

@HiltViewModel
class AutoGenerateGoalsCalcViewModel @Inject constructor(
    private val repo: AutoGoalsRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<AutoGenEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AutoGenEvent> = _events

    private val _committing = MutableStateFlow(false)
    val committing: StateFlow<Boolean> = _committing

    private var started = false

    fun startCommitOnce() {
        if (started) return
        started = true
        commit()
    }

    fun commit() {
        if (_committing.value) return

        viewModelScope.launch {
            _committing.value = true
            runCatching { repo.commitFromLocal() }
                .onSuccess {
                    _events.tryEmit(AutoGenEvent.Success)
                }
                .onFailure { e ->
                    val event = when (e) {
                        is HttpException -> AutoGenEvent.HttpError(e.code())
                        is IOException -> AutoGenEvent.NetworkError
                        else -> AutoGenEvent.Error
                    }
                    _events.tryEmit(event)
                }
            _committing.value = false
        }
    }
}
