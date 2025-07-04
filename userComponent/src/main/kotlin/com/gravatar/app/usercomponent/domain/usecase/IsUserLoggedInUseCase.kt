package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

internal class IsUserLoggedInUseCase(
    private val authRepository: AuthRepository,
) : IsUserLoggedIn {
    override fun invoke(): Flow<Boolean> {
        return authRepository.isUserLoggedIn()
    }
}

interface IsUserLoggedIn {
    operator fun invoke(): Flow<Boolean>
}
