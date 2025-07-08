package com.gravatar.app.clock

interface AppClock {
    fun now(): Long
}

internal class SystemAppClock : AppClock {
    override fun now(): Long {
        return System.currentTimeMillis()
    }
}
