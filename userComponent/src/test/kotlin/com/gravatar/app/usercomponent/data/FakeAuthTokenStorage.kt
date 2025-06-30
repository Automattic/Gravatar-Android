package com.gravatar.app.usercomponent.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthTokenStorage : AuthTokenStorage {

    private var authToken: String? = null

    override fun get(): Flow<String?> {
        return flow { emit(authToken) }
    }

    override suspend fun save(token: String) {
        authToken = token
    }

    override suspend fun clear() {
        authToken = null
    }
}
