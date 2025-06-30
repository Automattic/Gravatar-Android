package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.restapi.models.Avatar

interface UserRepository {

    suspend fun getAvatars(): Result<List<Avatar>>
}
