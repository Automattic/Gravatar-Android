package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.PrivateContactInfoStorage
import com.gravatar.app.usercomponent.domain.model.PrivateContactInfo

internal class UpdatePrivateContactInfoUseCase(
    private val privateContactInfoStorage: PrivateContactInfoStorage
) : UpdatePrivateContactInfo {

    override suspend fun invoke(privateContactInfo: PrivateContactInfo) {
        privateContactInfoStorage.savePrivateContactInfo(privateContactInfo)
    }
}

interface UpdatePrivateContactInfo {
    suspend operator fun invoke(privateContactInfo: PrivateContactInfo)
}
