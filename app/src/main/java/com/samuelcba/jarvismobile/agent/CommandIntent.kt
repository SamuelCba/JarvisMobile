package com.samuelcba.jarvismobile.agent

sealed class CommandIntent {
    data class OpenApp(val appName: String, val packageName: String) : CommandIntent()
    data object OpenAccessibilitySettings : CommandIntent()
    data object Unknown : CommandIntent()
}
