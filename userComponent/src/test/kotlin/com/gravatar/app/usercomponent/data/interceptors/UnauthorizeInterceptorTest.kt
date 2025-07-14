package com.gravatar.app.usercomponent.data.interceptors

import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.usecase.Logout
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UnauthorizeInterceptorTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var unauthorizeInterceptor: UnauthorizeInterceptor
    private val logout = mockk<Lazy<Logout>>()
    private val applicationScope = CoroutineScope(testDispatcher)
    private val testDispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setup() {
        unauthorizeInterceptor = UnauthorizeInterceptor(
            applicationScope = applicationScope,
            dispatcherProvider = testDispatcherProvider,
            logout = logout
        )
    }

    @Test
    fun `should invoke logout when response code is 401`() = runTest {
        // Given
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder().url("https://example.com").build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(HttpResponseCode.UNAUTHORIZED)
            .message("Unauthorized")
            .build()

        // When
        coEvery { logout.value.invoke() } returns Unit
        every { chain.request() } returns request
        every { chain.proceed(request) } returns response

        unauthorizeInterceptor.intercept(chain)

        // Then
        advanceUntilIdle()
        coVerify(exactly = 1) { logout.value.invoke() }
    }

    @Test
    fun `should not invoke logout when response code is not 401`() = runTest {
        // Given
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder().url("https://example.com").build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(404)
            .message("Not Found")
            .build()

        // When
        every { chain.request() } returns request
        every { chain.proceed(request) } returns response

        unauthorizeInterceptor.intercept(chain)

        // Then
        advanceUntilIdle()
        coVerify(exactly = 0) { logout.value.invoke() }
    }

    @Test
    fun `should not invoke logout when response is successful`() = runTest {
        // Given
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder().url("https://example.com").build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        // When
        every { chain.request() } returns request
        every { chain.proceed(request) } returns response

        unauthorizeInterceptor.intercept(chain)

        // Then
        advanceUntilIdle()
        coVerify(exactly = 0) { logout.value.invoke() }
    }
}
