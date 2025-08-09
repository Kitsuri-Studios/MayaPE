package io.kitsuri.mayape.models

import androidx.lifecycle.ViewModel
import io.kitsuri.mayape.utils.LibraryUtils.getMediaDirectory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TerminalViewModel : ViewModel() {
    private val _logs = MutableStateFlow<List<String>>(listOf(
        "[${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}] [Main]: [INFO] Terminal initialized"
    ))
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    fun addLog(threadName: String, tag: String, message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val formattedLog = "[$time] [$threadName]: [$tag] $message"
        _logs.value = _logs.value + formattedLog
    }

    fun clearLogs() {
        _logs.value = listOf(
            "[${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}] [Main]: [INFO] Terminal initialized"
        )
    }

    fun exportLogs(context: android.content.Context, fileName: String) {
        try {
            val mediaDir = getMediaDirectory(context)
            if (mediaDir == null) {
                addLog("Main", "ERROR", "Failed to access media directory")
                return
            }
            val sanitizedFileName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"
            val file = File(mediaDir, sanitizedFileName)
            file.writeText(_logs.value.joinToString("\n"))
            addLog("Main", "INFO", "Logs exported to $sanitizedFileName")
        } catch (e: Exception) {
            addLog("Main", "ERROR", "Failed to export logs: ${e.message}")
        }
    }
}