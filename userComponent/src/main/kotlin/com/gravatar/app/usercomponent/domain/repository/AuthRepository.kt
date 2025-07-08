package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.app.usercomponent.domain.model.LoginRequest

internal interface AuthRepository {

    suspend fun login(loginRequest: LoginRequest): Result<Unit>

    suspend fun isUserLoggedIn(): Boolean

    suspend fun logout()
}
