package com.gravatar.app.usercomponent.data

class FakeAuthTokenStorage : AuthTokenStorage {

    private var authToken: String? = null

    override suspend fun getToken(): String? {
        return authToken
    }

    override suspend fun saveToken(token: String) {
        authToken = token
    }

    override suspend fun clearToken() {
        authToken = null
    }
}
