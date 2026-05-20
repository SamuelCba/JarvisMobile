package com.samuelcba.jarvismobile.agent

sealed class CommandIntent {
    data class OpenApp(val appName: String, val packageName: String) : CommandIntent()
    data class TapText(val label: String) : CommandIntent()
    data class CreateFolder(val name: String?, val locationQuery: String?) : CommandIntent()
    data class CreateFile(val name: String?, val locationQuery: String?) : CommandIntent()
    data object OpenAccessibilitySettings : CommandIntent()
    data object OpenFileAccessSettings : CommandIntent()
    data object Back : CommandIntent()
    data object Home : CommandIntent()
    data object Recents : CommandIntent()
    data object ScrollUp : CommandIntent()
    data object ScrollDown : CommandIntent()
    data object VolumeUp : CommandIntent()
    data object VolumeDown : CommandIntent()
    data object BrightnessUp : CommandIntent()
    data object BrightnessDown : CommandIntent()
    data object Unknown : CommandIntent()
}
