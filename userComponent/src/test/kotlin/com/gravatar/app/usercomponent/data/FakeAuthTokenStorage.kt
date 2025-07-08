package com.gravatar.app.usercomponent.data

class FakeAuthTokenStorage : AuthTokenStorage {

    private var authToken: String? = null

    override suspend fun get(): String? {
        return authToken
    }

    override suspend fun save(token: String) {
        authToken = token
    }

    override suspend fun clear() {
        authToken = null
    }
}
