package io.kitsuri.mayape.manager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import io.kitsuri.mayape.models.TerminalViewModel

/**
 * Handles deep link intents for launching Minecraft
 */
class Importer : Activity() {
    private lateinit var logger: TerminalViewModel
    private lateinit var launcherManager: LauncherManager

    override fun onCreate(savedInstanceState: Bundle?) {
        logger = TerminalViewModel()
        launcherManager = LauncherManager(this, logger)
        logger.addLog("Importer", "CHECKPOINT", "onCreate() called with savedInstanceState = $savedInstanceState")
        super.onCreate(savedInstanceState)
        logger.addLog("Importer", "INFO", "Activity created, handling deep link intent")
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        logger.addLog("Importer", "CHECKPOINT", "onNewIntent() called with intent: $intent")
        super.onNewIntent(intent)
        logger.addLog("Importer", "INFO", "Handling new deep link intent")
        handleDeepLink(intent)
    }

    /**
     * Processes deep link intents and launches appropriate activity
     */
    private fun handleDeepLink(intent: Intent) {
        logger.addLog("Importer", "CHECKPOINT", "handleDeepLink() called")
        logger.addLog("Importer", "DEBUG", "Incoming intent action: ${intent.action}")
        logger.addLog("Importer", "DEBUG", "Incoming intent data: ${intent.data}")

        val newIntent = Intent(intent)
        logger.addLog("Importer", "INFO", "Created copy of intent for forwarding")

        if (isMinecraftRunning()) {
            logger.addLog("Importer", "INFO", "Minecraft is already running — forwarding intent to running instance")
            newIntent.setClassName(this, "com.mojang.minecraftpe.Launcher")
            startActivity(newIntent)
            logger.addLog("Importer", "CHECKPOINT", "Forwarded intent to running Minecraft")
        } else {
            logger.addLog("Importer", "INFO", "Minecraft is not running — preparing launcher")
            launcherManager.prepareLauncher { apks ->
                logger.addLog("Importer", "DEBUG", "Launcher preparation complete, APKs found: ${apks.size}")
                newIntent.apply {
                    setClassName(this@Importer, "com.mojang.minecraftpe.Launcher")
                    putStringArrayListExtra("APKS", ArrayList(apks))
                    flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                }
                logger.addLog("Importer", "CHECKPOINT", "Starting Minecraft with prepared APK list")
                startActivity(newIntent)
            }
        }

        logger.addLog("Importer", "INFO", "Finishing Importer activity")
        finish()
    }

    /**
     * Checks if Minecraft is currently running
     */
    private fun isMinecraftRunning(): Boolean {
        logger.addLog("Importer", "CHECKPOINT", "Checking if Minecraft is running")
        return try {
            Class.forName(
                "com.mojang.minecraftpe.Launcher",
                false,
                classLoader
            )
            logger.addLog("Importer", "INFO", "Minecraft PE Launcher class found — running")
            true
        } catch (e: ClassNotFoundException) {
            logger.addLog("Importer", "INFO", "Minecraft PE Launcher class not found — not running")
            false
        }
    }
}
