package com.samuelcba.jarvismobile.memory

import android.content.Context

class CommandMemory(context: Context) {
    private val preferences = context.getSharedPreferences("jarvis_memory", Context.MODE_PRIVATE)

    fun rememberCommand(command: String) {
        val current = recentCommands().toMutableList()
        current.remove(command)
        current.add(0, command)
        preferences.edit()
            .putString("recent_commands", current.take(8).joinToString(separator = "\n"))
            .apply()
    }

    fun recentCommands(): List<String> {
        return preferences.getString("recent_commands", null)
            ?.lines()
            ?.filter { it.isNotBlank() }
            .orEmpty()
    }
}
