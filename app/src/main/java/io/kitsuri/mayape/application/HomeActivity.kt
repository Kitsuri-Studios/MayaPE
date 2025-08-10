package io.kitsuri.mayape.application

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.kitsuri.mayape.manager.LauncherManager
import io.kitsuri.mayape.manager.SettingsManager
import io.kitsuri.mayape.models.TerminalViewModel
import io.kitsuri.mayape.ui.components.home.HomeScreen
import io.kitsuri.mayape.ui.theme.MayaTheme
import io.kitsuri.mayape.utils.LibraryUtils

class HomeActivity : ComponentActivity() {
    private val logger: TerminalViewModel by viewModels()
    private lateinit var settingsManager: SettingsManager
    private lateinit var launcherManager: LauncherManager

    private val settingsChangeListener = object : SettingsManager.SettingsChangeListener {
        override fun onSettingChanged(key: String, value: Any) {
            if (key in listOf("hxo_enabled", "hxo_dir", "hxo_sleep", "hxo_unload", "hxo_lib")) {
                val success = LibraryUtils.writeIniFile(this@HomeActivity, settingsManager)
                if (success) {
                    Log.d("HomeActivity", "HXO.ini updated due to setting change: $key = $value")
                    logger.addLog("Main", "HOME", "HXO.ini updated for $key = $value")
                } else {
                    Log.e("HomeActivity", "Failed to update HXO.ini for setting: $key")
                    logger.addLog("Main", "HOME", "Failed to update HXO.ini for $key")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsManager = SettingsManager(this)
        launcherManager = LauncherManager(this, logger)

        val mediaPath = LibraryUtils.getMediaDirectoryPath(this)
        Log.d("HomeActivity", "Media directory will be: $mediaPath")
        logger.addLog("Main", "HOME", "Media directory will be: $mediaPath")

        val initSuccess = LibraryUtils.initializeFiles(this, settingsManager, logger)
        LibraryUtils.registerIniSettings(settingsManager = settingsManager, logger)

        if (!initSuccess) {
            Log.e("HomeActivity", "Failed to initialize files")
            logger.addLog("Main", "HOME", "Failed to initialize files")
        } else {
            Log.d("HomeActivity", "Files initialized successfully")
            logger.addLog("Main", "HOME", "Files initialized successfully")
        }

        val refreshSuccess = LibraryUtils.refreshMods(this)
        if (refreshSuccess) {
            Log.d("HomeActivity", "Mods refreshed successfully")
            logger.addLog("Main", "HOME", "Mods refreshed successfully")
            val currentMods = LibraryUtils.getAllMods(this)
            Log.d("HomeActivity", "Current mods after refresh: $currentMods")
            logger.addLog("Main", "HOME", "Current mods: $currentMods")
        } else {
            Log.w("HomeActivity", "Failed to refresh mods")
            logger.addLog("Main", "HOME", "Failed to refresh mods")
        }

        val fileStatus = LibraryUtils.checkFilesExist(this)
        Log.d("HomeActivity", "File status: $fileStatus")
        logger.addLog("Main", "HOME", "File status: $fileStatus")

        // Register settings change listener
        settingsManager.addSettingsChangeListener(settingsChangeListener)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            MayaTheme {
                HomeScreen(
                    onLaunchGame = { launchGame() },
                    settingsManager = settingsManager
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeActivity", "App resumed, refreshing mods...")
        logger.addLog("Main", "HOME", "App resumed, refreshing mods...")
        val refreshSuccess = LibraryUtils.refreshMods(this)
        if (refreshSuccess) {
            Log.d("HomeActivity", "Mods refreshed on resume")
            logger.addLog("Main", "HOME", "Mods refreshed on resume")
        } else {
            Log.w("HomeActivity", "Failed to refresh mods on resume")
            logger.addLog("Main", "HOME", "Failed to refresh mods on resume")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the listener to prevent memory leaks
        settingsManager.removeSettingsChangeListener(settingsChangeListener)
    }

    private fun launchMinecraft() {
        launcherManager.prepareLauncher { apks ->
            startActivity(Intent().apply {
                setClassName(this@HomeActivity, "com.mojang.minecraftpe.Launcher")
                putStringArrayListExtra("APKS", ArrayList(apks))
                flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            })
            finish()
        }
    }

    fun launchGame() {
        launchMinecraft()
    }
}