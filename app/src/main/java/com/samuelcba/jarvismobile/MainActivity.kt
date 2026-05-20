package com.samuelcba.jarvismobile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.samuelcba.jarvismobile.accessibility.JarvisAccessibilityService
import com.samuelcba.jarvismobile.agent.CommandIntent
import com.samuelcba.jarvismobile.agent.CommandParser
import com.samuelcba.jarvismobile.memory.CommandMemory
import java.util.Locale

class MainActivity : Activity() {
    private val parser = CommandParser()
    private lateinit var memory: CommandMemory
    private lateinit var statusText: TextView
    private lateinit var logText: TextView
    private lateinit var commandInput: EditText
    private var speechRecognizer: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memory = CommandMemory(this)
        setContentView(buildContentView())
        updateStatus()
        appendLog("Sistema listo. Prueba: \"abre YouTube\", \"abre Chrome\" o \"abre ajustes\".")
        memory.recentCommands().forEach { appendLog("Memoria: $it") }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        super.onDestroy()
    }

    private fun buildContentView(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(7, 11, 16))
            setPadding(28, 28, 28, 28)
        }

        val title = TextView(this).apply {
            text = "JarvisMobile"
            setTextColor(Color.WHITE)
            textSize = 28f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        statusText = TextView(this).apply {
            setTextColor(Color.rgb(136, 255, 244))
            textSize = 14f
            setPadding(0, 8, 0, 24)
        }

        commandInput = EditText(this).apply {
            hint = "Escribe una orden..."
            setHintTextColor(Color.rgb(120, 132, 150))
            setTextColor(Color.WHITE)
            setSingleLine(false)
            maxLines = 3
            imeOptions = EditorInfo.IME_ACTION_SEND
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    runTypedCommand()
                    true
                } else {
                    false
                }
            }
        }

        val runButton = actionButton("Ejecutar orden") { runTypedCommand() }
        val micButton = actionButton("Hablar") { listenForCommand() }
        val accessibilityButton = actionButton("Activar accesibilidad") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            addView(runButton, weightedButtonParams())
            addView(micButton, weightedButtonParams())
        }

        logText = TextView(this).apply {
            setTextColor(Color.rgb(222, 230, 240))
            textSize = 15f
            setLineSpacing(4f, 1f)
        }

        val logScroll = ScrollView(this).apply {
            addView(logText)
        }

        root.addView(title)
        root.addView(statusText)
        root.addView(commandInput, fullWidthParams())
        root.addView(actions, fullWidthParams())
        root.addView(accessibilityButton, fullWidthParams())
        root.addView(logScroll, LinearLayout.LayoutParams(-1, 0, 1f).apply { topMargin = 18 })
        return root
    }

    private fun actionButton(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setTextColor(Color.rgb(7, 15, 16))
            setBackgroundColor(Color.rgb(25, 211, 197))
            setOnClickListener { onClick() }
        }
    }

    private fun weightedButtonParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, -2, 1f).apply {
            setMargins(0, 12, 10, 0)
        }
    }

    private fun fullWidthParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = 12
        }
    }

    private fun runTypedCommand() {
        val command = commandInput.text.toString().trim()
        if (command.isBlank()) {
            appendLog("Escribe una orden primero.")
            return
        }
        commandInput.text.clear()
        executeCommand(command)
    }

    private fun listenForCommand() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            appendLog("Reconocimiento de voz no disponible en este dispositivo.")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 7)
            return
        }

        appendLog("Escuchando...")
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) = Unit
                override fun onBeginningOfSpeech() = Unit
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit

                override fun onError(error: Int) {
                    appendLog("No pude escuchar bien. Codigo: $error")
                }

                override fun onPartialResults(partialResults: Bundle?) = Unit

                override fun onResults(results: Bundle?) {
                    val text = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                    if (text.isBlank()) {
                        appendLog("No recibi texto de voz.")
                    } else {
                        executeCommand(text)
                    }
                }
            })
            startListening(intent)
        }
    }

    private fun executeCommand(command: String) {
        appendLog("> $command")
        memory.rememberCommand(command)

        when (val intent = parser.parse(command)) {
            is CommandIntent.OpenApp -> openApp(intent.appName, intent.packageName)
            CommandIntent.OpenAccessibilitySettings -> {
                appendLog("Abriendo ajustes de accesibilidad.")
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            CommandIntent.Unknown -> {
                appendLog("No tengo ese comando todavia. MVP siguiente: modo entrenamiento para guardarlo como rutina.")
            }
        }
    }

    private fun openApp(appName: String, packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent == null) {
            appendLog("No encontre $appName instalado o Android no lo expone.")
            return
        }

        appendLog("Abriendo $appName.")
        startActivity(launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun updateStatus() {
        val accessibility = if (JarvisAccessibilityService.active) "activa" else "pendiente"
        statusText.text = "Accesibilidad: $accessibility | Voz: manual | MVP Kotlin"
    }

    private fun appendLog(message: String) {
        logText.append("$message\n")
    }
}
