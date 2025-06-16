package com.gravatar.app

import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    @Suppress("FunctionOnlyReturningConstant")
    fun getName(): String {
        return "Gravatar"
    }
}
