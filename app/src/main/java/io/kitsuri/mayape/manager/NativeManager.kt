package io.kitsuri.mayape.manager
import io.kitsuri.mayape.models.TerminalViewModel

object NativeManager {
    private var loggerViewModel: TerminalViewModel? = null

    fun initLogger(viewModel: TerminalViewModel) {
        loggerViewModel = viewModel
    }

    @JvmStatic
    fun logFromNative(threadName: String, tag: String, message: String) {
        loggerViewModel?.addLog(threadName, tag, message)
            ?: run { println("NativeManager: Logger not initialized yet") }
    }

    init {
        System.loadLibrary("Shoki")
    }

}

