package com.samuelcba.jarvismobile.agent

class CommandParser {
    fun parse(rawText: String): CommandIntent {
        val text = rawText.lowercase().trim()

        if (text.contains("accesibilidad") || text.contains("accessibility")) {
            return CommandIntent.OpenAccessibilitySettings
        }

        val appPackages = linkedMapOf(
            "whatsapp" to "com.whatsapp",
            "youtube" to "com.google.android.youtube",
            "chrome" to "com.android.chrome",
            "google" to "com.google.android.googlequicksearchbox",
            "ajustes" to "com.android.settings",
            "configuracion" to "com.android.settings",
            "settings" to "com.android.settings",
        )

        val openWords = listOf("abre", "abrir", "open", "lanza", "inicia")
        if (openWords.any(text::contains)) {
            val match = appPackages.entries.firstOrNull { (name, _) -> text.contains(name) }
            if (match != null) {
                return CommandIntent.OpenApp(match.key, match.value)
            }
        }

        return CommandIntent.Unknown
    }
}
