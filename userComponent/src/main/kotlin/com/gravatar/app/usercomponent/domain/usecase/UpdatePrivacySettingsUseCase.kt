package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.PrivacySettingsStorage
import com.gravatar.app.usercomponent.domain.model.PrivacySettings

internal class UpdatePrivacySettingsUseCase(
    private val privacySettingsStorage: PrivacySettingsStorage
) : UpdatePrivacySettings {

    override suspend fun invoke(privacySettings: PrivacySettings) {
        privacySettingsStorage.savePrivacySettings(privacySettings)
    }
}

interface UpdatePrivacySettings {
    suspend operator fun invoke(privacySettings: PrivacySettings)
}
