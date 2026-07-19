package com.caloshape.app.data.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RepoInvalidationBus @Inject constructor() {

    private val _profile = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val profile: SharedFlow<Unit> = _profile

    private val _weight = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val weight: SharedFlow<Unit> = _weight

    fun invalidateProfile() {
        _profile.tryEmit(Unit)
    }

    fun invalidateWeight() {
        _weight.tryEmit(Unit)
    }
}
