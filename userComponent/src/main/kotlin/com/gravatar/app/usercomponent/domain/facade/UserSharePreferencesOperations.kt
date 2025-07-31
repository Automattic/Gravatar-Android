package com.gravatar.app.usercomponent.domain.facade

import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import com.gravatar.app.usercomponent.domain.usecase.GetUserSharePreferences
import com.gravatar.app.usercomponent.domain.usecase.UpdateUserSharePreferences
import kotlinx.coroutines.flow.Flow

/**
 * Facade implementation that combines user preferences-related use cases.
 */
internal class UserSharePreferencesOperations(
    private val getUserSharePreferences: GetUserSharePreferences,
    private val updateUserSharePreferences: UpdateUserSharePreferences
) : UserSharePreferencesFacade {
    /**
     * Get user share preferences as a Flow.
     */
    override fun getPreferences(): Flow<UserSharePreferences> = getUserSharePreferences()

    /**
     * Update user share preferences.
     */
    override suspend fun updatePreferences(preferences: UserSharePreferences) =
        updateUserSharePreferences(preferences)
}

/**
 * Facade that combines user preferences-related use cases.
 */
interface UserSharePreferencesFacade {
    /**
     * Get user share preferences as a Flow.
     */
    fun getPreferences(): Flow<UserSharePreferences>

    /**
     * Update user share preferences.
     */
    suspend fun updatePreferences(preferences: UserSharePreferences)
}
