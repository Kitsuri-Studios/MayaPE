package io.kitsuri.mayape.application

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.kitsuri.mayape.manager.LauncherManager
import io.kitsuri.mayape.ui.components.home.HomeScreen
import io.kitsuri.mayape.ui.theme.MayaTheme
import io.kitsuri.mayape.utils.LibraryUtils

class HomeActivity : ComponentActivity() {
    private val launcherManager = LauncherManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediaPath = LibraryUtils.getMediaDirectoryPath(this)
        Log.d("HomeActivity", "Media directory will be: $mediaPath")
        // Initialize files with error checking
        val initSuccess = LibraryUtils.initializeFiles(this)
        if (!initSuccess) {
            Log.e("HomeActivity", "Failed to initialize files")
            // We might want to show an error dialog here But lol Whatever This is very Unlikely to happen lmao
        } else {
            Log.d("HomeActivity", "Files initialized successfully")
        }
        // Refresh mods - this will sync the JSON file with actual files in the directory
        // and preserve user preferences for enabled/disabled states
        val refreshSuccess = LibraryUtils.refreshMods(this)
        if (refreshSuccess) {
            Log.d("HomeActivity", "Mods refreshed successfully")
            val currentMods = LibraryUtils.getAllMods(this)
            Log.d("HomeActivity", "Current mods after refresh: $currentMods")
        } else {
            Log.w("HomeActivity", "Failed to refresh mods")
        }
        // Check what files exist
        val fileStatus = LibraryUtils.checkFilesExist(this)
        Log.d("HomeActivity", "File status: $fileStatus")

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            MayaTheme {
                HomeScreen(onLaunchGame = { launchGame() })
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Refresh mods every time the app comes back to foreground
        // This ensures the mod list is always up to date if files were added/removed externally
        Log.d("HomeActivity", "App resumed, refreshing mods...")
        val refreshSuccess = LibraryUtils.refreshMods(this)
        if (refreshSuccess) {
            Log.d("HomeActivity", "Mods refreshed on resume")
        } else {
            Log.w("HomeActivity", "Failed to refresh mods on resume")
        }
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