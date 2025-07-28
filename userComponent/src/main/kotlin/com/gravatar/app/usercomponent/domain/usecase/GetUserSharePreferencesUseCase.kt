package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.UserSharePreferencesStorage
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

internal class GetUserSharePreferencesUseCase(
    private val userSharePreferencesStorage: UserSharePreferencesStorage
) : GetUserSharePreferences {

    override fun invoke(): Flow<UserSharePreferences> {
        return userSharePreferencesStorage.getUserSharePreferences()
            .distinctUntilChanged()
    }
}

interface GetUserSharePreferences {
    operator fun invoke(): Flow<UserSharePreferences>
}
