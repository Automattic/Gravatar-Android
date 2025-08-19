package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.PrivacySettingsStorage
import com.gravatar.app.usercomponent.domain.model.PrivacySettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

internal class GetPrivacySettingsUseCase(
    private val privacySettingsStorage: PrivacySettingsStorage
) : GetPrivacySettings {

    override fun invoke(): Flow<PrivacySettings> {
        return privacySettingsStorage.getPrivacySettings()
            .distinctUntilChanged()
    }
}

interface GetPrivacySettings {
    operator fun invoke(): Flow<PrivacySettings>
}
