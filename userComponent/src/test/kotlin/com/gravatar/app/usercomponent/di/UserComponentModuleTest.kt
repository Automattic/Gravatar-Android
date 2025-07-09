package com.gravatar.app.usercomponent.di

import com.gravatar.app.clock.AppClock
import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.usercomponent.data.InMemoryUserSessionPersistence
import com.gravatar.app.usercomponent.data.WordPressClient
import com.gravatar.app.usercomponent.domain.usecase.SelectAvatarUseCase
import kotlinx.coroutines.CoroutineScope
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

class UserComponentModuleTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
        userComponentModule.verify(
            injections = injectedParameters(
                definition<WordPressClient>(DispatcherProvider::class),
                definition<InMemoryUserSessionPersistence>(CoroutineScope::class, DispatcherProvider::class),
                definition<SelectAvatarUseCase>(AppClock::class)
            )
        )
    }
}
