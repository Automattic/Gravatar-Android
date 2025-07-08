package com.gravatar.app.usercomponent.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class AvatarCacheBusterStorage {

    private val _state: MutableStateFlow<String?> = MutableStateFlow(null)
    val state: Flow<String?> = _state

    suspend fun set(value: String) {
        _state.emit(value)
    }
}
