package io.kitsuri.mayape.application

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.kitsuri.mayape.manager.SettingsManager
import io.kitsuri.mayape.ui.components.landing.WelcomeScreen
import io.kitsuri.mayape.ui.overlay.SettingsOverlay
import io.kitsuri.mayape.ui.theme.MayaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            MayaTheme  {
                WelcomeScreen()

                val navController = rememberNavController()
                settingsManager = SettingsManager(this)

                NavHost(
                    navController = navController,
                    startDestination = "Settings_Overlay"
                ) {
                    composable("Settings_Overlay") {
                        SettingsOverlay(onNavigateAdvanceSettings = {navController.popBackStack()}, isVisible = true, settingsManager = set) // fix the settingsManager
                    }
                }
            }
        }
    }
}