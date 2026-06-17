package com.calai.bitecal.ui.home.ui.settings.details.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calai.bitecal.data.profile.repo.AutoGoalsRepository
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
    data class Success(val message: String) : AutoGenEvent()
    data class Error(val message: String) : AutoGenEvent()
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
                    _events.tryEmit(AutoGenEvent.Success("Generated successfully!"))
                }
                .onFailure { e ->
                    val msg = when (e) {
                        is HttpException -> "Generate failed (${e.code()})"
                        is IOException -> "Network error, please try again."
                        else -> e.message?.takeIf { it.isNotBlank() } ?: "Generate failed"
                    }
                    _events.tryEmit(AutoGenEvent.Error(msg))
                }
            _committing.value = false
        }
    }
}
