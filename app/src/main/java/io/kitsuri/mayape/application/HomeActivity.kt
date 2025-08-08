package io.kitsuri.mayape.application

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
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

class HomeActivity : ComponentActivity() {
    private val launcherManager = LauncherManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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