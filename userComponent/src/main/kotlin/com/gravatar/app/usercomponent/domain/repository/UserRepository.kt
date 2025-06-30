package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile

interface UserRepository {

    suspend fun getAvatars(): Result<List<Avatar>>

    suspend fun getProfile(): Result<Profile>
}
