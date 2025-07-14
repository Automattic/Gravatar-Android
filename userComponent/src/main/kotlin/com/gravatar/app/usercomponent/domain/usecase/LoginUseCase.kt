package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.UserSessionPersistence
import com.gravatar.app.usercomponent.domain.model.LoginRequest
import com.gravatar.app.usercomponent.domain.model.LoginResult
import com.gravatar.app.usercomponent.domain.model.UserSession
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository

interface Login {
    suspend operator fun invoke(loginRequest: LoginRequest): LoginResult
}

internal class LoginUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val userSessionPersistence: UserSessionPersistence,
) : Login {

    override suspend fun invoke(loginRequest: LoginRequest): LoginResult {
        return getToken(loginRequest)
            .fold(
                onSuccess = { token ->
                    profileRepository.refreshUserProfile()
                        .fold(
                            onSuccess = {
                                userSessionPersistence.set(UserSession.LOGGED_IN)
                                LoginResult.Success
                            },
                            onFailure = {
                                LoginResult.ProfileLoadFailure
                            }
                        )
                },
                onFailure = {
                    LoginResult.AuthenticationFailure
                }
            )
    }

    private suspend fun getToken(loginRequest: LoginRequest): Result<String> {
        return when (loginRequest) {
            is LoginRequest.FullLogin -> authRepository.fetchToken(loginRequest.request)
            LoginRequest.LoadProfile -> {
                authRepository.getToken()
                    ?.let {
                        Result.success(it)
                    }
                    ?: Result.failure(IllegalStateException("No token found in storage"))
            }
        }
    }
}
