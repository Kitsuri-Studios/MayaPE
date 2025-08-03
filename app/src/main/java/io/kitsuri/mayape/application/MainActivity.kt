package io.kitsuri.mayape.application

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.kitsuri.mayape.manager.LauncherManager
import io.kitsuri.mayape.ui.components.login.MainLoginUi
import io.kitsuri.mayape.ui.theme.MayaTheme

/**
 * Main activity for the Maya PE launcher application
 * Enhanced with professional animations and improved UI consistency
 */
class MainActivity : ComponentActivity() {

    private val launcherManager = LauncherManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemUI()

        setContent {
            MayaTheme {
                MainLoginUi()
            }
        }
    }

    /**
     * Hides system UI elements for immersive experience
     */
    private fun hideSystemUI() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }

    fun launchMinecraft() {
        launcherManager.prepareLauncher { apks ->
            startActivity(Intent().apply {
                setClassName(this@MainActivity, "com.mojang.minecraftpe.Launcher")
                putStringArrayListExtra("APKS", ArrayList(apks))
                flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            })
            finish()
        }
    }
}



