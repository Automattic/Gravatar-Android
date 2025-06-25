package com.gravatar.app.loginUi.presentation.oauth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import org.koin.android.ext.android.inject

internal class OAuthActivity : ComponentActivity() {

    private val oAuthConfig: OAuthConfig by inject()

    private var oAuthStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }

        oAuthStarted = savedInstanceState?.getBoolean(OAUTH_STARTED_KEY) ?: false

        addOnNewIntentListener { newIntent ->
            val token = newIntent.data?.getQueryParameter("code")

            if (token != null) {
                val resultIntent = Intent().apply {
                    putExtra(ACTIVITY_RESULT, RESULT_TOKEN_RETRIEVED)
                    putExtra(TOKEN_KEY, token)
                }

                setResult(RESULT_OK, resultIntent)
            } else {
                setResult(
                    RESULT_OK,
                    Intent().apply {
                        putExtra(ACTIVITY_RESULT, RESULT_TOKEN_ERROR)
                    },
                )
            }
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(OAUTH_STARTED_KEY, oAuthStarted)
    }

    override fun onResume() {
        super.onResume()

        if (!oAuthStarted) {
            launchCustomTab(this)
            oAuthStarted = true
        } else {
            setResult(
                RESULT_OK,
                Intent().apply {
                    putExtra(ACTIVITY_RESULT, RESULT_CANCELED)
                },
            )
            finish()
        }
    }

    private fun launchCustomTab(
        context: Context,
    ) {
        val customTabIntent: CustomTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabIntent.launchUrl(
            context,
            Uri.Builder().scheme("https").authority("public-api.wordpress.com")
                .appendPath("oauth2")
                .appendPath("authorize").appendQueryParameter("client_id", oAuthConfig.clientId)
                .appendQueryParameter("redirect_uri", oAuthConfig.redirectUri)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("scope[1]", "auth")
                .appendQueryParameter("scope[2]", "gravatar-global").build().toString()
                .toUri(),
        )
    }

    internal companion object {
        private const val OAUTH_STARTED_KEY = "oauth_started"
        internal const val TOKEN_KEY = "auth_token"

        internal const val ACTIVITY_RESULT: String = "oAuthActivityResult"
        internal const val RESULT_CANCELED: Int = 1000
        internal const val RESULT_TOKEN_RETRIEVED: Int = 1001
        internal const val RESULT_TOKEN_ERROR: Int = 1002

        internal fun createIntent(
            context: Context,
        ): Intent {
            return Intent(context, OAuthActivity::class.java)
        }
    }
}

internal class OAuthResultContract :
    ActivityResultContract<Unit, OAuthResult>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return OAuthActivity.createIntent(context = context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): OAuthResult {
        return when (intent?.getIntExtra(OAuthActivity.ACTIVITY_RESULT, -1)) {
            OAuthActivity.RESULT_TOKEN_RETRIEVED -> OAuthResult.Token(
                intent.getStringExtra(OAuthActivity.TOKEN_KEY)!!,
            )

            OAuthActivity.RESULT_TOKEN_ERROR -> OAuthResult.Error
            else -> OAuthResult.Dismissed
        }
    }
}

internal sealed class OAuthResult {
    data class Token(val token: String) : OAuthResult()

    data object Dismissed : OAuthResult()

    data object Error : OAuthResult()
}
