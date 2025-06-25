package com.gravatar.app.loginUi.presentation.login.di

import com.gravatar.app.loginUi.di.loginUiModule
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI

class LoginUiModuleTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
        loginUiModule.verify()
    }
}
