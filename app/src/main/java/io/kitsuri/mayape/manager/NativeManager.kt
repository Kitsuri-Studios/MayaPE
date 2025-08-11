package io.kitsuri.mayape.manager
import android.content.Context
import android.content.Intent
import android.net.Uri
import io.kitsuri.mayape.models.TerminalViewModel

object NativeManager {
    private var loggerViewModel: TerminalViewModel? = null

    init {
        System.loadLibrary("Shoki")
    }
    fun initLogger(viewModel: TerminalViewModel) {
        loggerViewModel = viewModel
    }

    @JvmStatic
    fun logFromNative(threadName: String, tag: String, message: String) {
        loggerViewModel?.addLog(threadName, tag, message)
            ?: run { println("NativeManager: Logger not initialized yet") }
    }

    fun initialize(viewModel: TerminalViewModel) {
        initLogger(viewModel)

    }

}