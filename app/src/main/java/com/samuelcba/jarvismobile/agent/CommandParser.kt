package com.samuelcba.jarvismobile.agent

class CommandParser {
    fun parse(rawText: String): CommandIntent {
        val text = rawText.lowercase().trim()

        if (text.contains("accesibilidad") || text.contains("accessibility")) {
            return CommandIntent.OpenAccessibilitySettings
        }

        if (text.contains("permiso archivos") || text.contains("todos los archivos")) {
            return CommandIntent.OpenFileAccessSettings
        }

        parseFileCommand(text)?.let { return it }

        parseTapText(text)?.let { return it }

        if (text == "atras" || text == "atrás" || text.contains("vuelve atras") || text.contains("volver atras")) {
            return CommandIntent.Back
        }

        if (text == "inicio" || text.contains("ir a inicio") || text.contains("pantalla principal")) {
            return CommandIntent.Home
        }

        if (text.contains("recientes") || text.contains("apps recientes")) {
            return CommandIntent.Recents
        }

        if (text.contains("baja") && (text.contains("scroll") || text.contains("pantalla") || text.contains("abajo"))) {
            return CommandIntent.ScrollDown
        }

        if ((text.contains("sube") || text.contains("arriba")) &&
            (text.contains("scroll") || text.contains("pantalla") || text.contains("arriba"))
        ) {
            return CommandIntent.ScrollUp
        }

        if ((text.contains("sube") || text.contains("aumenta")) &&
            (text.contains("volumen") || text.contains("sonido"))
        ) {
            return CommandIntent.VolumeUp
        }

        if ((text.contains("baja") || text.contains("disminuye")) &&
            (text.contains("volumen") || text.contains("sonido"))
        ) {
            return CommandIntent.VolumeDown
        }

        if ((text.contains("sube") || text.contains("aumenta")) && text.contains("brillo")) {
            return CommandIntent.BrightnessUp
        }

        if ((text.contains("baja") || text.contains("disminuye")) && text.contains("brillo")) {
            return CommandIntent.BrightnessDown
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

    private fun parseFileCommand(text: String): CommandIntent? {
        val isCreate = listOf("crea", "crear", "haz", "genera").any(text::contains)
        if (!isCreate) {
            return null
        }

        val isFolder = listOf("carpeta", "folder", "directorio").any(text::contains)
        val isFile = listOf("archivo", "file", "documento").any(text::contains)
        if (!isFolder && !isFile) {
            return null
        }

        val name = extractName(text)
        val location = extractLocation(text)
        return if (isFolder) {
            CommandIntent.CreateFolder(name, location)
        } else {
            CommandIntent.CreateFile(name, location)
        }
    }

    private fun extractName(text: String): String? {
        val markers = listOf(
            "llamada ",
            "llamado ",
            "que se llame ",
            "se llame ",
            "nombre ",
            "named ",
            "called ",
        )
        val marker = markers.firstOrNull { text.contains(it) } ?: return null
        val afterMarker = text.substringAfter(marker).trim()
        val stopMarkers = listOf(" dentro de ", " en la carpeta ", " en el folder ", " en documentos", " en descargas", " en download", " en raiz", " en la raiz")
        val stop = stopMarkers
            .map { afterMarker.indexOf(it) }
            .filter { it >= 0 }
            .minOrNull()
        return afterMarker
            .let { if (stop == null) it else it.substring(0, stop) }
            .trim(' ', '"', '\'', '.', ',')
            .takeIf { it.isNotBlank() }
    }

    private fun extractLocation(text: String): String? {
        val markers = listOf(" dentro de ", " en la carpeta ", " en el folder ", " en ")
        val marker = markers.firstOrNull { text.contains(it) } ?: return null
        return text.substringAfter(marker)
            .trim(' ', '"', '\'', '.', ',')
            .takeIf { it.isNotBlank() }
    }

    private fun parseTapText(text: String): CommandIntent.TapText? {
        val prefixes = listOf(
            "toca ",
            "tocar ",
            "presiona ",
            "pulsa ",
            "dale a ",
            "haz click en ",
        )
        val prefix = prefixes.firstOrNull { text.startsWith(it) } ?: return null
        val label = text.removePrefix(prefix).trim()
        return if (label.isBlank()) null else CommandIntent.TapText(label)
    }
}
