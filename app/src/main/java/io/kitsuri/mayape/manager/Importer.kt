package io.kitsuri.mayape.manager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

/**
 * Handles deep link intents for launching Minecraft
 */
class Importer : Activity() {

    private val launcherManager = LauncherManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    /**
     * Processes deep link intents and launches appropriate activity
     */
    private fun handleDeepLink(intent: Intent) {
        val newIntent = Intent(intent)

        if (isMinecraftRunning()) {
            newIntent.setClassName(this, "com.mojang.minecraftpe.Launcher")
            startActivity(newIntent)
        } else {
            launcherManager.prepareLauncher { apks ->
                newIntent.apply {
                    setClassName(this@Importer, "com.mojang.minecraftpe.Launcher")
                    putStringArrayListExtra("APKS", ArrayList(apks))
                    flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                }
                startActivity(newIntent)
            }
        }

        finish()
    }

    /**
     * Checks if Minecraft is currently running
     */
    private fun isMinecraftRunning(): Boolean {
        return try {
            Class.forName(
                "com.mojang.minecraftpe.Launcher",
                false,
                classLoader
            )
            Log.d("Importer", "Minecraft PE Launcher class found")
            true
        } catch (e: ClassNotFoundException) {
            Log.d("Importer", "Minecraft PE Launcher class not found")
            false
        }
    }
}