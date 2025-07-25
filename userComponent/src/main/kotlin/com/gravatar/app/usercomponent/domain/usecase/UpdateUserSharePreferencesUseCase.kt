package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.UserSharePreferencesStorage
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences

internal class UpdateUserSharePreferencesUseCase(
    private val userSharePreferencesStorage: UserSharePreferencesStorage
) : UpdateUserSharePreferences {

    override suspend fun invoke(userSharePreferences: UserSharePreferences) {
        userSharePreferencesStorage.saveUserSharePreferences(userSharePreferences)
    }
}

interface UpdateUserSharePreferences {
    suspend operator fun invoke(userSharePreferences: UserSharePreferences)
}
