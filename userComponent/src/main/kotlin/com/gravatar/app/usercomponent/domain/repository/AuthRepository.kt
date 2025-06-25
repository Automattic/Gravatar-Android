package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.app.usercomponent.domain.model.LoginRequest

interface AuthRepository {

    suspend fun login(loginRequest: LoginRequest): Result<Unit>
}
