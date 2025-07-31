package com.gravatar.app.usercomponent.domain.facade

import com.gravatar.app.usercomponent.domain.model.PrivateContactInfo
import com.gravatar.app.usercomponent.domain.usecase.GetPrivateContactInfo
import com.gravatar.app.usercomponent.domain.usecase.UpdatePrivateContactInfo
import kotlinx.coroutines.flow.Flow

/**
 * Facade implementation that combines contact information-related use cases.
 */
internal class PrivateContactInfoOperations(
    private val getPrivateContactInfo: GetPrivateContactInfo,
    private val updatePrivateContactInfo: UpdatePrivateContactInfo
) : PrivateContactInfoFacade {
    /**
     * Get private contact information as a Flow.
     */
    override fun getContactInfo(): Flow<PrivateContactInfo> = getPrivateContactInfo()

    /**
     * Update private contact information.
     */
    override suspend fun updateContactInfo(info: PrivateContactInfo) =
        updatePrivateContactInfo(info)
}

/**
 * Facade that combines contact information-related use cases.
 */
interface PrivateContactInfoFacade {
    /**
     * Get private contact information as a Flow.
     */
    fun getContactInfo(): Flow<PrivateContactInfo>

    /**
     * Update private contact information.
     */
    suspend fun updateContactInfo(info: PrivateContactInfo)
}
