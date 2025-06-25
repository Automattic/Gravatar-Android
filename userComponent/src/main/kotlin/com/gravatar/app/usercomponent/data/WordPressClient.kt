package com.gravatar.app.usercomponent.data

import com.gravatar.app.foundations.DispatcherProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal class WordPressClient(
    private val httpClient: HttpClient,
    private val dispatcherProvider: DispatcherProvider,
) {

    companion object {
        private const val BASE_URL = "https://public-api.wordpress.com"
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun login(
        code: String,
        clientSecret: String,
        redirectUri: String,
        clientId: String
    ): Result<String> = withContext(dispatcherProvider.io) {
        try {
            val response = httpClient.submitForm(
                url = "$BASE_URL/oauth2/token",
                formParameters = parameters {
                    append("code", code)
                    append("client_secret", clientSecret)
                    append("redirect_uri", redirectUri)
                    append("client_id", clientId)
                    append("grant_type", "authorization_code")
                }
            ) {
                contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                val wpToken: WordPressOAuthToken = response.body()
                Result.success(wpToken.accessToken)
            } else {
                Result.failure(
                    Exception("Failed to login: ${response.status.value} ${response.status.description}")
                )
            }
        } catch (ex: Exception) {
            return@withContext Result.failure(
                Exception("Failed to login: ${ex.message}", ex)
            )
        }
    }
}

@Serializable
private data class WordPressOAuthToken(
    @SerialName("access_token") val accessToken: String,
)
