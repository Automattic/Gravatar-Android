package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.PrivateContactInfoStorage
import com.gravatar.app.usercomponent.domain.model.PrivateContactInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

internal class GetPrivateContactInfoUseCase(
    private val privateContactInfoStorage: PrivateContactInfoStorage
) : GetPrivateContactInfo {

    override fun invoke(): Flow<PrivateContactInfo> {
        return privateContactInfoStorage.getPrivateContactInfo()
            .distinctUntilChanged()
    }
}

interface GetPrivateContactInfo {
    operator fun invoke(): Flow<PrivateContactInfo>
}
