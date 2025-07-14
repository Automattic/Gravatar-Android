package com.gravatar.app.loginUi.presentation.login

import app.cash.turbine.test
import com.gravatar.app.loginUi.presentation.oauth.OAuthConfig
import com.gravatar.app.loginUi.presentation.oauth.OAuthResult
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.model.LoginRequest
import com.gravatar.app.usercomponent.domain.model.LoginResult
import com.gravatar.app.usercomponent.domain.model.OAuthRequest
import com.gravatar.app.usercomponent.domain.usecase.Login
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val login: Login = mockk()
    private val oAuthConfig = OAuthConfig(
        clientId = "test-client-id",
        redirectUri = "test-redirect-uri",
        clientSecret = "test-client-secret"
    )
    private lateinit var viewModel: LoginViewModel

    @Test
    fun `init should set initial state`() = runTest {
        // When
        initViewModel()

        // Then
        viewModel.uiState.test {
            assertEquals(LoginUiState(), awaitItem())
        }
    }

    @Test
    fun `onEvent OAuthResultReceived with Token should call login`() = runTest {
        // Given
        val code = "test-code"
        val oAuthResult = OAuthResult.Token(code)
        coEvery { login(any()) } returns LoginResult.Success
        initViewModel()

        // When
        viewModel.onEvent(LoginEvent.OAuthResultReceived(oAuthResult))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            assertEquals(LoginUiState(isLoading = false), awaitItem())
        }

        val expectedLoginRequest = LoginRequest.FullLogin(
            request = OAuthRequest(
                code = code,
                clientSecret = oAuthConfig.clientSecret,
                redirectUri = oAuthConfig.redirectUri,
                clientId = oAuthConfig.clientId
            )
        )
        coVerify { login(expectedLoginRequest) }
    }

    @Test
    fun `onEvent OAuthResultReceived with Error should do nothing`() = runTest {
        // Given
        initViewModel()

        // When
        viewModel.onEvent(LoginEvent.OAuthResultReceived(OAuthResult.Error))
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { login(any()) }
    }

    @Test
    fun `onEvent OAuthResultReceived with Dismissed should do nothing`() = runTest {
        // Given
        initViewModel()

        // When
        viewModel.onEvent(LoginEvent.OAuthResultReceived(OAuthResult.Dismissed))
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { login(any()) }
    }

    @Test
    fun `onEvent OnLoadProfileClicked should call login with LoadProfile`() = runTest {
        // Given
        coEvery { login(any()) } returns LoginResult.Success
        initViewModel()

        // When
        viewModel.onEvent(LoginEvent.OnLoadProfileClicked)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            assertEquals(LoginUiState(isLoading = false), awaitItem())
        }

        coVerify { login(LoginRequest.LoadProfile) }
    }

    @Test
    fun `loginUser should handle AuthenticationFailure`() = runTest {
        // Given
        coEvery { login(any()) } returns LoginResult.AuthenticationFailure
        initViewModel()

        // When
        viewModel.onEvent(LoginEvent.OnLoadProfileClicked)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            assertEquals(LoginUiState(isLoading = false), awaitItem())
        }

        viewModel.actions.test {
            assertEquals(LoginAction.ShowLoginError, awaitItem())
        }
    }

    @Test
    fun `loginUser should handle ProfileLoadFailure`() = runTest {
        // Given
        coEvery { login(any()) } returns LoginResult.ProfileLoadFailure
        initViewModel()

        // When
        viewModel.onEvent(LoginEvent.OnLoadProfileClicked)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = LoginUiState(
                isLoading = false,
                error = LoginError.ProfileLoadFailure(
                    reason = LoginError.ProfileLoadFailure.Reason.GENERIC_ERROR
                )
            )
            assertEquals(expectedState, awaitItem())
        }
    }

    private fun initViewModel() {
        viewModel = LoginViewModel(
            login = login,
            oAuthConfig = oAuthConfig,
        )
    }
}
