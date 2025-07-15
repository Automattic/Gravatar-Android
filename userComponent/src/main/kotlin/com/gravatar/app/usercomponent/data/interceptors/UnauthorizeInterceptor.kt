package com.gravatar.app.usercomponent.data.interceptors

import com.gravatar.app.usercomponent.domain.usecase.Logout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to analyze responses for 401 errors.
 *
 * The logout is injected as a lazy dependency to avoid a circular dependency.
 * ProfileService -> UnauthorizeInterceptor → LogoutUseCase → RealProfileRepository → ProfileService
 *
 */
class UnauthorizeInterceptor(
    private val applicationScope: CoroutineScope,
    private val logout: Lazy<Logout>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == HttpResponseCode.UNAUTHORIZED) {
            applicationScope.launch {
                logout.value.invoke()
            }
        }
        return response
    }
}
