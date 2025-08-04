package com.gravatar.app.restapi

import com.gravatar.app.model.DeleteAccountStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interface defining the Gravatar API endpoints.
 */
internal interface GravatarApi {
    /**
     * Disables the user's Gravatar account.
     * Makes a POST request to https://api.gravatar.com/v3/me/status
     *
     * @return [Result] indicating success or failure of the operation
     */
    @POST("me/status")
    suspend fun disableAccount(
        @Header("Authorization") authorization: String,
        @Body body: DeleteAccountStatus = DeleteAccountStatus()
    ): Response<Unit>
}
