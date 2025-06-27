package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.app.usercomponent.domain.model.LoginRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(loginRequest: LoginRequest): Result<Unit>

    fun isUserLoggedIn(): Flow<Boolean>

    suspend fun logout()
}
