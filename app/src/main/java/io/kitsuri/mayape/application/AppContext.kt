package io.kitsuri.mayape.application

import android.app.Application
import io.kitsuri.mayape.manager.NativeManager
import io.kitsuri.mayape.models.TerminalViewModel

class AppContext : Application() {
    companion object {
        lateinit var instance: AppContext
            private set

        /**
         * wont work this isnt a activity lol idek why i am keeping this but ye dead code *for now*
         */
        lateinit var logger: TerminalViewModel
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        logger = TerminalViewModel().apply {
            addLog("AppContext", "INIT", "Global logger initialized")
        }
    }
}
